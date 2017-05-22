"use strict";
var Poker = Poker || {};

/**
 * Handles a poker hand in the UI
 * @type {Poker.DynamicHand}
 */
Poker.DynamicHand = Class.extend({

    cards:null,
    cardOder : null,
    hoverCards : null,
    discards:null,
    discardsEnabled : false,
    minDiscards:1,
    maxDiscards:1,
    folded : false,

    handContainer : null,
    ratio:1.4,
    offset : 0.45,
    width : 0,
    height : 0,
    cardWidth: 90,
    maxCards : 5,
    align : 0,
    useTransform : true,
    myPlayer : false,
    tableId : -1,
    alignments : {
        "2" : [0,0],
        "5" : [0,-1,0,0,1],
        "6" : [0,-1,-1,0,1,1],
        "7" : [0,-1,-1,-1,-1,0,1],
        "8" : [0,-1,-1,-1,0,1,1,1],
        "9" : [0,-1,-1,-1,0,0,1,1,1],
        "10" :[0,-1,-1,-1,-1,0,1,1,1,1]
    },
    cardOffset : {
        "1" : [50],
        "2" : [60,110],
        "3" : [35,80,125],
        "4" : [15,60,105,150],
        "5" : [0,45,90,135,180],
        "6" : [-30,15,60,105,150,195],
        "7" : [-45,0,45,90,135,180,225]
    },
    init : function(handContainer, myPlayer, tableId) {

        this.handContainer = handContainer;
        this.myPlayer = myPlayer;
        this.tableId = tableId;
        this.hoverCards = [];
        this.calculateCardDimensions();
        this.setup();
        var self = this;
        this.calculateWidth();
        $(window).on('resizeEnd redrawTable',function(){
            setTimeout(function(){
                if($("#tableView-"+self.tableId).is(":visible")) {
                    self.calculateWidth();
                    self.calculateCardDimensions();
                    self.updateCardPositions();
                }
            },50);
        });
    },
    calculateWidth : function() {
        var fraction = this.myPlayer ? 0.2 : 0.11;
        var containerWidth = $("#tableView-"+this.tableId).width();
        this.width = Math.floor(containerWidth*fraction);
    },
    setAlignment : function(pos,capacity) {
        var p = this.alignments[""+capacity];
        if(typeof(p)=="undefined") {
            this.align = 0;
        } else {
            this.align = p[pos];
        }
        this.updateCardPositions();
    },
    fold : function() {
        this.folded = true;
    },
    setup : function() {
        this.cards = new Poker.Map();
        this.cardOrder = [];
        this.discards = new Poker.Map();
        this.calculateCardProperties();
        var cssUtils = new Poker.CSSUtils();
        cssUtils.clearTransform(this.handContainer);
    },
    calculateCardProperties : function() {
        this.height = this.handContainer.height();
    },
    calculateCardDimensions : function() {
        var w = this.width;
        this.cardWidth = Math.floor(w/(1+(this.maxCards-1)*this.offset));
        this.handContainer.find("img").width(this.cardWidth).height(Math.floor(this.cardWidth*this.ratio))
    },
    getPositionForCard : function(num,total) {

       if(this.align == 0) {
           return this.getCenteredPosition(num,total);
       } else if(this.align < 0) {
           return this.getLeftPosition(num,total);
       } else  {
           return this.getRightPosition(num,total);
       }
    },
    exposeCards : function() {
        if(this.folded==false) {
            var cssUtils = new Poker.CSSUtils();
            cssUtils.setScale3d(this.handContainer,1,1,1, this.getTransformOrigin());
        }
    },
    getTransformOrigin : function() {
        if(this.align == 0) {
            return "50% 0%";
        } else if(this.align < 0) {
            return "0% 0%";
        } else {
            return "100% 0%";
        }
    },
    getLeftPosition : function(num,total){
        var pxOffset = Math.floor(this.cardWidth*this.offset);
        return { x : (num-1)*pxOffset, y : 0};
    },
    getRightPosition : function(num,total){
        var pxOffset = Math.floor(this.cardWidth*this.offset);
        return { x : this.width-this.cardWidth-(total-num)*pxOffset, y : 0};
    },
    getCenteredPosition : function(num,total) {
        var pxOffset = Math.floor(this.cardWidth*this.offset);
        var allCardWidth = this.cardWidth + (total-1)*pxOffset;
        var firstCardLeft = (this.width - allCardWidth)/2;
        return { x : firstCardLeft + (num-1)*pxOffset, y : 0};
    },

    enableDiscards : function(minDiscards,maxDiscards) {
        this.minDiscards = minDiscards;
        this.maxDiscards = maxDiscards;
        this.handContainer.addClass("discard-enable");
        this.discardsEnabled = true;
    },

    disableDiscards : function() {
        this.discardsEnabled = false;
        this.hoverCards = [];
        this.discards = new Poker.Map();
    },

    /**
     * @param {Poker.Card} card
     */
    addCard : function(card) {
        if(this.cards.contains(card.id)){
            return;
        }
        this.cards.put(card.id, card);
        this.cardOrder.push(card.id);
        this.handContainer.append(card.render(this.cards.size()));
        var self = this;
        card.getContainerElement().click(function() {
            if(self.discardsEnabled==true) {
                self.toggleDiscardedCard(card.id);
                self.updateCardPositions();
            }
        }).hover(
            function(){
                if(self.discardsEnabled==true) {
                    self.hoverCards.push(card.id);
                    self.updateCardPositions();
                }

            },function(){
                if(self.discardsEnabled==true) {
                    var index = self.hoverCards.indexOf(card.id);
                    self.hoverCards.splice(index,1);
                    self.updateCardPositions();
                }
            });
        this.calculateCardDimensions();
        this.updateCardPositions();

    },
    updateCardPositions : function() {
        this.calculateCardProperties();
        if(this.cards.size()>0) {
            this.calculateCardDimensions();
        }
        var nrOfCards = this.cards.size();
        var cssUtils = new Poker.CSSUtils();
        for(var i = 0; i<this.cardOrder.length; i++) {
            var card = this.cards.get(this.cardOrder[i]);
            var pos  = this.getPositionForCard(i+1,this.cardOrder.length);
            if(this.hoverCards.indexOf(card.id)!=-1 || this.discards.contains(card.id)==true) {
                pos.y = -Math.floor(this.cardWidth*0.5);
            }
            var el = card.getContainerElement();
            if(el && el.length && el.length>0) {
               cssUtils.setTranslate3d(el,this.cardOffset[nrOfCards+""][i],Math.floor(pos.y),0, "%");
               //el.css("left",Math.floor(pos.x) + "px");
               //el.css("top",Math.floor(pos.y) + "px");

            }
        }

    },
    discardCards : function(cardsToDiscard) {
        for(var c in cardsToDiscard) {
            var card = this.cards.remove(cardsToDiscard[c]);
            card.getContainerElement().off().remove();
            var index = this.cardOrder.indexOf(card.id);
            this.cardOrder.splice(index,1);
        }
        this.updateCardPositions();
    },
    removeAllCards : function() {
        this.cards = new Poker.Map();
        this.discards = new Poker.Map();
    },
    clear : function() {
        this.folded = false;
        this.setup();
        this.handContainer.empty();
        this.useTransform = true;
    },

    toggleDiscardedCard : function(cardId) {
        var card = this.cards.get(cardId);
        if (this.discards.contains(cardId)) {
            this.discards.remove(cardId);
            card.getContainerElement().removeClass("discarded");
        } else {
            if ( this.discards.size() < this.maxDiscards ) {
                this.discards.put(cardId, cardId);
                card.getContainerElement().addClass("discarded");
            } else if(this.discards.size()>0) {
                this.toggleDiscardedCard(this.discards.values()[0]);
                this.discards.put(cardId, cardId);
                card.getContainerElement().addClass("discarded");
            }
        }
    },
    getDiscards : function() {
        var discards = this.discards.values();
        return discards;
    }
});