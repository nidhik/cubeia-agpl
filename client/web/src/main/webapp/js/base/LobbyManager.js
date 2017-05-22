"use strict";
var Poker = Poker || {};
/**
 * Handles the lobby UI
 * @type {Poker.LobbyManager}
 */
Poker.LobbyManager = Class.extend({

    /**
     * @type {Poker.LobbyData}
     */
    cashGamesLobbyData : null,

    tournamentLobbyData : null,

    sitAndGoLobbyData : null,

    listItemTemplate:null,

    currentScroll:null,

    sitAndGoState : false,

    /**
     * @type Poker.LobbyLayoutManager
     */
    lobbyLayoutManager : null,

    /**
     * @constructor
     */
    init: function () {
        var self = this;
        this.lobbyLayoutManager = Poker.AppCtx.getLobbyLayoutManager();
        this.cashGamesLobbyData = new Poker.LobbyData(new Poker.TableLobbyDataValidator(),
            function(items) {
                var sort = self.cashGamesLobbyData.getCurrentSort();
                self.lobbyLayoutManager.setCurrentSorting(sort.attribute,sort.isAscending());
                self.lobbyLayoutManager.createTableList(items);
            },
            function(items){
                self.lobbyLayoutManager.updateTableItems(items);
            },
            function(itemId) {
                self.lobbyLayoutManager.tableRemoved(itemId);
            });

        this.cashGamesLobbyData.addSort(new Poker.NameSort(false));
        this.cashGamesLobbyData.addSort(new Poker.CapacitySort(false));
        this.cashGamesLobbyData.addSort(new Poker.BlindsSort(false));

        this.lobbyLayoutManager.setCashGameSortingFunction(function(attribute,asc){
            self.cashGamesLobbyData.setSortBy(attribute,asc);
        });


        this.tournamentLobbyData = new Poker.LobbyData(new Poker.TournamentLobbyDataValidator(),
            function(items) {
                self.lobbyLayoutManager.createTournamentList(items);
            },
            function(items){
                self.lobbyLayoutManager.updateTournamentItems(items);
            },
            function(itemId) {
                self.lobbyLayoutManager.tournamentRemoved(itemId);
            });

        this.sitAndGoLobbyData = new Poker.LobbyData(new Poker.TournamentLobbyDataValidator(),
            function(items) {
                var sort = self.sitAndGoLobbyData.getCurrentSort();
                self.lobbyLayoutManager.setCurrentSorting(sort.attribute,sort.isAscending());
                self.lobbyLayoutManager.createSitAndGoList(items);

            },
            function(items){
                self.lobbyLayoutManager.updateSitAndGoItems(items);
            },
            function(itemId) {
                self.lobbyLayoutManager.tournamentRemoved(itemId);
            });

        this.sitAndGoLobbyData.addSort(new Poker.CapacitySort(false));
        this.sitAndGoLobbyData.addSort(new Poker.BuyInSort(false));


        this.lobbyLayoutManager.setSitAndGoSortingFunction(function(attribute,asc){
            self.sitAndGoLobbyData.setSortBy(attribute,asc);
        });

        $("#leaderboard").cs_leaderboard({
            global : true
        });

    },
    onLogin : function(reconnecting) {
        if(reconnecting) {
            Poker.Subscribe();
        }
        this.lobbyLayoutManager.onLogin(reconnecting);

    },
    handleTableSnapshotList : function (tableSnapshotList) {
        var items = [];
        for (var i = 0; i < tableSnapshotList.length; i++) {
            items.push(Poker.ProtocolUtils.extractTableData(tableSnapshotList[i]));
        }
        this.cashGamesLobbyData.addItems(items);
    },
    handleTournamentSnapshotList : function (tournamentSnapshotList) {
        if(tournamentSnapshotList.length>0 && tournamentSnapshotList[0].address.indexOf("/sitandgo")!=-1) {
            this.lobbyLayoutManager.state = Poker.LobbyLayoutManager.SIT_AND_GO_STATE;
        } else {
            this.lobbyLayoutManager.state = Poker.LobbyLayoutManager.TOURNAMENT_STATE;
        }

        var items = [];
        for (var i = 0; i < tournamentSnapshotList.length; i++) {
            items.push(Poker.ProtocolUtils.extractTournamentData(tournamentSnapshotList[i]));
        }
        if(this.lobbyLayoutManager.state == Poker.LobbyLayoutManager.SIT_AND_GO_STATE) {
            this.sitAndGoLobbyData.addItems(items);
        } else {
            this.tournamentLobbyData.addItems(items);
        }
    },

    /**
     *
     * @param {FB_PROTOCOL.TournamentUpdatePacket[]} tournamentUpdateList
     */
    handleTournamentUpdates : function (tournamentUpdateList) {
        var items = [];
        for (var i = 0; i < tournamentUpdateList.length; i++) {
            items.push(Poker.ProtocolUtils.extractTournamentData(tournamentUpdateList[i]));
        }

        if(this.lobbyLayoutManager.state == Poker.LobbyLayoutManager.SIT_AND_GO_STATE) {
            this.sitAndGoLobbyData.updateItems(items);
        } else {
            this.tournamentLobbyData.updateItems(items);
        }

    },

    getTableStatus:function (seated, capacity) {
        if (seated == capacity) {
            return "full";
        }
        return "open";
    },
    getBettingModel : function (model) {
        if (model == "NO_LIMIT") {
            return "NL"
        } else if (model == "POT_LIMIT") {
            return "PL";
        } else if (model == "FIXED_LIMIT") {
            return "FL";
        }
        return model;
    },
    handleTableUpdateList : function (tableUpdateList) {
        var items = [];
        for (var i = 0; i < tableUpdateList.length; i++) {
            items.push(Poker.ProtocolUtils.extractTableData(tableUpdateList[i]));
        }
        this.cashGamesLobbyData.updateItems(items);
    },
    handleTableRemoved : function(tableId) {
        this.cashGamesLobbyData.remove(tableId);
    },
    handleTournamentRemoved : function(tournamentId) {
        console.log("REMOVE TOURNAMENT = " + tournamentId);
        if(this.lobbyLayoutManager.state = Poker.LobbyLayoutManager.SIT_AND_GO_STATE) {
            this.sitAndGoLobbyData.remove(tournamentId);
        } else {
            this.tournamentLobbyData.remove(tournamentId);
        }
    },
    clearLobby : function () {
        this.cashGamesLobbyData.clear();
        this.tournamentLobbyData.clear();
        this.sitAndGoLobbyData.clear();
        $("#tableListContainer").empty();
    },

    getCapacity:function (id) {
        var tableData = this.cashGamesLobbyData.getItem(id);
        return tableData.capacity;
    }

});

