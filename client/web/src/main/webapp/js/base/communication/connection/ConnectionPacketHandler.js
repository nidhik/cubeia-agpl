var Poker = Poker || {};

/**
 *
 * @type {Poker.ConnectionPacketHandler}
 */
Poker.ConnectionPacketHandler = Class.extend({
    /**
     * @type {Poker.ConnectionManager}
     */
    connectionManager : null,
    init : function() {
        this.connectionManager = Poker.AppCtx.getConnectionManager();
    },
    handleLogin : function(status,playerId,name, credentials) {

        if (status == FB_PROTOCOL.ResponseStatusEnum.OK) {
            var token = null;
            if(credentials!=null) {
                var data = FIREBASE.ByteArray.fromBase64String(credentials);
                token = utf8.fromByteArray(data);
            }
            this.connectionManager.onUserLoggedIn(playerId,name,token);
        } else {
            this.connectionManager.showConnectStatus(
                    i18n.t("login.login-failed", {sprintf : [status]})
                );
        }
    },
    handleStatus : function(status) {
        //CONNECTING:1,CONNECTED:2,DISCONNECTED:3,RECONNECTING:4,RECONNECTED:5,FAIL:6,CANCELLED:7
        if (status === FIREBASE.ConnectionStatus.CONNECTED) {
            this.connectionManager.onUserConnected();
            this.initOperatorConfig();
        } else if (status === FIREBASE.ConnectionStatus.DISCONNECTED) {
            this.connectionManager.onUserDisconnected();
        } else if(status === FIREBASE.ConnectionStatus.CONNECTING){
            this.connectionManager.onUserConnecting();
        } else if(status == FIREBASE.ConnectionStatus.RECONNECTING){
            this.connectionManager.onUserReconnecting();
        } else if(status == FIREBASE.ConnectionStatus.RECONNECTED) {
            this.connectionManager.onUserReconnected();
        } else {
            console.log("Unhandled status " + status);
        }
    },
    handleForceLogout : function(code,message) {
        console.log(message);
        //logged in somewhere else
        this.connectionManager.onForcedLogout(code,message);
    },
    initOperatorConfig : function() {
        if(!Poker.OperatorConfig.isPopulated()) {
            var packet = new FB_PROTOCOL.LocalServiceTransportPacket();
            packet.seq = 0;
            packet.servicedata = utf8.toByteArray("" + Poker.SkinConfiguration.operatorId);
            Poker.AppCtx.getConnector().sendProtocolObject(packet);
        } else {
            Poker.AppCtx.getConnectionManager().onSettingsLoaded();
        }
    }
});