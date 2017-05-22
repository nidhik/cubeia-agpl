"use strict";
var Poker = Poker || {};

/**
 * Handles a poker card in the UI
 * @type {Poker.Card}
 */
Poker.Card = Class.extend({
    cardString:null,
    id:-1,
    tableId : -1,
    templateManager:null,
    cardImage : null,
    cardElement : null,
    type : null,

    init:function (id, tableId, cardString, templateManager) {
        this.templateManager = templateManager;
        this.id = id;
        this.tableId = tableId;
        this.type="svg";
        if (cardString == "  ") {
            cardString = "back";
            this.type = "png";
        }
        this.cardString = cardString;
    },
    /**
     * Creates and returns the html for the card, does not add anything
     * to the DOM
     * @return {*}
     */
    render : function (cardNum) {
        var t = this.getTemplate();

        var backfaceImageUrl = contextPath+ "/skins/" + Poker.SkinConfiguration.name +"/images/cards/"+this.cardString+"."+this.type;
        var output = this.templateManager.render(t, {domId:this.getDomId(), backgroundImage:backfaceImageUrl, cardNum : cardNum});
        return output;
    },
    getDomId : function() {
        return this.domId = this.id + "-" + this.tableId;
    },
    /**
     * Exposes a card, from displaying the back to the actual card
     * updates DOM
     * @param cardString
     */
    exposeCard : function(cardString, callback) {
        this.cardString = cardString;
        this.setCardImage(contextPath + "/skins/" + Poker.SkinConfiguration.name +"/images/cards/"+this.cardString+".svg");
        callback();
    },

    /**
     * Sets the backgroundImage attribute on card image div.
     * @param imageUrl
     */
    setCardImage : function(imageUrl) {
        var element = this.getContainerElement();
        element.attr("src",imageUrl).hide();
        setTimeout(function(){element.show();},50);
    },

    /**
     * Returns the JQuery card element
     * @return {*}
     */
    getJQElement:function () {
        if(this.cardImage==null) {
            this.cardElement =  $("#" + this.getCardDivId());
            this.cardImage = document.getElementById(this.getCardDivId()).children[0];
        }
        return $("#" + this.getCardDivId());
    },
    getContainerElement : function(){
       return $("#c" + this.getDomId());
    },
    getDOMElement : function() {
      return this.getJQElement().get(0);
    },
    getTemplate:function () {
        return "playerCardTemplate";
    },
    getCardDivId:function () {
        return "playerCard-" + this.id + "-" + this.tableId;
    },
});