Poker.Filter = Class.extend({
    init : function () {
    },
    filterUpdated : function () {

    },
    /**
     * Returns true if it should be included in the lobby and
     * false if it shouldn't
     * @param lobbyData
     * @return {boolean} if it should be included
     */
    filter: function (lobbyData) {
        return false;
    }
});
Poker.LobbyFilter = Poker.Filter.extend({
    enabled:false,
    id:null,
    filterFunction:null,
    lobbyLayoutManager:null,
    checkbox : false,

    init : function (id, enabled, filterFunction, lobbyLayoutManager) {
        var userSetting = Poker.Utils.loadBoolean(id, enabled);
        this.enabled = userSetting;
        this.id = id;
        this.filterFunction = filterFunction;
        this.lobbyLayoutManager = lobbyLayoutManager;

        var self = this;
        var input = $("#" + this.id);
        if(input.attr("type")=="checkbox") {
            this.checkbox = true;
            input.on("change",this.clickHandler(self));
            if (this.enabled == true) {
                input.attr("checked",true);
            } else {
                input.attr("checked",false);
            }
        } else {
            input.touchSafeClick(this.clickHandler(self));
            if (this.enabled == true) {
                $("#" + this.id).addClass("active");
            } else {
                $("#" + this.id).removeClass("active");
            }
        }

    },
    filterUpdated : function () {
        this.lobbyLayoutManager.filterUpdated();
    },
    /**
     * Returns true if it should be included in the lobby and
     * false if it shouldn't
     * @param lobbyData
     * @return {boolean} if it should be included
     */
    filter: function (lobbyData) {
        return this.filterFunction(this.enabled, lobbyData);
    },
    clickHandler : function (self) {
        return function () {
            self.enabled = !self.enabled;
            if(self.checkbox == false) {
                $(this).toggleClass("active");
            }
            Poker.Utils.store(self.id, self.enabled);
            self.filterUpdated();
        }
    }
});

Poker.PropertyMinMaxFilter = Poker.LobbyFilter.extend({
    min:-1,
    max:-1,
    property:null,
    init:function (id, enabled, lobbyLayoutManager, property, min, max) {
        this.min = min;
        this.max = max;
        this.property = property;
        var self = this;
        this._super(id, enabled, function (enabled, lobbyData) {
            return self.doFilter(enabled, lobbyData);
        }, lobbyLayoutManager);

    },
    doFilter:function (enabled, lobbyData) {
        var p = lobbyData[this.property];
        if (typeof(p) != "undefined" && !this.enabled) {
            p = parseFloat(p);
            if (this.max != -1 && this.min != -1) {
                return p > this.max || p < this.min;
            } else if (this.max != -1) {
                return p > this.max;
            } else if (this.min != -1) {
                return p < this.min;
            } else {
                console.log("PropertyFilter: neither min or max is defined");
                return true;
            }
        } else {
            return true;
        }
    }
});

