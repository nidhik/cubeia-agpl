"use strict";
var Poker = Poker || {};

/**
 * @type {Poker.ActionButtons}
 * @extends Poker.AbstractTableButtons
 */
Poker.ActionButtons = Poker.AbstractTableButtons.extend({

    cancelBetActionButton : null,
    fixedBetActionButton : null,
    fixedRaiseActionButton : null,

    init : function(view, actionCallback, raiseCallback, betCallback, amountCallback) {
        this._super(view,actionCallback);

        this._addBetAmountButton($(".action-bet",view),Poker.ActionType.BET,actionCallback,amountCallback);
        this._addBetAmountButton($(".action-raise",view),Poker.ActionType.RAISE,actionCallback,amountCallback);

        this._addActionButton($(".action-bring-in",view),Poker.ActionType.BRING_IN,actionCallback ,true);
        this._addActionButton($(".action-check", view), Poker.ActionType.CHECK, actionCallback, false);
        this._addActionButton($(".action-fold", view), Poker.ActionType.FOLD, actionCallback, false);
        this._addActionButton($(".action-call", view), Poker.ActionType.CALL, actionCallback, true);
        this._addActionButton($(".action-big-blind", view), Poker.ActionType.BIG_BLIND, actionCallback, true);
        this._addActionButton($(".action-small-blind", view), Poker.ActionType.SMALL_BLIND, actionCallback, true);

        this._addActionButton($(".action-rebuy", view), Poker.ActionType.REBUY, actionCallback, true);
        this._addActionButton($(".action-decline-rebuy", view), Poker.ActionType.DECLINE_REBUY, actionCallback, true);
        this._addActionButton($(".action-add-on", view), Poker.ActionType.ADD_ON, actionCallback, true);
        this._addActionButton($(".action-discard", view), Poker.ActionType.DISCARD, actionCallback, true);

        this.fixedBetActionButton = new Poker.ActionButton($(".fixed-action-bet",view),Poker.ActionType.BET,actionCallback,true);
        this.fixedRaiseActionButton = new Poker.ActionButton($(".fixed-action-raise",view),Poker.ActionType.RAISE,actionCallback,true);

    },

    _addActionButton : function(elId, actionType, callback, showAmount){
        var button = null;
        if(actionType.id == Poker.ActionType.BET.id || actionType.id == Poker.ActionType.RAISE.id ) {
            button = new Poker.BetSliderButton(elId,actionType,callback,showAmount);
        } else {
            button = new Poker.ActionButton(elId, actionType, callback, showAmount);
        }
        this.buttons.put(actionType.id, button);
    },
    _addBetAmountButton : function(button,action, actionCallback, amountCallback) {
        var button = new Poker.BetAmountButton(button, action ,actionCallback,false,amountCallback);
        this.buttons.put(action.id, button);
    },
    betOrRaise : function(){
        var button = this.buttons.get(Poker.ActionType.BET.id);
        if(button.isVisible()){
            button.click();
        } else {
            button = this.buttons.get(Poker.ActionType.RAISE.id);
            button.click();
        }
    },
    hideAll : function() {
        var buttons = this.buttons.values();
        for (var a in buttons) {
            if (buttons[a].actionType === Poker.ActionType.ADD_ON) {
                // Not hiding add-on button because it should be visible during the entire break.
                continue;
            }
            buttons[a].el.hide();
        }
        this.fixedBetActionButton.hide();
        this.fixedRaiseActionButton.hide();
    },
    showButtons : function(actions, mainPot, fixedLimit) {
        for (var a in actions) {
            var act = actions[a];
            if (fixedLimit && act.type.id == Poker.ActionType.BET.id) {
                if (act.minAmount > 0) {
                    this.fixedBetActionButton.setAmount(act.minAmount);
                }
                this.fixedBetActionButton.show();
            } else if (fixedLimit && act.type.id == Poker.ActionType.RAISE.id) {
                if (act.minAmount > 0) {
                    this.fixedRaiseActionButton.setAmount(act.minAmount,act.maxAmount,mainPot);
                }
                this.fixedRaiseActionButton.show();
            } else {
                if (act.minAmount > 0) {
                    this.getButton(act.type).setAmount(act.minAmount,act.maxAmount,mainPot);
                }
                this.show(act.type);
            }
        }
    },
    showRebuyButtons: function() {
        console.log("Showing rebuy buttons");
        this.show(Poker.ActionType.DECLINE_REBUY);
        this.show(Poker.ActionType.REBUY);
    },
    hideRebuyButtons : function() {
        console.log("Hiding rebuy buttons");
        this.hide(Poker.ActionType.DECLINE_REBUY);
        this.hide(Poker.ActionType.REBUY);
    },
    showAddOnButton: function() {
        console.log("Showing add-on button");
        this.show(Poker.ActionType.ADD_ON);
    },
    hideAddOnButton : function() {
        console.log("Hiding add-on button");
        this.hide(Poker.ActionType.ADD_ON);
    }
});