    "use strict";

var Poker = Poker || {};

Poker.TournamentList = Poker.Pager.extend({
    container : null,
    playerProfiles : null,
    pagerContainer : null,
    filterText : null,
    tournamentId : -1,
    init : function(tournamentId,container, pagerContainer, filterInput) {
        var self = this;
        this._super(15);
        this.tournamentId = tournamentId;
        this.container = container;
        this.pagerContainer = pagerContainer;
        this.playerProfiles = new Poker.Map();
        filterInput.on("keyup",function(e){
            self.updateFilter($(this).val());
        });

    },
    updateFilter : function(text) {
        this.filterText = $.trim(text).toLowerCase();
        this.updateItems();

    },
    displayItems : function(players) {
        var template = Poker.AppCtx.getTemplateManager().getRenderTemplate("tournamentPlayerListItem");
        var height = this.container.parent().height();
        this.container.parent().height(height+"px");
        this.container.empty();
        var self = this;
        $.each(players,function(i,p) {
            self.container.append(template.render(p));
            if(p.tableId!=null && p.tableId>=0) {
                self.container.find(".go-to-table-"+p.playerId).show().click(function(e){
                    new Poker.TableRequestHandler(p.tableId).openTournamentTable(self.tournamentId,10);
                });
            }

            if(self.playerProfiles.contains(p.playerId)) {
                self.updateAvatar(p.playerId,self.playerProfiles.get(p.playerId));
            } else {
                Poker.AppCtx.getPlayerApi().requestPlayerProfile(p.playerId, Poker.MyPlayer.loginToken, function(data){
                    self.playerProfiles.put(p.playerId,data);
                    self.updateAvatar(p.playerId,data);
                },function(){
                    console.log("Error fetching avatar for player " + p.playerId);
                });
            }

        });
        if(players.length==0) {
            this.container.append   ("<td/>").attr("colspan","3").
                append(i18n.t("tournament-lobby.players.no-players"));
        }
        if(this.getNrOfPages()<2) {
            this.pagerContainer.hide();
        } else {
            this.pagerContainer.show();
        }
        this.pagerContainer.find(".active").removeClass("active");
        this.pagerContainer.find(".page-"+this.activePage).addClass("active");
        this.container.parent().height("");
    },
    setItems : function(items) {
        this._super(items);
        this.createPages();
    },
    setPage : function(page) {
        this._super(page);
    },
    filter : function(items) {
        if(this.filterText==null || this.filterText.length<2) {
            return items;
        }
        var self = this;
        var filtered = [];
        $.each(items,function(i,player){
            if(player.name.toLowerCase().indexOf(self.filterText)!=-1) {
                filtered.push(player);
            }
        });
        return filtered;
    },
    createPages : function() {
        var self = this;
        this.pagerContainer.empty();
        var pages = this.getNrOfPages();
        var prev = $("<div/>").addClass("page-previous").html("<<");
        this.pagerContainer.append(prev);
        prev.click(function(e){ self.previous();})
        for(var i = 0; i<pages; i++) {
            var page = $("<div/>").addClass("page-selector").addClass("page-"+i).html(i+1);
            if(this.activePage == i) {
                page.addClass("active");
            }
            this.pagerContainer.append(page);
            (function(index){
                page.click(function(e){
                    self.setPage(index);
                });

            })(i);
        }
        var next = $("<div/>").addClass("page-next").html(">>");
        this.pagerContainer.append(next);
        next.click(function(e){ self.next();})
    },
    updateAvatar : function(playerId, profile) {
        if(profile.externalAvatarUrl){
            this.container.find(".player-"+playerId + " .generic-avatar ").addClass("player").css({backgroundImage : "url('"+profile.externalAvatarUrl+"')"});
        }
        if(profile.level) {
            this.container.find(".player-"+playerId + " .level").show().addClass("level-"+profile.level);
        }
    }
});