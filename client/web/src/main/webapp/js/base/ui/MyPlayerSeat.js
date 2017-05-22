"use strict";
var Poker = Poker || {};

/**
 * Handles the UI for the logged in player
 *
 * @extends {Poker.Seat}
 * @type {Poker.MyPlayerSeat}
 */
Poker.MyPlayerSeat = Poker.Seat.extend({

    /**
     * @type Poker.MyActionsManager
     */
    myActionsManager : null,

    /**
     * @type CircularProgressBar
     */
    progressbar : null,

    /**
     * @type Number
     */
    tableId : null,

    seatBalance : null,

    avatarElement : null,

    infoElement : null,
    seatBase : null,
    soundManager : null,

    init : function(tableId,elementId, seatId, player, myActionsManager, animationManager,soundManager) {
        this._super(tableId, elementId,seatId, player,animationManager);
        this.tableId = tableId;
        this.myActionsManager = myActionsManager;
        this.seatElement = $("#"+elementId);
        this.renderSeat();
        this.infoElement = $("#"+elementId+"Info").show();
        this.progressbar = new Poker.CanvasProgressbar("#"+elementId+"Progressbar canvas",null,soundManager);
        this.seatBalance = this.seatElement.find(".seat-balance");
        this.myActionsManager.onSatDown();
        this.seatBase = this.seatElement.find(".avatar-base");
    },
    setSeatPos : function(prev,pos) {
        //do nothing
    },
    exposeCards : function(){

    },
    renderSeat : function(){
        var output = this.templateManager.render("myPlayerSeatTemplate",this.player);
        this.seatElement.html(output);
        this.cardsContainer = this.seatElement.find(".cards-container");
        this.actionAmount = this.seatElement.find(".action-amount");
        this.actionText = this.seatElement.find(".action-text");
        this.handStrength = this.seatElement.find(".hand-strength");
        this.avatarElement = this.seatElement.find(".avatar");
        this.levelElement = this.seatElement.find(".player-level");
        this.awardElement = this.seatElement.find(".player-award");
        this.itemElement = this.seatElement.find(".player-item");
        this.hand = new Poker.DynamicHand(this.cardsContainer,true, this.tableId);

        this.reset();
        $("#myPlayerName-"+this.tableId).html(this.player.name);
    },
    activateSeat : function(allowedActions, timeToAct,mainPot,fixedLimit) {
        var self = this;
        var auto = this.myActionsManager.onRequestPlayerAction(allowedActions, mainPot, fixedLimit, function(){
            var time = timeToAct;
            self.progressbar.start(time);
        });
        for (var a in allowedActions) {
            var act = allowedActions[a];
            if (act.type.id == Poker.ActionType.DISCARD.id) {
                this.seatElement.find(".discard-description").show();
                this.hand.enableDiscards(act.minAmount, act.maxAmount);
            }
        }
        if(auto==false) {
            Poker.AppCtx.getViewManager().requestTableFocus(this.tableId);
        }
        return auto;
    },
    rebuyRequested : function(rebuyCost, chipsForRebuy, timeToAct) {
        this.progressbar.start(timeToAct);
        this.myActionsManager.showRebuyButtons(rebuyCost, chipsForRebuy);
    },
    addOnRequested : function(addOnCost, chipsForAddOn) {
        this.myActionsManager.showAddOnButton(addOnCost, chipsForAddOn);
    },
    hideRebuyButtons : function() {
        this.myActionsManager.hideRebuyButtons();
        this.progressbar.hide();
    },
    hideAddOnButton : function() {
        this.myActionsManager.hideAddOnButton();
    },

    onAction : function(actionType,amount,cardsToDiscard){

        this.running = false;
        this.progressbar.stop();
        this.showActionData(actionType,amount);
        this.myActionsManager.hideActionElements();
        if(actionType.id == Poker.ActionType.FOLD.id) {
            this.fold();
            Poker.AppCtx.getViewManager().updateTableInfo(this.tableId,{});
        } else if(actionType.id == Poker.ActionType.SIT_IN.id) {

        } else if(actionType == Poker.ActionType.DISCARD) {
            this.discardCards(cardsToDiscard);
            this.hand.disableDiscards();
        }
        this.seatElement.find(".discard-description").hide();
        this.cardsContainer.removeClass("discard-enable");
    },
    clearSeat : function() {
        this.seatElement.html("");
        $("#myPlayerBalance-"+this.tableId).html("");
        $("#myPlayerName-"+this.tableId).html("");
        this.myActionsManager.onWatchingTable();
        this.infoElement.hide();
    },
    showHandStrength : function(hand) {
        if(hand.id != Poker.Hand.UNKNOWN.id) {
            this.handStrength.visible = true;
            this.handStrength.html(hand.text).show();
        }
    },
    updatePlayer : function(player) {
        var updated = false;
        if(player.tableStatus.id != this.player.tableStatus.id || player.away != this.player.away ||
            player.sitOutNextHand != this.player.sitOutNextHand) {
            updated = true;
        }
        this.player = player;
        $("#myPlayerBalance-"+this.tableId).html(this.player.balance);
        this.seatBalance.html(this.player.balance);

        if (updated) {
            this.handlePlayerStatus();
        }
    },
    updatePlayerStatus : function(player) {
        this.player = player;
        this.handlePlayerStatus();
    },
    handlePlayerStatus : function() {
        if(this.player.tableStatus == Poker.PlayerTableStatus.SITTING_OUT) {
            this.seatElement.addClass("seat-sit-out");
            this.seatElement.find(".player-status").show().html(this.player.tableStatus.text);
            this.myActionsManager.onSitOut();
        } else if (this.player.tableStatus == Poker.PlayerTableStatus.TOURNAMENT_OUT){
            this.myActionsManager.onTournamentOut();
        } else if(this.player.tableStatus == Poker.PlayerTableStatus.SITTING_IN){
            this.seatElement.find(".player-status").html("").hide();
            this.seatElement.removeClass("seat-sit-out");
            if(this.player.away == true || this.player.sitOutNextHand == true) {
                this.myActionsManager.setSitOutNextHand(true);
            } else {
                this.myActionsManager.onSitIn();
            }
        }
    },
    hideActionText : function() {
        this.actionText.html("").hide();
    },
    onCardDealt : function(card) {
        return;
        var div = card.getJQElement();
        new Poker.CSSClassAnimation(div).addClass("dealt").start(this.animationManager);
        Poker.AppCtx.getViewManager().updateTableInfo(this.tableId,{card:card});
    },
    onReset : function() {
        Poker.AppCtx.getViewManager().updateTableInfo(this.tableId,{});
        if ( this.hand != null ) {
            this.hand.removeAllCards();
        }
    },
    fold : function() {

        this.seatElement.addClass("seat-folded");
        this.seatElement.find(".player-card-container").addClass("seat-folded");
        this.myActionsManager.onFold();
        this.handStrength.visible = false;
        if(this.player.tableStatus == Poker.PlayerTableStatus.SITTING_OUT) {
            this.hideActionText();
        }

    },
    clear : function() {
        this.seatElement.empty();
        $("#myPlayer-"+this.tableId).hide();
        this.progressbar.stop();
    },
    getDealerButtonOffsetElement : function() {
        return this.seatBase;
    },
    isMySeat : function() {
        return true;
    }

});