Poker.PropertyStringFilter = Poker.LobbyFilter.extend({
    str:null,
    property:null,
    init:function (id, enabled, lobbyLayoutManager, property, str) {
        this.property = property;
        this.str = str;
        var self = this;
        this._super(id, enabled, function (enabled, lobbyData) {
            return self.doFilter(enabled, lobbyData);
        }, lobbyLayoutManager);

    },
    doFilter : function (enabled, lobbyData) {
        var p = lobbyData[this.property];
        if (typeof(p) != "undefined" && !this.enabled) {
            return (p !== this.str);
        } else {
            return true;
        }
    }
});

Poker.EqualsFilter = Poker.PropertyStringFilter.extend({

    init:function (id, enabled, lobbyLayoutManager, property, str) {
        this._super(id, enabled, lobbyLayoutManager, property, str);
    },
    doFilter : function (enabled, lobbyData) {
        var p = lobbyData[this.property];
        if (typeof(p) != "undefined" && this.enabled==true) {
            return (p == this.str);
        } else {
            return true;
        }
    }
});

Poker.RadioGroupFilter = Poker.Filter.extend({
    /**
     * A list of filters that should be treated as a group of radio buttons.
     * That is, when one of the filters is enabled, all other filters should be
     * disabled.
     */
    radioGroup : null,
    lobbyLayoutManager : null,
    properties : null,
    prefix : null,
    currentFilter : null,
    idProperty : "id",
    emptyFilterGroups : null,
    autoHideEmpty : true,
    init : function(group, lobbyLayoutManager, properties,prefix,idProperty, autoHideEmpty) {
        var self = this;
        if(typeof(autoHideEmpty)!="undefined") {
            this.autoHideEmpty = autoHideEmpty;
        }
        if(typeof(prefix)!="undefined") {
            this.prefix = prefix;
        } else {
            this.prefix = "filterButton";
        }
        if(typeof(idProperty)!="undefined"){
            this.idProperty = idProperty;
        }

        this.radioGroup = group;
        this.lobbyLayoutManager = lobbyLayoutManager;
        this.properties = properties;
        var prefix = this.prefix;
        this.currentFilter = group[0][this.idProperty];
        $("#" + prefix + this.currentFilter).addClass("active");
        this.reset();
        $.each(group,function(i,el){
            var link = $("#" + prefix + el[self.idProperty]);
            link.touchSafeClick(function(e){
                self.deselectButtons();
                self.currentFilter = el[self.idProperty];
                $(this).addClass("active");
                self.filterUpdated();
            });
            if(self.autoHideEmpty) {
               link.hide();
            }
        });

    },
    reset : function() {
        if(this.autoHideEmpty == true) {
            var self = this;
            this.emptyFilterGroups = new Poker.Map();
            $.each(this.radioGroup,function(i,e){
                self.emptyFilterGroups.put(e[self.idProperty],e);
            });
        }

    },
    deselectButtons : function() {
        var self = this;
        $.each(this.radioGroup, function(i,el){
            $("#" + self.prefix + el[self.idProperty]).removeClass("active");
        });
    },
    filter : function(lobbyData) {
        for(var i = 0; i<this.properties.length; i++) {
            if(this.filterSingle(this.properties[i],lobbyData) == false) {
                return false;
            }
        }
        return true;
    },
    filterSingle : function(property,lobbyData) {
        var p = lobbyData[property];
        if (typeof(p) != "undefined" && !this.enabled) {
            if(this.autoHideEmpty==true) {
                this.emptyFilterGroups.remove(p);
            }
            return (p == this.currentFilter);
        } else {
            return true;
        }
    },
    filterUpdated : function () {
        this.lobbyLayoutManager.filterUpdated();
    },
    hideEmptyFilters : function() {
        if(this.autoHideEmpty==true) {
            var self = this;
            $.each(this.radioGroup,function(i,e){
                $("#" + self.prefix + e[self.idProperty]).show();
            });
            $.each(this.getEmptyFilterGroups(),function(i,e){
                $("#" + self.prefix + e).hide();
            });
        }
        $.each(this.radioGroup,function(i,e){
            var item = $("#" + self.prefix + e[self.idProperty]);
            if(item.hasClass("active") && !item.is(":visible") ) {
                $.each(self.radioGroup,function(j,g){
                    var select = $("#" + self.prefix + g[self.idProperty]);
                    if(select.is(":visible")){
                        select.click();
                        return false;
                    }
                });
                return false;
            }
        });

    },
    getEmptyFilterGroups : function(){
        return this.emptyFilterGroups.keys();
    }


});

Poker.PrivateTournamentFilter = Poker.Filter.extend({

    init : function() {

    },
    filter: function(lobbyData) {
        var operatorId = Poker.SkinConfiguration.operatorId;
        var stringList = lobbyData["operatorIds"];
        if (!stringList || stringList.length == 0) {
            return true; // this is a table, or no operators set
        } else if (!operatorId) {
            // this is a private tournament, but the player has no operator (?!), deny
            return false;
        } else {
            var stringArr = stringList.split(",");
            return $.inArray(operatorId.toString(), stringArr) != -1; // allow if in array
        }
    }
});





