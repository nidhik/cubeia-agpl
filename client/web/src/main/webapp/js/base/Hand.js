"use strict";
var Poker = Poker || {};

/**
 * The poker hands that a player can get
 * @type {Poker.Hand}
 */
Poker.Hand = {
    UNKNOWN : { text: "Unknown", id : 0, type : "short"},
    HIGH_CARD : { text: "High card", id : 1, type : "short" },
    PAIR : { text: "Pair", id : 2, type : "short"},
    TWO_PAIRS : { text :"Two Pairs", id : 3, type : "short"},
    THREE_OF_A_KIND : { text : "Three of a kind",id : 4, type : "long"},
    STRAIGHT : {text : "Straight",id : 5, type : "short"},
    FLUSH : { text : "Flush",id : 6, type : "short"},
    FULL_HOUSE : {text : "Full house",id : 7, type : "short"},
    FOUR_OF_A_KIND : {text : "Four of a kind",id : 8, type : "long"},
    STRAIGHT_FLUSH : { text : "Straight Flush",id : 9, type : "long"},
    ROYAL_STRAIGHT_FLUSH : { text : "Royal flush",id : 10, type : "long"},

    fromName : function(name) {
        for(var x in Poker.Hand) {
            if(x == name) {
                return Poker.Hand[x];
            }
        }
        return null;
    },
    fromId : function(id) {
        for(var x in Poker.Hand) {
            if(Poker.Hand[x].id && Poker.Hand[x].id == id) {
                  return Poker.Hand[x];
            }
        }
        return null;
    }
};