"use strict";
var Poker = Poker || {};

Poker.AchievementManager = Class.extend({

    soundManager : null,

    init : function() {
        this.soundManager = new Poker.SoundManager(Poker.AppCtx.getSoundRepository(), -1);
        this.soundManager.setReady(true);
    },
    handleAchievement : function(tableId, playerId, message) {
        console.log("player " + playerId + " received", message);
        if(playerId == Poker.MyPlayer.id) {
            if(message.type=="achievement") {
                var n = new Poker.TextNotifcation(message.achievement.name + ' ' + i18n.t("achievement.completed"),
                    message.achievement.description,message.achievement.imageUrl);
                Poker.AppCtx.getNotificationsManager().notify(n, {time:15000});
                this.soundManager.playSound(Poker.Sounds.PROGRESSION_ACHIEVEMENT, 0);
            } else if(message.type=="xp" && message.subType == "levelUp") {
                var level = message.attributes.level;
                var totalXp = message.attributes.totalXp;
                var profileManager = Poker.AppCtx.getProfileManager();
                if(profileManager.myPlayerProfile.level!=level){
                    var n = new Poker.LevelUpNotification(level);
                    Poker.AppCtx.getNotificationsManager().notify(n, {time : 15000, class_name : "gritter-dark level-up"});
                    this.soundManager.playSound(Poker.Sounds.PROGRESSION_LEVEL_UP, 0);
                    profileManager.updateLevel(parseInt(level),parseInt(totalXp));
                } else {
                    console.log("Ignoring level up, same level");
                }
            } else if(message.type == "xp" && message.subType =="increase")  {
                var level = message.attributes.level;
                var totalXp = message.attributes.totalXp;
                Poker.AppCtx.getProfileManager().updateXp(parseInt(totalXp),parseInt(level));

            } else if(message.type == "item")  {
                var n = new Poker.TextNotifcation(
                    i18n.t("item.awarded")+' '+message.item.name,
                    message.item.description,
                    message.item.imageUrl);
                Poker.AppCtx.getNotificationsManager().notify(n, {time:15000});
                this.soundManager.playSound(Poker.Sounds.PROGRESSION_ACHIEVEMENT, 0);
            } else if (message.type == "payment") {
            	
            	var n;
            	
            	if (message.subType == "confirmed") {
            		var amount = message.attributes["CONVERTED_AMOUNT"];
            		var currency = message.attributes["CONVERTED_CURRENCY"];
            		
            		var formattedAmount = Poker.Utils.formatWithSymbol(amount, currency);
            		
            		var n = new Poker.TextNotifcation(
        				i18n.t("payment.notifications.deposit-confirmed.title"),
        				i18n.t("payment.notifications.deposit-confirmed.text", { sprintf : [ formattedAmount] }),
        				contextPath + "/skins/default/images/Bitcoin-64.png"
            		);
            		
            	} else if (message.subType == "error") {
            		var errorCode = message.attributes["ERROR_CODE"];
            		
            		var n = new Poker.TextNotifcation(
        				i18n.t("payment.notifications.error.title"),
        				i18n.t("payment.notifications.error." + errorCode, { sprintf : [ formattedAmount] }),
        				contextPath + "/skins/default/images/Bitcoin-64.png"
            		);
            	}
            	
            	if (n) {
	            	Poker.AppCtx.getNotificationsManager().notify(n, {time:30000});
	            	this.soundManager.playSound(Poker.Sounds.PROGRESSION_ACHIEVEMENT, 0);
            	}
            } else if (message.type == "bonusActivated") {
                var currency = message.attributes.currency;
                var bonusString = Poker.Utils.formatWithSymbol(message.attributes.amountBonus,currency);
                var realString  =  Poker.Utils.formatWithSymbol(message.attributes.amountReal,currency);
                var msg = "Awarded bonus of " + bonusString;
                if(parseFloat(message.attributes.amountReal)>0) {
                    msg = msg + " and " + realString + " to main account";
                }
                var n = new Poker.TextNotifcation(
                    i18n.t("bonus.activated")+' '+message.attributes.name,
                    msg,
                    contextPath + "/skins/default/images/notification/bonus-activated.png");
                Poker.AppCtx.getNotificationsManager().notify(n, {time:15000});
                this.soundManager.playSound(Poker.Sounds.BONUS_ACTIVATED, 0);

            } else if (message.type == "bonusReleased") {
                var currency = message.attributes.currency;
                var released  =  Poker.Utils.formatWithSymbol(message.attributes.amount,currency);
                var msg = "Released " + released +" from your bonus account.";
                var n = new Poker.TextNotifcation(
                    i18n.t("bonus.released"),
                    msg,
                    contextPath + "/skins/default/images/notification/bonus-released.png");
                Poker.AppCtx.getNotificationsManager().notify(n, {time:15000});
                this.soundManager.playSound(Poker.Sounds.BONUS_RELEASED, 0);
            }
        }

    }
});
