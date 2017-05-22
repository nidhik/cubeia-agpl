"use strict";
var Poker = Poker || {};

/**
 *
 * @type {Poker.TournamentManager}
 */
Poker.TournamentLayoutManager = Class.extend({
    viewContainer : null,
    tournamentId :-1,

    /**
     * @type Poker.TemplateManager
     */
    templateManager : null,

    viewElement : null,
    playerListBody : null,
    tableListBody : null,
    registerButton : null,
    unregisterButton : null,
    loadingButton : null,
    leaveButton : null,
    leaveFunction : null,
    takeSeatButton : null,
    name : null,
    shareDone : false,
    chat : null,
    tournamentIsFull : null,
    playerList : null,

    registeredPlayers : null,
    maxPlayers : null,

    init : function(tournamentId, name, registered, viewContainer,leaveFunction) {
        this.leaveFunction = leaveFunction;
        this.tournamentId = tournamentId;
        this.viewContainer = viewContainer;
        this.name = name;
        this.templateManager = Poker.AppCtx.getTemplateManager();

        var viewHTML = this.templateManager.render("tournamentTemplate",{tournamentId : tournamentId, name : name});

        viewContainer.append(viewHTML);

        var viewId = "#tournamentView"+tournamentId;
        this.viewElement = $(viewId);
        this.playerListBody = this.viewElement.find(".player-list tbody");
        this.tableListBody = this.viewElement.find(".table-list tbody");
        var playerListPager = this.viewElement.find(".player-list-pager");
        var filterInput = this.viewElement.find(".filter-input");
        this.playerList = new Poker.TournamentList(tournamentId,this.playerListBody,playerListPager,filterInput);
        this.initActions();
        if(registered==true) {
            this.setPlayerRegisteredState();
        }
        var self = this;

        var menu = new Poker.BasicMenu(this.viewElement.find(".tournament-navbar"));
        menu.addItem(".players-link", function(){
            self.viewElement.find(".tournament-section").hide();
            self.viewElement.find(".players-row").show();
        });
        menu.addItem(".payouts-link", function(){
            self.viewElement.find(".tournament-section").hide();
            self.viewElement.find(".payouts-row").show();
        });
        menu.addItem(".blinds-link", function(){
            self.viewElement.find(".tournament-section").hide();
            self.viewElement.find(".blinds-row").show();
        });
        menu.addItem(".tables-link", function(){
            self.viewElement.find(".tournament-section").hide();
            self.viewElement.find(".tables-row").show();
        });
        menu.activateItem(".players-link");
        this.chat = new Poker.TableEventLog(this.viewElement.find(".table-chat-container"));
        new Poker.ChatInput(this.viewElement.find(".chat-input"), function(msg){
            new Poker.TournamentRequestHandler(tournamentId).sendChatMessage(Poker.Utils.filterMessage(msg));
        });

    },
    onChatMessage : function(screenName,message) {
        this.chat.appendChatMessage({name : screenName },message);
    },
    updatePlayerList : function(players) {
        this.playerList.setItems(players);
        this.registeredPlayers = players.length;
        this.updateFullInfo();
    },
    updateFullInfo : function() {
        if(this.registeredPlayers!=null && this.maxPlayers!=null) {
            if(this.registeredPlayers>=this.maxPlayers) {
                this.tournamentIsFull.show();
                this.registerButton.addClass("full");
            } else {
                this.registerButton.removeClass("full");
                this.tournamentIsFull.hide();
            }
        } else {
            this.registerButton.removeClass("full");
            this.tournamentIsFull.hide();
        }
    },
    updateTableList : function(tables) {
        var template = this.templateManager.getRenderTemplate("tournamentTableListItem");
        this.tableListBody.empty();
        var self = this;
        tables.sort();
        $.each(tables,function(i,t) {
            self.tableListBody.append(template.render({index : (i+1), id : t }));
            self.tableListBody.find("#tournamentTable"+t).click(function(e){
                new Poker.TableRequestHandler(t).openTournamentTable(self.tournamentId,10);
            });
        });
        if(tables.length==0) {
            this.tableListBody.append("<td/>").attr("colspan","2").
                append(i18n.t("tournament-lobby.tables.no-tables"));
        }
    },
    updateBlindsStructure : function(blindsStructure) {
        var blindsTemplate = this.templateManager.getRenderTemplate("tournamentBlindsStructureTemplate");
        this.viewElement.find(".blinds-structure").html(blindsTemplate.render(blindsStructure));

    },
    updateTournamentInfo : function(info) {
        this.viewElement.find(".tournament-name-title").html(info.tournamentName);
        if(info.userRuleExpression!=null) {
            var level = Poker.ProtocolUtils.parseLevel(info.userRuleExpression) + 1;

            if(level>1) {
                this.viewElement.find(".requires-level").show();
                this.viewElement.find(".requires-level .level").attr("class","").addClass("level").addClass("level-"+level).show();
                if(level>Poker.AppCtx.getProfileManager().myPlayerProfile.level) {
                    this.viewElement.find(".requires-level .lock").show();
                } else {
                    this.viewElement.find(".requires-level .lock").hide();
                }
            }
        }
        var sitAndGo = false;
        if(info.maxPlayers == info.minPlayers) {
            sitAndGo = true;
        } else {
            this.maxPlayers = info.maxPlayers;
        }
        $.extend(info,{sitAndGo : sitAndGo,tournamentId : this.tournamentId});
        var infoTemplate = this.templateManager.getRenderTemplate("tournamentInfoTemplate");
        this.viewElement.find(".tournament-info").html(infoTemplate.render(info));

        if(info.sitAndGo==false) {
            var m = moment(parseInt(info.startTime));
            this.viewElement.find(".tournament-start-date").html(m.format("lll") + " ("+ m.fromNow()+")");
        }

        if(info.description!=null && info.description!=""){
            this.viewElement.find(".tournament-description").html(info.description);
        } else {
            this.viewElement.find(".tournament-description").hide();
        }
        if(this.shareDone==false) {
            this.shareDone=true;
        }
        this.updateFullInfo();
    },
    updateTournamentStatistics : function(statistics) {
        if(statistics.playersLeft.remainingPlayers>0) {
            this.viewElement.find(".tournament-statistics").show();
            this.viewElement.find(".remaining-players").html(statistics.playersLeft.remainingPlayers);
        } else {
            this.hideTournamentStatistics();
        }

    },
    hideTournamentStatistics : function()  {
        this.viewElement.find(".tournament-statistics").hide();
    },
    updatePayoutInfo : function(payoutInfo) {
        var payoutTemplate = this.templateManager.getRenderTemplate("tournamentPayoutStructureTemplate");
        this.viewElement.find(".payout-structure").html(payoutTemplate.render(payoutInfo));
    },
    initActions : function() {
        this.leaveButton = this.viewElement.find(".leave-action");
        this.registerButton = this.viewElement.find(".register-action");
        this.unregisterButton = this.viewElement.find(".unregister-action");
        this.loadingButton =  this.viewElement.find(".loading-action").hide();
        this.takeSeatButton =  this.viewElement.find(".take-seat-action").hide();
        this.tournamentIsFull = this.viewElement.find(".tournament-full");

        var self = this;
        this.leaveButton.touchSafeClick(function(e){
            self.leaveLobby();
        });
        this.registerButton.touchSafeClick(function(e){
            var tournamentRequestHandler = new Poker.TournamentRequestHandler(self.tournamentId);
            tournamentRequestHandler.requestBuyInInfo();
        });
        this.unregisterButton.hide().touchSafeClick(function(e){
            var tournamentRequestHandler = new Poker.TournamentRequestHandler(self.tournamentId);
            $(this).hide();
            self.loadingButton.show();
            tournamentRequestHandler.unregisterFromTournament();

        });
        this.takeSeatButton.touchSafeClick(function(e){
            var tournamentRequestHandler = new Poker.TournamentRequestHandler(self.tournamentId);
            tournamentRequestHandler.takeSeat();
        });
    },

    onFailedRegistration : function() {
        this.setPlayerUnregisteredState();
    },
    onFailedUnregistraion : function() {
        this.setPlayerRegisteredState();
    },
    setTournamentNotRegisteringState : function(registered){
        if(registered) {
            this.takeSeatButton.show();
        } else {
            this.takeSeatButton.hide();
        }
        this.loadingButton.hide();
        this.registerButton.hide();
        this.unregisterButton.hide();
        this.tournamentIsFull.hide();
    },
    setPlayerRegisteredState : function() {
        this.loadingButton.hide();
        this.registerButton.hide();
        this.unregisterButton.show();
    },
    setPlayerUnregisteredState : function() {
        this.loadingButton.hide();
        this.registerButton.show();
        this.unregisterButton.hide();
    },
    getViewElementId : function() {
        return this.viewElement.attr("id");
    },
    leaveLobby : function() {
        new Poker.TournamentRequestHandler(this.tournamentId).leaveTournamentLobby();
    },
    showBuyInInfo : function(buyIn, fee, currency, balanceInWallet) {
        var buyInDialog = new Poker.TournamentBuyInDialog();
        buyInDialog.show(this.tournamentId,this.name,buyIn,fee,balanceInWallet,currency);
    }

});
