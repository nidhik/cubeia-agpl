var Poker = Poker || {};

/**
 * Handles Firebase communications, packages received
 * are delegated to the different *PacketHandler classes
 *
 * @type {Poker.CommunicationManager}
 */
Poker.CommunicationManager = Class.extend({

    /**
     * @type FIREBASE.Connector
     */
    connector : null,

    webSocketUrl : null,
    webSocketPort : null,
    secure : false,
    ignoreNextForceLogout : false,

    /**
     * @type Poker.TableManager
     */
    tableManager : null,

    /**
     *
     * @param {String} webSocketUrl
     * @param {Number} webSocketPort
     * @constructor
     */
    init : function(webSocketUrl, webSocketPort,secure) {
        this.secure = secure || false;
        this.webSocketUrl = webSocketUrl;
        this.webSocketPort = webSocketPort;
        this.tableManager = Poker.AppCtx.getTableManager();
        this.connect();
    },
    /**
     *
     * @return {FIREBASE.Connector}
     */
    getConnector : function() {
        return this.connector;
    },
    /**
     * Callback for lobby packages
     * @param protocolObject
     */
    lobbyCallback : function(protocolObject) {
        var lobbyPacketHandler = new Poker.LobbyPacketHandler();
        switch (protocolObject.classId) {
            case FB_PROTOCOL.TableSnapshotListPacket.CLASSID :
                lobbyPacketHandler.handleTableSnapshotList(protocolObject.snapshots);
                break;
            case FB_PROTOCOL.TableUpdateListPacket.CLASSID :
                lobbyPacketHandler.handleTableUpdateList(protocolObject.updates);
                break;
            case FB_PROTOCOL.TableRemovedPacket.CLASSID :
                lobbyPacketHandler.handleTableRemoved(protocolObject.tableid);
                break;
            case FB_PROTOCOL.TournamentSnapshotListPacket.CLASSID :
                lobbyPacketHandler.handleTournamentSnapshotList(protocolObject.snapshots);
                break;
            case FB_PROTOCOL.TournamentUpdateListPacket.CLASSID :
                lobbyPacketHandler.handleTournamentUpdates(protocolObject.updates);
                break;
            case FB_PROTOCOL.TournamentRemovedPacket.CLASSID:
                lobbyPacketHandler.handleTournamentRemoved(protocolObject.mttid);
                break;
        }
    },
    setIgnoreNextForceLogout : function() {
        this.ignoreNextForceLogout = true;
    },
    logoutAndDisconnect : function() {
        this.getConnector().logout(true);
        this.getConnector().close();
        this.connected = false;
    },
    forceLogout : function(packet) {
        console.log("Forcing log out");
        console.log(packet);
        if(this.ignoreNextForceLogout == true) {
            console.log("ignoring taking action on force logout");
            this.ignoreNextForceLogout = false;
        } else {
            new Poker.ConnectionPacketHandler().handleForceLogout(packet.code,packet.message);
            this.getConnector().getIOAdapter().unregisterHandlers();
        }

    },
    /**
     * Login callback
     * @param {FIREBASE.ConnectionStatus} status
     * @param {Number} playerId
     * @param {String} name
     */
    loginCallback : function(status,playerId,name, credentials) {
       new Poker.ConnectionPacketHandler().handleLogin(status,playerId,name,credentials);
    },
    retryCount : 0,
    /**
     * Callback for connection status
     * @param {FIREBASE.ConnectionStatus} status
     */
    statusCallback : function(status) {
        new Poker.ConnectionPacketHandler().handleStatus(status);
    },


    /**
     * Sets up callbacks and connects to Firebase
     */
    connect : function () {
        console.log("Connecting");
        if(this.connector!=null) {
            console.log("Unregistering handlers");
            this.connector.getIOAdapter().unregisterHandlers();
        }
        var self = this;
        FIREBASE.ReconnectStrategy.MAX_ATTEMPTS = 0;




        this.connector = new CustomConnector(
            function(po) {
                Poker.AppCtx.getConnectionManager().onPacketReceived();
                self.handlePacket(po);
            },
            function(po){
                Poker.AppCtx.getConnectionManager().onPacketReceived();
                self.lobbyCallback(po);
            },
            function(status, playerId, name, credentials){
                self.loginCallback(status,playerId,name,credentials);
            },
            function(status){
                self.statusCallback(status);
            });


        console.log("Connector connect: ", this.webSocketUrl, this.webSocketPort);
        
        var useCometd = $.url().param("cometd") != undefined;
        var isSafari5 = function isSafari5() {
            var safari =  !!navigator.userAgent.match(' Safari/') && !navigator.userAgent.match(' Chrom') && !!navigator.userAgent.match(' Version/5.');
            if(safari) {
                console.log("Safari 5.x detected ");
            }
            return safari;
        };
        
        if (useCometd || isSafari5()) {
            console.log("Using cometd adapter as fallback");
            this.connector.connect("FIREBASE.CometdAdapter", this.webSocketUrl, this.webSocketPort, "cometd", this.secure, function() {
            	org.cometd.JSON.toJSON = JSON.stringify;
            	org.cometd.JSON.fromJSON = JSON.parse;
                var cometd = new org.cometd.Cometd();
                cometd.registerTransport("long-polling", new LongPollingTransportImpl());
                return cometd
            });
        } else {
        	console.log("Using websocket transport");
           this.connector.connect("FIREBASE.WebSocketAdapter", this.webSocketUrl, this.webSocketPort, "socket",this.secure);
        }
    },

    /**
     * Calls the connectors login function
     * @param {String} username
     * @param {String} password
     */
    doLogin : function(username,password) {
        Poker.MyPlayer.password = password;
        
        if (Poker.MyPlayer.pureToken) {
        	console.log("pure token");
        	var tokenArray = utf8.toByteArray(Poker.MyPlayer.loginToken);
        	this.connector.login("", "", Poker.SkinConfiguration.operatorId, tokenArray);
        } else {
        	this.connector.login(username, password, Poker.SkinConfiguration.operatorId);
        }
    },
    

    handlePacket : function (packet) {
        var tournamentId = -1;
        if(packet.mttid) {
            tournamentId = packet.mttid;
        }
        var tournamentPacketHandler = new Poker.TournamentPacketHandler(tournamentId);
        var tableId = -1;
        if(packet.tableid) {
            tableId = packet.tableid;
        }

        var tablePacketHandler = new Poker.TablePacketHandler(tableId);
        switch (packet.classId) {
            case FB_PROTOCOL.SystemMessagePacket.CLASSID:
                Poker.AppCtx.getDialogManager().displayGenericDialog({
                    header : "System Message",
                    message : packet.message
                });
                break;
            case FB_PROTOCOL.TableChatPacket.CLASSID:
                tablePacketHandler.handleChatMessage(packet);
                break;
            case FB_PROTOCOL.NotifyJoinPacket.CLASSID :
                tablePacketHandler.handleNotifyJoin(packet);
                break;
            case FB_PROTOCOL.NotifyLeavePacket.CLASSID :
                tablePacketHandler.handleNotifyLeave(packet);
                break;
            case FB_PROTOCOL.SeatInfoPacket.CLASSID :
                tablePacketHandler.handleSeatInfo(packet);
                break;
            case FB_PROTOCOL.JoinResponsePacket.CLASSID :
                tablePacketHandler.handleJoinResponse(packet);
                break;
            case FB_PROTOCOL.GameTransportPacket.CLASSID :
                this.handleGameDataPacket(packet);
                break;
            case FB_PROTOCOL.UnwatchResponsePacket.CLASSID:
                tablePacketHandler.handleUnwatchResponse(packet);
                break;
            case FB_PROTOCOL.LeaveResponsePacket.CLASSID:
                tablePacketHandler.handleLeaveResponse(packet);
                break;
            case FB_PROTOCOL.WatchResponsePacket.CLASSID:
                tablePacketHandler.handleWatchResponse(packet);
                break;
            case FB_PROTOCOL.NotifySeatedPacket.CLASSID:
                if(packet.mttid==-1) {
                    tablePacketHandler.handleSeatedAtTable(packet);
                } else {
                    tournamentPacketHandler.handleSeatedAtTournamentTable(packet);
                }
                break;
            case FB_PROTOCOL.MttSeatedPacket.CLASSID:
                tournamentPacketHandler.handleSeatedAtTournamentTable(packet);
                break;
            case FB_PROTOCOL.MttRegisterResponsePacket.CLASSID:
                tournamentPacketHandler.handleRegistrationResponse(packet);
                break;
            case FB_PROTOCOL.MttUnregisterResponsePacket.CLASSID:
                tournamentPacketHandler.handleUnregistrationResponse(packet);
                break;
            case FB_PROTOCOL.MttTransportPacket.CLASSID:
                tournamentPacketHandler.handleTournamentTransport(packet);
                break;
            case FB_PROTOCOL.MttPickedUpPacket.CLASSID:
                tournamentPacketHandler.handleRemovedFromTournamentTable(packet);
                break;
            case FB_PROTOCOL.LocalServiceTransportPacket.CLASSID:
                this.handleLocalServiceTransport(packet);
                break;
            case FB_PROTOCOL.NotifyRegisteredPacket.CLASSID:
                tournamentPacketHandler.handleNotifyRegistered(packet);
                break;
            case FB_PROTOCOL.PingPacket.CLASSID:
                console.log("PING PACKET RECEIVED");
                break;
            case FB_PROTOCOL.ForcedLogoutPacket.CLASSID:
                this.forceLogout(packet);
                break;
            case FB_PROTOCOL.ServiceTransportPacket.CLASSID:
                this.handleServicePacket(packet);
                break;
            case FB_PROTOCOL.VersionPacket.CLASSID:
                Poker.AppCtx.getPingManager().versionPacketReceived();
                break;
            case FB_PROTOCOL.NotifyChannelChatPacket.CLASSID:
                tournamentPacketHandler.handleChatMessage(packet);
                break;
            case FB_PROTOCOL.JoinChatChannelResponsePacket.CLASSID:
                break;
            default :
                console.log("NO HANDLER");
                console.log(packet);
                break;
        }
    },

    handleLocalServiceTransport : function(packet) {
        var byteArray = FIREBASE.ByteArray.fromBase64String(packet.servicedata);
        var message = utf8.fromByteArray(byteArray);
        var config = JSON.parse(message);
        Poker.OperatorConfig.populate(config);
        console.log(config);
        Poker.AppCtx.getConnectionManager().onSettingsLoaded();
    },

    handleServicePacket:function (servicePacket) {
        console.log("SERVICE PACKET = ", servicePacket);
        var valueArray =  FIREBASE.ByteArray.fromBase64String(servicePacket.servicedata);
        var serviceData = new FIREBASE.ByteArray(valueArray);
        var length = serviceData.readInt();
        var classId = serviceData.readUnsignedByte();
        var protocolObject = com.cubeia.games.poker.routing.service.io.protocol.ProtocolObjectFactory.create(classId, serviceData);

        switch (protocolObject.classId() ) {
            case com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHandIds.CLASSID:
                new Poker.ServicePacketHandler().handleHandIds(protocolObject.tableId, protocolObject.handIds);
                break;
            case com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHandSummaries.CLASSID:
                new Poker.ServicePacketHandler().handleHandSummaries(protocolObject.tableId, protocolObject.handSummaries);
                break;
            case com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHands.CLASSID:
                new Poker.ServicePacketHandler().handleHands(protocolObject.tableId, protocolObject.hands);
                break;
            case com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHand.CLASSID:
                new Poker.ServicePacketHandler().handleHand(protocolObject.hand);
                break;
            case com.cubeia.games.poker.routing.service.io.protocol.TournamentIdResponse.CLASSID:
                Poker.AppCtx.getTournamentManager().handleTournamentId(protocolObject.id);
                break;
            case com.cubeia.games.poker.routing.service.io.protocol.PokerProtocolMessage.CLASSID:
                console.log("SP = ", protocolObject);
                this.handleGameData(-1,servicePacket.pid,protocolObject.packet,false);
                break;
        }
    },
    handleGameDataPacket:function (gameTransportPacket) {
        if(Poker.Settings.isEnabled(Poker.Settings.Param.FREEZE_COMMUNICATION,null)==true) {
            return;
        }
        if(!this.tableManager.tableExist(gameTransportPacket.tableid)) {
            console.log("Received packet for table (" + gameTransportPacket.tableid + ") you're not viewing");
            return;
        }
        var tableId = gameTransportPacket.tableid;
        var playerId = gameTransportPacket.pid;
        this.handleGameData(playerId,tableId,gameTransportPacket.gamedata,true);

    },
    handleGameData : function(playerId,tableId, packet,base64Encoded) {
        var gameData = null;
        if(base64Encoded==true) {
            var valueArray =  FIREBASE.ByteArray.fromBase64String(packet);
            gameData = new FIREBASE.ByteArray(valueArray);
        } else {
            gameData = new FIREBASE.ByteArray(packet);
        }


        var length = gameData.readInt();
        var classId = gameData.readUnsignedByte();

        var protocolObject = com.cubeia.games.poker.io.protocol.ProtocolObjectFactory.create(classId, gameData);

        var pokerPacketHandler = new Poker.PokerPacketHandler(tableId);

        switch (protocolObject.classId() ) {
            case com.cubeia.games.poker.io.protocol.GameState.CLASSID:
                console.log(protocolObject.capacity);
                this.tableManager.notifyGameStateUpdate(tableId,protocolObject.capacity, protocolObject.currentLevel, protocolObject.secondsToNextLevel,protocolObject.betStrategy, protocolObject.variant,protocolObject.currency, protocolObject.name);
                break;
            case com.cubeia.games.poker.io.protocol.BestHand.CLASSID:
                this.tableManager.updateHandStrength(tableId,protocolObject,false);
                break;
            case com.cubeia.games.poker.io.protocol.BuyInInfoRequest.CLASSID:
                console.log("UNHANDLED PO BuyInInfoRequest");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.BuyInInfoResponse.CLASSID:
                pokerPacketHandler.handleBuyIn(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.BuyInResponse.CLASSID:
                console.log("BUY-IN RESPONSE ");
                console.log(protocolObject);
                this.tableManager.handleBuyInResponse(tableId,protocolObject.resultCode);
                break;
            case com.cubeia.games.poker.io.protocol.CardToDeal.CLASSID:
                console.log("UNHANDLED PO CardToDeal");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.DealerButton.CLASSID:
                this.tableManager.setDealerButton(tableId,protocolObject.seat);
                break;
            case com.cubeia.games.poker.io.protocol.DealPrivateCards.CLASSID:
                pokerPacketHandler.handleDealPrivateCards(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.DealPublicCards.CLASSID:
                pokerPacketHandler.handleDealPublicCards(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.DeckInfo.CLASSID:
                console.log("UNHANDLED PO DeckInfo");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.ErrorPacket.CLASSID:
                console.log("UNHANDLED PO ErrorPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.ExposePrivateCards.CLASSID:
                pokerPacketHandler.handleExposePrivateCards(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.ExternalSessionInfoPacket.CLASSID:
                console.log("UNHANDLED PO ExternalSessionInfoPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.FuturePlayerAction.CLASSID:
                console.log("UNHANDLED PO FuturePlayerAction");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.GameCard.CLASSID:
                console.log("UNHANDLED PO GameCard");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.HandCanceled.CLASSID:
                console.log("UNHANDLED PO HandCanceled");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.HandEnd.CLASSID:
                this.tableManager.endHand(tableId,protocolObject.hands,protocolObject.potTransfers);
                break;
            case com.cubeia.games.poker.io.protocol.InformFutureAllowedActions.CLASSID:
                pokerPacketHandler.handleFuturePlayerAction(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PerformAction.CLASSID:
                pokerPacketHandler.handlePerformAction(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PingPacket.CLASSID:
                console.log("UNHANDLED PO PingPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerAction.CLASSID:
                console.log("UNHANDLED PO PlayerAction");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerBalance.CLASSID:
                pokerPacketHandler.handlePlayerBalance(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerDisconnectedPacket.CLASSID:
                console.log("UNHANDLED PO PlayerDisconnectedPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerHandStartStatus.CLASSID:
                pokerPacketHandler.handlePlayerHandStartStatus(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerPokerStatus.CLASSID:
                pokerPacketHandler.handlePlayerPokerStatus(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerReconnectedPacket.CLASSID:
                console.log("UNHANDLED PO PlayerReconnectedPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerState.CLASSID:
                console.log("UNHANDLED PO PlayerState");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PongPacket.CLASSID:
                console.log("UNHANDLED PO PongPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PotTransfers.CLASSID:
                pokerPacketHandler.handlePotTransfers(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.RakeInfo.CLASSID:
                break;
            case com.cubeia.games.poker.io.protocol.RequestAction.CLASSID:
                pokerPacketHandler.handleRequestAction(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.RebuyOffer.CLASSID:
                console.log("Received rebuy offer!");
                pokerPacketHandler.handleRebuyOffer(protocolObject, playerId);
                break;
            case com.cubeia.games.poker.io.protocol.AddOnOffer.CLASSID:
                console.log("Add-ons offered.");
                console.log(protocolObject);
                pokerPacketHandler.handleAddOnOffer(protocolObject, Poker.MyPlayer.id);
                break;
            case com.cubeia.games.poker.io.protocol.AddOnPeriodClosed.CLASSID:
                console.log("Add-on period closed.");
                pokerPacketHandler.handleAddOnPeriodClosed(Poker.MyPlayer.id);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerPerformedRebuy.CLASSID:
                pokerPacketHandler.handleRebuyPerformed(playerId);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerPerformedAddOn.CLASSID:
                pokerPacketHandler.handleAddOnPerformed(playerId);
                break;
            case com.cubeia.games.poker.io.protocol.StartHandHistory.CLASSID:

                break;
            case com.cubeia.games.poker.io.protocol.HandStartInfo.CLASSID:
                this.tableManager.startNewHand(tableId, protocolObject.handId);
                break;
            case com.cubeia.games.poker.io.protocol.StopHandHistory.CLASSID:
                break;
            case com.cubeia.games.poker.io.protocol.TakeBackUncalledBet.CLASSID:

                break;
            case com.cubeia.games.poker.io.protocol.WaitingToStartBreak:
                this.tableManager.notifyWaitingToStartBreak(tableId);
                break;
            case com.cubeia.games.poker.io.protocol.BlindsAreUpdated.CLASSID:
                console.log("Blinds updated seconds to next", protocolObject);
                this.tableManager.notifyBlindsUpdated(tableId, protocolObject.level, null, protocolObject.secondsToNextLevel);
                break;
            case com.cubeia.games.poker.io.protocol.TournamentDestroyed.CLASSID:
                this.tableManager.notifyTournamentDestroyed(tableId);
                break;
            case com.cubeia.games.poker.io.protocol.AchievementNotificationPacket.CLASSID:
                console.log(protocolObject);
                new Poker.AchievementPacketHandler(tableId).handleAchievementNotification(protocolObject.playerId, protocolObject.message);
                break;
            default:
                console.log("Ignoring packet");
                console.log(protocolObject);
                break;
        }
    }
});

var CustomConnector = function(a,b,c,d) {
    this.sup = FIREBASE.Connector;
    this.sup(a,b,c,d);

    var self = this;
    var superSendProtocolObject = this.sendProtocolObject;
    this.sendProtocolObject = function(po) {
        if(po.classId() != FB_PROTOCOL.VersionPacket.CLASSID) {
            Poker.AppCtx.getConnectionManager().onUserActivity();
        }
        superSendProtocolObject.call(self,po);
    };
};

var LongPollingTransportImpl = function() {
    var _super = new org.cometd.LongPollingTransport();
    var that = org.cometd.Transport.derive(_super);
    
    var _setHeaders = function(xhr, headers) {
        var headerName;
        if (headers) {
            for (headerName in headers) {
                if (headerName.toLowerCase() === "content-type") {
                    continue
                }
                xhr.setRequestHeader(headerName, headers[headerName])
            }
        }
    };                
    
    that.xhrSend = function(packet) {
        return $.ajax({url: packet.url, async: packet.sync !== true, type: "POST", contentType: "application/json;charset=UTF-8", data: packet.body,withCredentials: true, 
        	beforeSend: function(xhr) {
                _setHeaders(xhr, packet.headers);
                return true
            }, success: packet.onSuccess,error: function(xhr, reason, exception) {
                packet.onError(reason, exception)
            }
          }
        );
    };
    return that;
};

FIREBASE.WebSocketAdapter.prototype.unregisterHandlers  = function() {
    var _socket = this.getSocket();
    if(_socket!=null) {
        _socket.onopen = null;
        _socket.onmessage = null;
        _socket.onclose = null;
    }
};

