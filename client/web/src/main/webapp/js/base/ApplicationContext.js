"use strict";
var Poker = Poker  || {};
/**
 * Class that manages global instances.
 * Usage:
 *  //needs to be executed before anything else,
 *  //usually when the onload event is triggered
 *  Poker.AppCtx.wire();
 *
 *  var tableManager = Poker.AppCtx.getTableManager();
 * @type {Poker.AppCtx}
 */
Poker.AppCtx = Class.extend({
    init : function() {
    },
    /**
     * Creates all the global instances that are needed for the application
     *
     * @param settings
     */
    wire : function(settings) {

        var chatManager = new Poker.ChatManager();
        this.getChatManager = function() {
            return chatManager;
        };

        var playerApi = new Poker.PlayerApi(settings.playerApiBaseUrl, settings.operatorApiBaseUrl);
        this.getPlayerApi = function() {
            return playerApi;
        };

        var accountingApi = new Poker.AccountingApi(settings.playerApiBaseUrl);
        this.getAccountingApi = function() {
            return accountingApi;
        };

        //this
        var templateManager = new Poker.TemplateManager();

        /**
         *
         * @return {Poker.TemplateManager}
         */
        this.getTemplateManager = function() {
            return templateManager;
        };

        var tableManager = new Poker.TableManager();
        /**
         *
         * @return {Poker.TableManager}
         */
        this.getTableManager = function() {
            return tableManager;
        };

        var dialogManager = new Poker.DialogManager();
        /**
         *
         * @return {Poker.DialogManager}
         */
        this.getDialogManager = function() {
            return dialogManager;
        };

        var viewManager = new Poker.ViewManager("tabItems");
        /**
         *
         * @return {Poker.ViewManager}
         */
        this.getViewManager = function(){
            return viewManager;
        };

        var mainMenuManager = new Poker.MainMenuManager(this.getViewManager());

        /**
         *
         * @return {Poker.MainMenuManager}
         */
        this.getMainMenuManager = function() {
            return mainMenuManager;
        };




        var lobbyLayoutManager = new Poker.LobbyLayoutManager();

        /**
         *
         * @return {Poker.LobbyLayoutManager}
         */
        this.getLobbyLayoutManager = function() {
            return lobbyLayoutManager;
        };

        /*
         * The only layout manager we only need (?) one instance of,
         * since you only are able to have one lobby open at once
         */
        var lobbyManager = new Poker.LobbyManager();

        /**
         *
         * @return {Poker.LobbyManager}
         */
        this.getLobbyManager = function() {
            return lobbyManager;
        };

        var soundsRepository = new Poker.SoundRepository();



        /**
         *
         * @return {Poker.SoundRepository}
         */
        this.getSoundRepository = function() {
            return soundsRepository;
        };



        var connectionManager = new Poker.ConnectionManager(settings.operatorId, settings.authCookie);
        /**
         * @return {Poker.ConnectionManager}
         */
        this.getConnectionManager = function() {
            return connectionManager;
        };


        var comHandler = new Poker.CommunicationManager(settings.webSocketUrl, settings.webSocketPort, settings.secure);
        /**
         *
         * @return {Poker.CommunicationManager}
         */
        this.getCommunicationManager = function() {
            return comHandler;
        };

        /**
         *
         * @return {FIREBASE.Connector}
         */
        this.getConnector = function() {
            return comHandler.getConnector();
        };

        var tournamentManager = new Poker.TournamentManager(settings.tournamentLobbyUpdateInterval);
        /**
         * @return {Poker.TournamentManager}
         */
        this.getTournamentManager = function() {
            return tournamentManager;
        };

        var handHistoryManager = new Poker.HandHistoryManager();

        /**
         * @return {Poker.HandHistoryManager}
         */
        this.getHandHistoryManager = function() {
            return handHistoryManager;
        };

        var navigation = new Poker.Navigation();
        this.getNavigation = function() {
            return navigation;
        };





        var notificationsManager = new Poker.NotificationsManager();
        /**
         * @return {Poker.NotificationsManager}
         */
        this.getNotificationsManager = function() {
            return notificationsManager;
        };

        var achievementManager = new Poker.AchievementManager();
        /**
         * @return {Poker.AchievementManager}
         */
        this.getAchievementManager = function() {
            return achievementManager;
        }

        var profileManager = new Poker.ProfileManager();
        /**
         * @return {Poker.ProfileManager}
         */
        this.getProfileManager = function() {
            return profileManager;
        };

        var accountPageManager = new Poker.AccountPageManager();

        /**
         *
         * @return {Poker.AccountPageManager}
         */
        this.getAccountPageManager = function() {
            return accountPageManager;
        };
        var pingManager = new Poker.PingManager();
        /**
         * @return {Poker.PingManager}
         */
        this.getPingManager = function() {
            return pingManager;
        };


        Handlebars.registerHelper('translateCurrencyCode',function(currencyCode){
            return Poker.Utils.translateCurrencyCode(currencyCode);
        });
        Handlebars.registerHelper("currencySymbol",function(amount,code){
            return new Handlebars.SafeString(Poker.Utils.formatWithSymbol(amount,code));
        });
        Handlebars.registerHelper("currencyAmountSymbol",function(amount,code){
            return new Handlebars.SafeString(Poker.Utils.formatWithSymbol(Poker.Utils.formatCurrency(amount),code));
        });
        Handlebars.registerHelper("currencyMultiple",function(amount1,amount2,separator,code){
            return new Handlebars.SafeString(Poker.Utils.formatMultipleAmounts(amount1,amount2,separator,code));
        });
        Handlebars.registerHelper('currency',function(amount){
            return Poker.Utils.formatCurrency(amount);
        });
        Handlebars.registerHelper('validId', function(){
            var id = arguments[0];
            return id!=null && id>=0;
        });

        Handlebars.registerHelper('accountLabel', function(role){
            if(role == "BONUS") {
                return "Bonus:";
            } else {
                return "Balance:"
            }
        });

        Handlebars.registerHelper('mbtc', function(satoshis){
            return Poker.Utils.formatCurrency(parseInt(satoshis)/100000);
        });
        Handlebars.registerHelper('renderLock',function(level){
            var myLevel = profileManager.myPlayerProfile.level
            console.log("level = " + level);
            console.log("myLevel = " + myLevel);
            if(level!=0 && myLevel<level) {
                return new Handlebars.SafeString('<div class="lock"></div>');
            } else {
                return "";
            }
        });

        Handlebars.registerHelper('t', function(i18n_key) {
            var result = i18n.t(i18n_key);
            return new Handlebars.SafeString(result);
        });

        Handlebars.registerHelper('fromNow',function(date){
            return moment(parseInt(date)).fromNow();
        });

        Handlebars.registerHelper('date', function(date) {
            return moment(parseInt(date)).format("lll");
        });

        Handlebars.registerHelper('cardIcon',function(cardStr){
            var res = '<span class="card-str">' + cardStr.charAt(0).toUpperCase()  + '</span><span class="suit-icon-'+cardStr.charAt(1)+'"></span>';
            return new Handlebars.SafeString(res);
        });

    }
});
Poker.AppCtx = new Poker.AppCtx();