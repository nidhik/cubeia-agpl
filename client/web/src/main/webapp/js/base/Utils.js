"use strict";
var Poker = Poker || {};

Poker.Utils = {
    currencySymbol: "",

    formatMultipleAmounts : function(amount1,amount2,separator,code) {
        amount1 = Poker.Utils.formatCurrency(amount1);
        amount2 = Poker.Utils.formatCurrency(amount2);
        var result = amount1 + separator + amount2 + " " + code;
        var currency = Poker.OperatorConfig.getCurrencyMap().get(code);
        if(currency!=null) {
            if(currency.symbol!=null && currency.symbol.length>0){
                result = currency.symbol + amount1 + separator + currency.symbol + amount2;
            } else {
                result = amount1 + separator + amount2 + " " + currency.name;
            }
        }
        return result;
    },
    formatWithSymbol :  function(amount,code) {
        var result = amount + " " + code;
        var currency = Poker.OperatorConfig.getCurrencyMap().get(code);
        if(currency!=null) {
            if(currency.symbol!=null && currency.symbol.length>0){
                result = currency.symbol+amount;
            } else {
                result = amount + " " + currency.name;
            }
        }
        return result;
    },
    formatCurrency: function(amount) {
        return Poker.Utils._baseFormat(amount);
    },
    _baseFormat: function(amount) {
        var fractionalDigits = 10;
        var amount = "" + parseFloat(amount).toFixed(10);

        var result = "";
        var split = amount.split(".");

        //remove trailing zeros
        var decimals = split[1];
        if(!decimals) {
            decimals="";
        }
        for (var i = decimals.length - 1; i >= 0; i--) {
            if (decimals.charAt(i) != '0') {
                result = Poker.Utils.formatWholePart(split[0]) + "." + decimals.substr(0, i + 1);
                break;
            }
            if (i == 0) {
                result = Poker.Utils.formatWholePart(split[0]);
            }
        }

        return result;
    },
    formatWholePart: function(amount) {
        return amount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    },
    formatCurrencyString: function(amount) {
        return Poker.Utils.currencySymbol + Poker.Utils.formatCurrency(amount);
    },
    translateCurrencyCode: function(currencyCode) {
        var map = Poker.OperatorConfig.getCurrencyMap();
        if(map.contains(currencyCode)){
            return map.get(currencyCode).name;
        } else {
            return currencyCode;
        }

    },
    getCardString: function(gamecard) {
        var ranks = "23456789tjqka ";
        var suits = "cdhs ";
        return ranks.charAt(gamecard.rank) + suits.charAt(gamecard.suit);
    },
    depositReturn: function(type) {
        var dialogManager = Poker.AppCtx.getDialogManager();
        if (type == "success" || type == "cancel") {
            dialogManager.displayGenericDialog({
                container: Poker.AppCtx.getViewManager().getActiveView().getViewElement(),
                translationKey: "deposit-" + type,
                displayCancelButton: false
            });
        }
    },
    calculateDimensions: function(availableWidth, availableHeight, aspectRatio) {
        var width = availableHeight * aspectRatio;
        var height = availableHeight;
        if (width > availableWidth) {
            height = availableWidth / aspectRatio;
            width = availableWidth;
        }
        return { width: width, height: height};
    },
    /**
     * Calculates the distance in percent for two elements based on
     * a containers dimensions, if the container isn't specified
     * window dimensions will be used
     * @param src
     * @param target
     * @param [targetCenter]
     * @param [srcCenter]
     * @return {Object}
     */
    calculateDistance: function(src, target, targetCenter, srcCenter) {
        var srcOffset = src.offset();
        var targetOffset = target.offset();
        var targetLeft = targetOffset.left;

        if (targetCenter === true) {
            targetLeft = targetLeft + (target.outerWidth() / 2);
        }

        var srcLeft = srcOffset.left;
        if (srcCenter === true) {
            srcLeft = srcLeft + (src.outerWidth() / 2);
        }

        var leftPx = targetLeft - srcLeft;
        var topPx = targetOffset.top - srcOffset.top;
        var distLeft = 100 * (leftPx / src.outerWidth());
        var distTop = 100 * (topPx / src.outerHeight());


        return { left: distLeft, top: distTop };

    },
    storeUser: function(username, password) {
        Poker.Utils.store("username", username);
        Poker.Utils.store("password", password);
    },
    removeStoredUser: function() {
        Poker.Utils.remove("username");
        Poker.Utils.remove("password");
    },
    store: function(name, value) {
        var store = Poker.Utils.getStorage();
        if (store != null) {
            store.removeItem(name);
            store.setItem(name, value);
        }
    },
    remove: function(name) {
        var store = Poker.Utils.getStorage();
        if (store != null) {
            store.removeItem(name);
        }
    },
    load: function(name, defaultValue) {
        var store = Poker.Utils.getStorage();
        if (store != null) {
            return store.getItem(name);
        } else if (typeof(defaultValue) !== "undefined") {
            return defaultValue;
        } else {
            return null;
        }
    },
    loadJSON : function(name) {
        var json = Poker.Utils.load(name,null);
        if(json==null) {
            return null;
        }
        return JSON.parse(json);
    },
    storeJSON : function(name,value) {
        var json = JSON.stringify(value);
        Poker.Utils.store(name,value);
    },
    loadBoolean: function(name, defaultValue) {
        var val = Poker.Utils.load(name, defaultValue);
        if (val != null) {
            return val == "true";
        } else if (typeof(defaultValue) !== "undefined") {
            return defaultValue;
        }
        return false;
    },
    getStorage: function() {
        if (typeof(localStorage) !== "undefined") {
            return localStorage;
        }
        else {
            return null;
        }
    },
    filterMessage: function(message) {
        if (message != null) {
            return message.replace(/[<>"']*/gi, '');
        }
        return null;
    }

};