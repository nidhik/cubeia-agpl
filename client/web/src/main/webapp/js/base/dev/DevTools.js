"use strict";
var Poker = Poker || {};


Poker.DevTools = Class.extend({
    tableId : 999999,
    tableManager : null,
    cards : null,
    cardIdSeq : 0,
    mockEventManager : null,
    variant : null,
    TEXAS_HOLDEM : { numCards : 2, id : 0},
    CRAZY : { numCards : 3, id : 2},
    OMAHA : { numCards : 4, id : 5},
    TELESINA : { numCards : 5, id:  1},
    SEVEN_CARD_STUD_ONE_CARD_LEFT : { numCards : 6, id:  4},
    SEVEN_CARD_STUD : { numCards : 7, id:  4},
    capacity : 2,

    init : function() {
        var self = this;
        this.initCards();
        this.variant = this.TEXAS_HOLDEM;
    },
    initCards : function() {
       var suits = "hsdc ";
       var rank = "0"
    },
    launch : function() {
        var self = this;
        Poker.AppCtx.getViewManager().onLogin();
        setTimeout(function(){
            self.createTable();
        },1000);
    },

    createTable : function() {
        var self = this;
        var capacity = this.capacity;
        var tableName = "Dev Table";
        this.tableManager = Poker.AppCtx.getTableManager();
        var tableViewContainer = $(".table-view-container");
        var templateManager = Poker.AppCtx.getTemplateManager();


        var beforeFunction = function() {
            var tableLayoutManager = new Poker.TableLayoutManager(self.tableId, tableViewContainer,
                templateManager,capacity,new Poker.SoundManager());

            self.tableManager.createTable(self.tableId, capacity, tableName , tableLayoutManager);
            self.tableManager.updateCapacity(self.tableId,capacity);
            Poker.AppCtx.getViewManager().addTableView(tableLayoutManager,tableName);
            //new Poker.PositionEditor("#tableView-"+self.tableId);
            tableLayoutManager.updateVariant(self.variant.id);
            self.cardIdSeq = 0;
        };

        var cleanUpFunction = function() {
            self.tableManager.leaveTable(self.tableId);
            Poker.AppCtx.getViewManager().removeTableView(self.tableId);
        };

        this.mockEventManager = new Poker.MockEventManager(beforeFunction,cleanUpFunction);

        var mockEvent = function(name,func,delay) {
            return new Poker.MockEvent(name,func,delay);
        };
        Poker.MyPlayer.id = 0;
        Poker.MyPlayer.name= "test";
        $(".table-view-container").show();
        this.mockEventManager.addEvent(
            mockEvent("Add players",function(){
                for(var i = 0; i<capacity; i++) {
                    self.addPlayer(i,i,"Cool_P2_layerLongName"+i);
                }
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("Update state",function(){
                var bl = new com.cubeia.games.poker.io.protocol.BlindsLevel();
                bl.bigBlind = "1";
                bl.smallBlind = "0.5";
                bl.isBreak = false;
                self.tableManager.notifyGameStateUpdate(self.tableId,capacity, bl ,0,com.cubeia.games.poker.io.protocol.BetStrategyEnum.NO_LIMIT,self.variant.id,null);
            }));
        this.mockEventManager.addEvent(
            mockEvent("Deal cards",function(){
                for(var i = 0; i<capacity; i++) {
                    self.dealCards(i,i);
                }
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("Player 0 small blind",function(){
                self.playerAction(0,Poker.ActionType.SMALL_BLIND);
            })
        );

        this.mockEventManager.addEvent(
            mockEvent("Update main pots", function(){
                self.tableManager.updateTotalPot(self.tableId,10000);
                self.tableManager.updatePots(self.tableId,[new Poker.Pot(0,Poker.PotType.MAIN,10000)]);
            })
        );


        this.mockEventManager.addEvent(
            mockEvent("Player 1 big blind",function(){
                self.playerAction(1,Poker.ActionType.BIG_BLIND);
            })
        );

        this.mockEventManager.addEvent(
            mockEvent("Activate player 2", function(){
                self.tableManager.handleRequestPlayerAction(self.tableId,2,
                    [
                        new Poker.Action(Poker.ActionType.FOLD,0,0),
                        new Poker.Action(Poker.ActionType.CALL,10,10),
                        new Poker.Action(Poker.ActionType.RAISE,10,1000000)
                    ],15000)
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("Player 2 call",function(){
                self.playerAction(2,Poker.ActionType.CALL);
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("Player 3 fold",function(){
                self.playerAction(3,Poker.ActionType.FOLD,0);
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("Player 4 raise",function(){
                self.playerAction(4,Poker.ActionType.RAISE);
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("Player 5 raise",function(){
                self.playerAction(5,Poker.ActionType.RAISE);
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("All players call",function(){
                self.playerAction(6,Poker.ActionType.CALL);
                self.playerAction(7,Poker.ActionType.CALL);
                self.playerAction(8,Poker.ActionType.CALL);
                self.playerAction(9,Poker.ActionType.CALL);
                self.playerAction(0,Poker.ActionType.CALL);
                self.playerAction(1,Poker.ActionType.CALL);
                self.playerAction(2,Poker.ActionType.CALL);
                self.playerAction(4,Poker.ActionType.CALL);
            })
        );

        this.mockEventManager.addEvent(
            mockEvent("Deal flop", function(){
                self.tableManager.dealCommunityCards(self.tableId,[
                    { id : self.cardIdSeq++, cardString : self.getCardString(self.cardIdSeq) },
                    { id : self.cardIdSeq++, cardString : self.getCardString(self.cardIdSeq) },
                    { id : self.cardIdSeq++, cardString : self.getCardString(self.cardIdSeq) }

                ]);
            })
        );


        this.mockEventManager.addEvent(
            mockEvent("Deal turn", function(){
                self.tableManager.dealCommunityCards(self.tableId,
                [{ id : self.cardIdSeq++, cardString : self.getCardString(self.cardIdSeq) }]);
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("Deal river", function(){
                self.tableManager.dealCommunityCards(self.tableId,[{ id : self.cardIdSeq++, cardString : self.getCardString(self.cardIdSeq) }]);
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("Future actions",function(){
              self.tableManager.onFutureAction(self.tableId,[
                  Poker.ActionType.FOLD,
                  Poker.ActionType.CALL,
                  Poker.ActionType.RAISE
              ],100,200);
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("Request Player Action",function(){
                self.tableManager.handleRequestPlayerAction(self.tableId,0,
                    [
                        new Poker.Action(Poker.ActionType.FOLD,0,0),
                        new Poker.Action(Poker.ActionType.CALL,10,10),
                        new Poker.Action(Poker.ActionType.RAISE,10,1000000)
                    ],15000)
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("Player 1 bet blind",function(){
                self.playerAction(0,Poker.ActionType.BET);
            })
        );

        this.mockEventManager.addEvent(
            mockEvent("Expose private cards",function(){
                self.tableManager.exposePrivateCards(self.tableId,self.getPlayerCards());
            })
        );

        this.mockEventManager.addEvent(mockEvent("Update pots", function(){
            self.tableManager.updatePots(self.tableId, [new Poker.Pot(0,Poker.PotType.MAIN,9000),
                new Poker.Pot(1,Poker.PotType.SIDE,1000)] );
        }));
        this.mockEventManager.addEvent(mockEvent("End hand", function(){
            var bestHands = [];

            var bh = new com.cubeia.games.poker.io.protocol.BestHand();
            bh.handType =  com.cubeia.games.poker.io.protocol.HandTypeEnum.THREE_OF_A_KIND;
            bh.player = 1;
            bh.cards = [self.getCard(1,"s"),self.getCard(2,"d")];
            bestHands.push(bh);


            var potTransfers = new com.cubeia.games.poker.io.protocol.PotTransfers();
            potTransfers.fromPlayerToPot = false;

            potTransfers.transfers = [self.getPotTransfer(0,1,8000),self.getPotTransfer(0,2,1000),self.getPotTransfer(1,2,1000)];

            self.tableManager.endHand(self.tableId,bestHands,potTransfers);
        }));
    },
    getPlayerCards : function() {
        var cards = [];

        var count = 0;
        for(var i = 0; i<11; i++) {
            for(var j = 0; j<this.variant.numCards; j++) {
                var card = this.getProtocolCard(count);
                cards.push({player : i , card : { cardId : count, rank : card.rank, suit:card.suit}});
                count++;
            }
       }
        return cards;

    },
    getPotTransfer : function(potId,playerId,amount) {
        var pt = new com.cubeia.games.poker.io.protocol.PotTransfer();
        pt.amount = amount;
        pt.playerId = playerId;
        pt.potId = potId;
        return pt;
    },
    getCard : function(rank,suit) {
        var card1 = new com.cubeia.games.poker.io.protocol.GameCard();
        card1.rank = rank;
        card1.suit = suit;
        return card1;
    },

    addPlayer : function(seat,playerId,name) {
        this.tableManager.addPlayer(this.tableId,seat,playerId, name);
        this.tableManager.updatePlayerStatus(this.tableId, playerId, Poker.PlayerTableStatus.SITTING_IN);
        this.tableManager.updatePlayerBalance(this.tableId,playerId, 100000);
    },
    dealCards : function(seat,playerId) {
        var numCards = this.variant.numCards;
        if(playerId == Poker.MyPlayer.id) {
            for(var i = 0; i<numCards; i++) {
                this.tableManager.dealPlayerCard(this.tableId,playerId,this.cardIdSeq,this.getCardString(this.cardIdSeq));
                this.cardIdSeq++;
            }
        } else {
            for(var i = 0; i<numCards; i++) {
                this.tableManager.dealPlayerCard(this.tableId,playerId,this.cardIdSeq,"  ");
                this.cardIdSeq++;
            }
        }

    },
    getCardString : function(cardId) {

        return Poker.Utils.getCardString(this.getProtocolCard(cardId));
    },
    getProtocolCard : function(cardId) {
        var suit = cardId%4;
        var rank = cardId%13;
        return {suit:suit,rank:rank};
    },
    playerAction : function(playerId,action,amount) {
        if(playerId>=this.capacity) {
            return;
        }
        if(!amount) {
            amount = 10000
        }
        this.tableManager.handlePlayerAction(this.tableId,playerId,action,amount);
    },

    getRandomCard : function() {

    }
});

$(document).ready(function(){

    if(document.location.hash.indexOf("dev")!=-1){
        console.log("dev mode enabled");
        var dt = new Poker.DevTools();
        setTimeout(function(){dt.launch();},1000);
    }
});
