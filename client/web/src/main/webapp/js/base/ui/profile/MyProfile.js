"use strict";
var Poker = Poker || {};

Poker.MyProfile = Poker.Profile.extend({
    xp : 0,
    currentLevelXp : 0,
    thisLevelXp : 0,
    accounts : null,
    bonuses : null,

    init : function(){
        this._super();
        this.accounts = [];
        this.bonuses = [];
    },
    addBalance : function(amount,currencyCode,role){
        var balance = new Poker.Balance();
        balance.amount = amount;
        balance.currencyCode = currencyCode;
        balance.formattedBalance = Poker.Utils.formatWithSymbol(amount, currencyCode);
        balance.role = role;
        var updated = false;
        for(var i = 0; i<this.accounts.length; i++) {
            if(this.accounts[i].currencyCode == currencyCode && this.accounts[i].role == role) {
                this.accounts[i] = balance;
                updated = true;
                break;
            }
        }
        if(updated==false) {
            this.accounts.push(balance);
        }
    },
    addBonus : function(timeToNextCollect, coolDown, canCollect, bonusBalanceLowerLimit, name, currencyCode) {
        var bonus = new Poker.Bonus();
        bonus.timeToNextCollect = timeToNextCollect;
        bonus.coolDown = coolDown;
        bonus.canCollect = canCollect;
        bonus.bonusBalanceLowerLimit = bonusBalanceLowerLimit;
        bonus.name = name;
        bonus.currencyCode = currencyCode;

        var updated = false;
        for(var i = 0; i<this.bonuses.length; i++) {
            if(this.bonuses[i].name == name) {
                this.bonuses[i] = bonus;
                updated = true;
                break;
            }
        }
        if(updated==false) {
            this.bonuses.push(bonus);
        }
    }

});

Poker.Balance = Class.extend({
    formattedBalance : null,
    amount  : 0,
    currencyCode : null,
    role : null
});

Poker.Bonus = Class.extend({
    timeToNextCollect : 0,
    coolDown : 0,
    canCollect : false,
    bonusBalanceLowerLimit : 0,
    name : null,
    currencyCode : null
});