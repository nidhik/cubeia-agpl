"use strict";
var Poker = Poker || {};

/**
 * @type {Poker.OperatorConfig}
 */
Poker.OperatorConfig = Class.extend({
    operatorId : null,

    /**
     * @type Poker.Map
     */
    configMap : null,

    currencyMap : null,
    enabledCurrencies : null,

    /**
     * @type Boolean
     */
    populated : false,
    init : function() {
        this.configMap = new Poker.Map();
    },
    isPopulated : function() {
        return this.populated;
    },
    createCurrencyMap: function(currencyParam) {
        this.currencyMap = new Poker.Map();
        if(typeof(currencyParam)!="undefined" && currencyParam!=null) {
            currencyParam = $.trim(currencyParam);
            if(currencyParam.length>0) {
                var currencies = JSON.parse(currencyParam);
                for(var i = 0; i<currencies.length; i++) {
                    this.currencyMap.put(currencies[i].code,currencies[i]);
                }
            }

        }
    },
    isCurrencyEnabled : function(currencyCode) {
        if(this.currencyMap.size()==0) {
            return true;
        } else {
            return this.currencyMap.contains(currencyCode);
        }
    },
    populate : function(params) {
        for(var p in params) {
          this.configMap.put(p,params[p]);
        }
        this.createCurrencyMap(this.getValue("CURRENCIES",''));
        this.populated = true;
    },
    getLogoutUrl : function() {
        return this.getValue("LOGOUT_PAGE_URL","");
    },
    getClientHelpUrl : function() {
        return this.getValue("CLIENT_HELP_URL","");
    },

    getProfilePageUrl:function() {
        return this.getValue("PROFILE_PAGE_URL", "http://localhost:8083/player-api/html/profile.html");
    },
    getLobbyRightPromotionUrl : function() {
        return this.getValue("LOBBY_RIGHT_PROMOTION_URL","");
    },
    getLobbyTopPromotionUrl : function() {
        return this.getValue("LOBBY_TOP_PROMOTION_URL",null);
    },
    getBuyCreditsUrl : function() {
        return this.getValue("BUY_CREDITS_URL", "");
    },
    getAccountInfoUrl : function() {
        return this.getValue("ACCOUNT_INFO_URL", "");
    },
    getShareUrl : function() {
        return this.getValue("SHARE_URL", null);
    },
    getOperatorName : function() {
        return this.getValue("OPERATOR_NAME", null);
    },
    getOperatorUrl : function() {
        return this.getValue("CLIENT_HOME_URL", null);
    },
    getEnabledCurrencies : function() {
        return this.currencyMap.values();
    },
    getCurrencyMap : function() {
        return this.currencyMap;
    },
    getValue : function(param,def) {
        var value =  this.configMap.get(param);
        if(value==null) {
          console.log("Value for param " + param + " not available, returning default " + def);
          value = def;
      }
      return value;
    }
});
Poker.OperatorConfig = new Poker.OperatorConfig();