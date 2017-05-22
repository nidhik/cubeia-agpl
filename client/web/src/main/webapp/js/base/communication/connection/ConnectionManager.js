var Poker = Poker || {};


Poker.ConnectionManager = Class.extend({

    MAX_RECONNECT_ATTEMPTS : 15,

    retryCount : 0,

    /**
     * time out since last packet received to check for disconnect
     */
    disconnectCheckTimeout : null,

    /**
     * version packet grace timeout that triggers the reconnecting
     */
    startReconnectingGraceTimeout : null,

    /**
     * timeout for reconnect attempts
     */
    reconnectRetryTimeout : null,

    connected : false,

    /**
     * @type {Poker.DisconnectDialog}
     */
    disconnectDialog : null,

    lastActivity : null,
    activityCheckTimer : null,
    maxInactivityTime : 1800000, //30 MIN

    initialization : {
        resources : false,
        settings : false
    },
    reconnecting : false,

    init : function() {
        this.disconnectDialog = new Poker.DisconnectDialog();

        this.startActivityCheck();

    },
    startActivityCheck : function() {
        var self = this;
        this.onUserActivity();
        this.activityCheckTimer = setInterval(function(){
            var now = new Date().getTime();
            var inactiveTime = now - self.lastActivity
            if(inactiveTime>self.maxInactivityTime) {
                self.clearTimeouts();
                var cm = Poker.AppCtx.getCommunicationManager();
                cm.setIgnoreNextForceLogout();
                cm.logoutAndDisconnect();
                Poker.AppCtx.getDialogManager().displayGenericDialog(
                    { header : "Logged out", message :"You've been logged out due to inactivity"},
                    function(){
                        document.location.reload();
                    }
                );
                return;
            } else {
                console.log("User last activity = " + inactiveTime);
            }
        },60000);
    },
    onUserActivity : function() {
        this.lastActivity = new Date().getTime();
    },
    onUserLoggedIn : function(playerId, name, token) {
        console.log("onUserLoggedIn reconnecting = " + this.reconnecting);
        Poker.MyPlayer.onLogin(playerId,name, token);
        Poker.AppCtx.getNavigation().onLoginSuccess(this.reconnecting);
        Poker.AppCtx.getAccountPageManager().onLogin(playerId,name);
        if(this.reconnecting==false) {
            $('#loginView').hide();
            $("#lobbyView").show();
        }
        Poker.AppCtx.getLobbyManager().onLogin(this.reconnecting)
        if(!this.reconnecting) {
            var viewManager = Poker.AppCtx.getViewManager();
            viewManager.onLogin();
        }
        Poker.AppCtx.getTableManager().onPlayerLoggedIn(this.reconnecting);
        Poker.AppCtx.getTournamentManager().onPlayerLoggedIn(this.reconnecting);
        this.reconnecting = false;
        if(token!=null) {
            Poker.Utils.storeUser(name,Poker.MyPlayer.password);
        }

        // check deposit return...
        var depositType = purl().fparam("deposit");
        if(depositType) {
            document.location.hash = "";
            Poker.Utils.depositReturn(depositType);
        } 
    },
    onUserConnected : function() {
        this.connected = true;
        this.scheduleDisconnectCheck();
        this.retryCount = 0;
        this.disconnectDialog.close();
        this.showConnectStatus(i18n.t("login.connected"));
    },
    onResourcesLoaded : function() {
        this.initialization.resources = true;
        this.onClientReady();
    },
    onSettingsLoaded : function() {
        this.initialization.settings = true;
        this.onClientReady();
    },
    onClientReady : function() {

        if(this.initialization.resources == false || this.initialization.settings==false) {
            return;
        }
        $(".loading-progressbar .progress").width("100%");
        var vm = Poker.AppCtx.getViewManager();

        if(Poker.MyPlayer.loginToken!=null) {
            this.handleTokenLogin();
        } else {
            var cont = $(".login-container").show();
            setTimeout(function(){
                cont.addClass("show");
            },50);

            var loggedIn = this.handleLoginOnReconnect();
            if(!loggedIn) {
                this.handlePersistedLogin();
            }
        }
    },
    handleTokenLogin : function() {
        var token = Poker.MyPlayer.loginToken;
        Poker.AppCtx.getCommunicationManager().doLogin(token, token);
    },
    /**
     * Tries to login with credentials stored in local storage
     */
    handlePersistedLogin : function() {

        var username = Poker.Utils.load("username");
        if(username!=null) {
            var password = Poker.Utils.load("password");
            Poker.AppCtx.getCommunicationManager().doLogin(username, password);
        }
    },

    handleLoginOnReconnect : function() {
        if(Poker.MyPlayer.password!=null) {
            Poker.AppCtx.getCommunicationManager().doLogin(Poker.MyPlayer.name, Poker.MyPlayer.password);
            return true;
        } else {
            return false;
        }

    },
    onForcedLogout : function(code,message) {
        this.clearTimeouts();
        Poker.AppCtx.getViewManager().onForceLogout(code,message);
    },
    onUserDisconnected : function() {
        if(this.connected==true) {
            this.handleDisconnect();
            this.connected = false;
        }
    },
    handleDisconnect : function() {
        Poker.AppCtx.getPingManager().onDisconnect();
        console.log("DISCONNECTED");
        this.showConnectStatus(i18n.t("login.disconnected", {sprintf : [this.retryCount]}));
        this.clearTimeouts();
        this.reconnecting = true;
        this.reconnect();
    },
    onUserConnecting : function() {
        this.showConnectStatus(i18n.t("login.connecting"));
    },
    showConnectStatus : function(text) {
        $(".connect-status").html(text);
    },
    onUserReconnecting : function() {
        this.retryCount++;
        this.disconnectDialog.show(this.retryCount);
        this.showConnectStatus(i18n.t("login.disconnected", {sprintf : [this.retryCount]}));
    },
    onUserReconnected : function() {
        this.onUserConnected();
    },
    onPacketReceived : function() {
        this.scheduleDisconnectCheck();
    },
    scheduleDisconnectCheck : function() {
        this.clearTimeouts();
        var self = this;
        this.disconnectCheckTimeout = setTimeout(function(){
            self.sendVersionPacket();
            self.startReconnectingGraceTimeout = setTimeout(function(){
                console.log("version packet not received, handle disconnect");
                self.handleDisconnect();
            },5000);
        },10000);
    },
    clearTimeouts : function() {
        if(this.disconnectCheckTimeout!=null) {
            clearTimeout(this.disconnectCheckTimeout);
        }
        if(this.startReconnectingGraceTimeout!=null) {
            clearTimeout(this.startReconnectingGraceTimeout);
        }
        if(this.reconnectRetryTimeout!=null) {
            clearTimeout(this.reconnectRetryTimeout);
        }
    },
    reconnect : function() {

        if(this.retryCount < this.MAX_RECONNECT_ATTEMPTS) {
            console.log("Reconnecting");
            this.onUserReconnecting();
            Poker.AppCtx.getCommunicationManager().connect();
            this.scheduleReconnect();
        } else {
            this.disconnectDialog.stoppedReconnecting();
        }
    },
    scheduleReconnect : function() {
        if(this.reconnectRetryTimeout) {
            clearTimeout(this.reconnectRetryTimeout);
        }
        var self = this;
        this.reconnectRetryTimeout = setTimeout(function(){
            self.reconnect();
        },2000);
    },
    sendVersionPacket : function() {
        console.log("Sending version packet");
        var versionPacket = new FB_PROTOCOL.VersionPacket();
        versionPacket.game = 1;
        versionPacket.operatorid = 0;
        versionPacket.protocol = 8559;
        Poker.AppCtx.getCommunicationManager().getConnector().sendProtocolObject(versionPacket);
        Poker.AppCtx.getPingManager().versionPacketSent();
    }
});

