"use strict";
var Poker = Poker || {};
/**
 * Handles tournament lobby related data for the
 * tournaments the user is currently watching
 * @type {Poker.TournamentManager}
 */
Poker.TournamentManager = Class.extend({

    /**
     * @type Poker.Map
     */
    tournaments : null,

    /**
     * @type Poker.Map
     */
    registeredTournaments : null,

    /**
     *  @type Poker.Map
     */
    tournamentTables : null,

    /**
     * @type Poker.DialogManager
     */
    dialogManager : null,

    /**
     * @type Poker.PeriodicalUpdater
     */
    tournamentUpdater : null,

    /**
     * @type Poker.TableManager
     */
    tableManager : null,

    hasBeenOpen : false,



    /**
     *
     * @param {Number} tournamentLobbyUpdateInterval
     * @constructor
     */
    init : function(tournamentLobbyUpdateInterval) {
        this.tournaments = new Poker.Map();
        this.tournamentTables = new Poker.Map();
        this.registeredTournaments = new Poker.Map();
        this.dialogManager = Poker.AppCtx.getDialogManager();
        this.tableManager = Poker.AppCtx.getTableManager();

        var self = this;
        this.tournamentUpdater = new Poker.PeriodicalUpdater(function(){
            self.updateTournamentData();
        },tournamentLobbyUpdateInterval);
    },
    createTournament : function(id, name) {
        this.hasBeenOpen = true;
        var viewManager = Poker.AppCtx.getViewManager();
        if(this.getTournamentById(id)!=null) {
            viewManager.activateViewByTournamentId(id);
        } else {
            var self = this;
            var viewContainer = $(".view-container");

            var layoutManager = new Poker.TournamentLayoutManager(id, name, this.isRegisteredForTournament(id),
                viewContainer,function(){
                        new Poker.TournamentRequestHandler(id).unsubscribeFromChat();
                        self.removeTournament(id);
                    }
            );
            viewManager.addTournamentView(layoutManager.getViewElementId(), name, layoutManager);

            this.tournaments.put(id,new Poker.Tournament(id, name, layoutManager));
            var trh = new Poker.TournamentRequestHandler(id);
            trh.requestTournamentInfo();
            trh.subscribeToChat();
            this.activateTournamentUpdates(id);
            this.tournamentUpdater.start();
        }
    },
    onRemovedFromTournament : function(tableId,keepWatching) {
        console.log("On removed from tournament table " + tableId + " keep watching = " + keepWatching);

        this.tableManager.updatePlayerStatus(tableId,Poker.MyPlayer.id,Poker.PlayerTableStatus.TOURNAMENT_OUT);
        this.tableManager.removePlayer(tableId, Poker.MyPlayer.id);

        if(keepWatching==false) {
            this.tableManager.leaveTable(tableId,false);
        }


    },
    setTournamentTable : function(tournamentId, tableId) {
        if(this.tournamentTables.contains(tournamentId)) {
            this.tournamentTables.get(tournamentId).push(tableId);
        } else {
            var tables = [];
            tables.push(tableId);
            this.tournamentTables.put(tournamentId,tables);
        }
    },
    onChatMessage : function(tournamentId, screenName, message) {
        var tournament = this.getTournamentById(tournamentId);
        if(tournament!=null) {
            tournament.tournamentLayoutManager.onChatMessage(screenName,Poker.Utils.filterMessage(message));
        }
    },
    isTournamentTable : function(tableId) {
        var tables = this.tournamentTables.values();
        for(var i = 0; i<tables.length;i++) {
            for(var j = 0; j<tables[i].length; j++) {
                if(tables[i][j]===tableId) {
                    return true;
                }
            }
        }
        return false;
    },
    removeTournament : function(tournamentId) {
        var tournament = this.tournaments.remove(tournamentId);
        if (tournament!=null) {
            new Poker.TournamentRequestHandler(tournamentId).unsubscribeFromChat();
            if (this.tournaments.size() == 0) {
                console.log("Stopping updates of lobby for tournament: " + tournamentId);
                this.tournamentUpdater.stop();
            }
        }
    },
    onPlayerLoggedIn : function(reconnecting) {
        if(reconnecting==false) {
            var tournaments = this.tournaments.values();
            for(var i = 0; i<tournaments.length; i++){
                var t = tournaments[i];
                new Poker.TournamentRequestHandler(t.id).leaveTournamentLobby();
                this.createTournament(t.id,t.name);
            }
        }
    },
    /**
     * @param id
     * @return {Poker.Tournament}
     */
    getTournamentById : function(id) {
        return this.tournaments.get(id);
    },
    /**
     * @param {Number} tournamentId
     * @param {com.cubeia.games.poker.io.protocol.TournamentLobbyData} tournamentData
     */
    handleTournamentLobbyData : function(tournamentId, tournamentData) {
        var tournament = this.getTournamentById(tournamentId);
        this.handlePlayerList(tournament,tournamentData.players);
        this.handleTableList(tournament,tournamentData.tournamentTables.tables);
        this.handleBlindsStructure(tournament,tournamentData.blindsStructure);
        this.handlePayoutInfo(tournament,tournamentData.payoutInfo);
        $.extend(tournamentData.tournamentInfo, { prizePool: tournamentData.payoutInfo.prizePool });
        this.handleTournamentInfo(tournament, tournamentData.tournamentInfo);
        if (this.isTournamentRunning(tournamentData.tournamentInfo.tournamentStatus)) {
            this.handleTournamentStatistics(tournament, tournamentData.tournamentStatistics);
        } else {
            tournament.tournamentLayoutManager.hideTournamentStatistics();
        }
    },
    handleTableList : function(tournament,tables){
        tournament.tournamentLayoutManager.updateTableList(tables);
    },
    handlePlayerList : function(tournament,playerList) {
        var players = [];
        if(playerList) {
           players = playerList.players;
        }
        tournament.tournamentLayoutManager.updatePlayerList(players);
    },
    handleBlindsStructure : function(tournament,blindsStructure) {
        tournament.tournamentLayoutManager.updateBlindsStructure(blindsStructure);
    },
    handlePayoutInfo : function(tournament, payoutInfo) {
        tournament.tournamentLayoutManager.updatePayoutInfo(payoutInfo);
    },
    handleTournamentStatistics : function(tournament,statistics) {
        tournament.tournamentLayoutManager.updateTournamentStatistics(statistics);
    },
    handleRegistrationSuccessful : function(tournamentId) {
        this.registeredTournaments.put(tournamentId,true);
        var tournament = this.tournaments.get(tournamentId);
        if(tournament!=null) {
            tournament.tournamentLayoutManager.setPlayerRegisteredState();
        }
        this.dialogManager.displayGenericDialog({
            tournamentId : tournamentId,
            header:i18n.t("dialogs.tournament-register-success.header"),
            message:i18n.t("dialogs.tournament-register-success.message", { sprintf : [tournamentId]})
        });

    },
    /**
     * @param {Poker.Tournament} tournament
     * @param {com.cubeia.games.poker.io.protocol.TournamentInfo} info
     */
    handleTournamentInfo : function(tournament, info) {
        var view = Poker.AppCtx.getViewManager().findViewByTournamentId(tournament.id);
        if(view!=null){
            view.updateName(info.tournamentName);
        }
        tournament.tournamentLayoutManager.updateTournamentInfo(info);
        var registered = this.registeredTournaments.contains(tournament.id);
        if (this.isTournamentRunning(info.tournamentStatus)) {
            tournament.tournamentLayoutManager.setTournamentNotRegisteringState(registered);
        } else if (info.tournamentStatus != com.cubeia.games.poker.io.protocol.TournamentStatusEnum.REGISTERING) {
            tournament.tournamentLayoutManager.setTournamentNotRegisteringState(false);

        } else if (registered == true) {
            tournament.tournamentLayoutManager.setPlayerRegisteredState();
        } else {
            tournament.tournamentLayoutManager.setPlayerUnregisteredState();
        }
        // TODO: we could update the name here, at least if it's the dummy name (Tourney). (I tried, but couldn't figure out the Mustache stuff.)
        // tournament.tournamentLayoutManager.updateName(info.tournamentName);
    },
    handleRegistrationFailure : function(tournamentId) {
        this.dialogManager.displayGenericDialog({
            tournamentId : tournamentId,
            header: i18n.t("dialogs.tournament-register-failure.header"),
            message: i18n.t("dialogs.tournament-register-failure.message", { sprintf : [tournamentId]})
        });

    },
    handleUnregistrationSuccessful : function(tournamentId) {
        this.registeredTournaments.remove(tournamentId);
        var tournament = this.tournaments.get(tournamentId);
        if(tournament!=null) {
            tournament.tournamentLayoutManager.setPlayerUnregisteredState();
        }
        this.dialogManager.displayGenericDialog({
            tournamentId : tournamentId,
            header:i18n.t("dialogs.tournament-unregister-success.header"),
            message:i18n.t("dialogs.tournament-unregister-success.message", { sprintf : [tournamentId]})
        });

    },
    handleUnregistrationFailure : function(tournamentId) {
        this.dialogManager.displayGenericDialog({
            tournamentId : tournamentId,
            header:i18n.t("dialogs.tournament-unregister-failure.header"),
            message:i18n.t("dialogs.tournament-unregister-failure.message", { sprintf : [tournamentId]})
        });

    },
    isRegisteredForTournament : function(tournamentId) {
        return this.registeredTournaments.get(tournamentId) != null;
    },
    activateTournamentUpdates : function(tournamentId) {
        var tournament = this.tournaments.get(tournamentId);
        if (tournament != null) {
            tournament.updating = true;
        }
        this.tournamentUpdater.rushUpdate();
    },
    deactivateTournamentUpdates : function(tournamentId) {
        var tournament = this.tournaments.get(tournamentId);
        if (tournament != null) {
            tournament.updating = false;
        }
    },
    updateTournamentData : function() {
        var tournaments =  this.tournaments.values();
        for (var i = 0; i < tournaments.length; i++) {
            if (tournaments[i].updating == true && tournaments[i].finished == false) {
                console.log("found updating tournament retrieving tournament data");
                new Poker.TournamentRequestHandler(tournaments[i].id).requestTournamentInfo();
            }
        }
    },
    openTournamentLobbies : function(tournamentIds) {
        if(this.registeredTournaments.size()>0){
            var tourneys = this.registeredTournaments.keys();
            for(var i = 0; i<tourneys.length; i++){
                var t = tourneys[i];
                if(tournamentIds.indexOf(parseInt(t))==-1){
                    this.registeredTournaments.remove(t);
                    var t  = this.tournaments.get(t);
                    if(t!=null){
                        t.tournamentLayoutManager.setPlayerRegisteredState();
                        t.tournamentLayoutManager.setPlayerUnregisteredState();
                        var trh = new Poker.TournamentRequestHandler(t.id);
                        trh.requestTournamentInfo();
                        trh.subscribeToChat();
                    }
                }
            }
        }
        var isReconnect = this.hasBeenOpen;
        //TODO: the name of the tournament needs to be fetched from somewhere!
        for (var i = 0; i < tournamentIds.length; i++) {
            if(!this.registeredTournaments.contains(tournamentIds[i])) {
                if(isReconnect==false || this.tournaments.contains(tournamentIds[i])){
                    this.registeredTournaments.put(tournamentIds[i],true);
                    this.createTournament(tournamentIds[i],"Tourney");
                }
            }
        }
    },
    tournamentFinished : function(tournamentId) {
        var tournament = this.tournaments.get(tournamentId);
        if(tournament!=null){
            console.log("Tournament finished rushing update");
            tournament.finished = true;
            new Poker.TournamentRequestHandler(tournamentId).requestTournamentInfo();
        } else {
            console.log("Tournament finished but not found");
        }
    },
    /**
     * Checks if this tournament is running (which it is if the status is running, on_break or preparing_break).
     * @param {Number} status
     * @return {boolean}
     */
    isTournamentRunning : function(status) {
        var running = com.cubeia.games.poker.io.protocol.TournamentStatusEnum.RUNNING;
        var onBreak = com.cubeia.games.poker.io.protocol.TournamentStatusEnum.ON_BREAK;
        var preparingForBreak = com.cubeia.games.poker.io.protocol.TournamentStatusEnum.PREPARING_BREAK;
        return status == running || status == onBreak || status == preparingForBreak;
    },
    onBuyInInfo : function(tournamentId, buyIn, fee, currency, balanceInWallet, sufficientFunds) {
        var tournament = this.getTournamentById(tournamentId);
        if (sufficientFunds == true) {
            tournament.tournamentLayoutManager.showBuyInInfo(buyIn,fee,currency,balanceInWallet);
        } else {
            this.dialogManager.displayGenericDialog({
                translationKey : "not-enough-funds"
            })
        }
    },
    handleTournamentId : function(id) {
        if(id!=-1) {
            this.createTournament(id, "tournament");
        }  else {
            this.dialogManager.displayGenericDialog({
                translationKey : "tournament-not-found"
            });
        }
    },
    openTournamentLobbyByName : function(name) {
        var request = new com.cubeia.games.poker.routing.service.io.protocol.TournamentIdRequest();
        request.name = name;
        var packet = new FB_PROTOCOL.ServiceTransportPacket();
        packet.pid = Poker.MyPlayer.id;
        packet.seq = 0;
        packet.idtype = 0; // namespace
        packet.service = "com.cubeia.poker:player-service";
        packet.servicedata = FIREBASE.ByteArray.toBase64String(request.save().createServiceDataArray(request.classId()));
        Poker.AppCtx.getConnector().sendProtocolObject(packet);
    }
});