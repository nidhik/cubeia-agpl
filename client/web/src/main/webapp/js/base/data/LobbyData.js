"use strict";

var Poker = Poker || {};

/**
 * Handles lobby data (for tables/tournaments)
 * Automatically merges existing items with snapshot updates
 * @type {Poker.LobbyData}
 */
Poker.LobbyData = Class.extend({
    items : null,
    validator : null,
    notifyUpdate : false,
    onUpdate : null,
    onCreate : null,
    onItemRemoved : null,
    sorters : null,
    /**
     *
     * @param {Poker.LobbyDataValidator} validator
     * @param {Function} onUpdate
     * @param {Function} onItemRemoved
     * @constructor
     */
    init : function(validator,onCreate,onUpdate,onItemRemoved){
        this.items = new Poker.Map();
        this.validator = validator;
        this.onUpdate = onUpdate;
        this.onCreate = onCreate;
        this.onItemRemoved = onItemRemoved;
        this.sorters = [];
    },
    updateItems : function(items) {
        var requiresRedraw = false;
        for(var i = 0; i<items.length; i++) {
            var item = items[i];
            var current = this.items.get(item.id);
            if(current!=null) {
                current = this._update(current,item);
                this.items.put(item.id,current);
                if(this.validator.shouldRemoveItem(current)) {
                    this.onItemRemoved(item.id);
                } else if(requiresRedraw==false) {
                    requiresRedraw = this._requiresRedraw(item);
                }
            }
        }
        if(requiresRedraw==true) {
          this.onCreate(this.getFilteredItems());
        } else if(this.notifyUpdate==true) {
            this.onUpdate(this.getFilteredItems());
        }
    },
    addItems : function(items) {
        for(var i = 0; i<items.length; i++) {
            this.addItem(items[i]);
        }
        if(this.notifyUpdate==true) {
            this.notifyUpdate==false;
            this.onCreate(this.getFilteredItems());
        }
    },
    remove : function(id) {
        var removed = this.items.remove(id);
        this.onItemRemoved(id);
    },
    clear : function() {
        this.items = new Poker.Map();
        this.notifyUpdate = false;
    },
    _requiresRedraw : function(item) {
        if(item.registered!=null || item.seated!=null || item.status!=null) {
           return true;
        }
        return false;
    },
    /**
     * @param item.id
     * @param [item.showInLobby]
     */
    addItem : function(item) {
        if(typeof(item.id)=="undefined") {
            console.log("No id in item, don't know what to do");
            return;
        }
        var current = item;
        this.items.put(item.id,current);

        if(this.validator.validate(current)) {
            this.notifyUpdate = true;
        }

    },
    _update : function(current,update) {

        for(var x in current) {
            if(typeof(update[x])!="undefined" && update[x]!=null) {
                current[x] = update[x];
            }
        }

        return current;
    },
    /**
     * Returns items that passes the Poker.LobbyDataValidator
     * validation step
     * @return {Array}
     */
    getFilteredItems : function() {
        var items = this.items.values();
        var filtered = [];
        for(var i = 0; i<items.length; i++) {
            if(!this.validator.shouldRemoveItem(items[i]) && this.validator.validate(items[i])==true) {
                filtered.push(items[i]);
            }
        }
        if(this.sorters.length>0) {
            filtered = this.sort(filtered);
        }
        return filtered;
    },
    sort : function(items) {
        var sorters = this.sorters;
        items = items.sort(function(a,b){
            for(var i = sorters.length-1; i>=0; i--) {
                var res = sorters[i].sort(a,b);
                if(res < 0 || res > 0) {
                    return res;
                }
            }
            return 0;
        });
        return items;

    },
    setSortBy : function(attribute,asc) {
        console.log("setting sort, ", this.sorters);
        var last = this.sorters.length-1;
        if(attribute == this.sorters[last].attribute) {
            this.sorters[last].ascending = asc;
        } else {
            for(var i = 0; i<this.sorters.length-1; i++) {
                if(this.sorters[i].attribute == attribute) {
                    this.sorters.push(this.sorters.splice(i,1)[0]);
                    this.sorters[last].ascending = asc;
                    break;
                }
            }
        }

        this.onCreate(this.getFilteredItems());

    },
    getItem : function(id) {
        return this.items.get(id);
    },
    addSort : function(sort) {
        this.sorters.push(sort);
    },
    getCurrentSort : function() {
        return this.sorters[this.sorters.length-1];
    }
});

/**
 * @type {Poker.LobbyDataValidator}
 */
Poker.LobbyDataValidator = Class.extend({
    /**
     * @param item
     * @return {Boolean}
     */
    validate : function(item) {
        return true;
    },
    shouldRemoveItem : function(item) {
        return item.showInLobby!=null && item.showInLobby == 0;
    }
});

/**
 * @type {Poker.TableLobbyDataValidator}
 * @extends {Poker.LobbyDataValidator}
 */
Poker.TableLobbyDataValidator = Poker.LobbyDataValidator.extend({
    init : function() {

    },
    validate : function(item) {
        return item.name!=null && item.capacity!=null;
    }

});

/**
 * @type {Poker.TournamentLobbyDataValidator}
 * @extends {Poker.LobbyDataValidator}
 */
Poker.TournamentLobbyDataValidator = Poker.LobbyDataValidator.extend({
    init : function() {

    },
    validate : function(item) {
        return item.name!=null && item.capacity!=null && item.status!=null && item.buyIn!=null ;
    },
    shouldRemoveItem : function(item) {
        return this._super(item) || (item.status!=null && item.status == "CLOSED");
    }

});

Poker.SortFunction = Class.extend({
    attribute : null,
    ascending : false,
    init : function(attribute, ascending) {
        this.ascending = ascending;
        this.attribute = attribute;
    },
    sort : function(a,b) {
    },
    isAscending : function() {
        return this.ascending==true ? 1 : -1;
    }
});
Poker.CapacitySort = Poker.SortFunction.extend({
    init : function(ascending) {
        this._super("capacity",ascending);
    },
    sort : function(a,b) {
        if(a.capacity == b.capacity) {
            if(typeof(a.seated)!="undefined") {
                return -(a.seated - b.seated);
            } else {
                return -(a.registered - b.registered)
            }

        }
        return this.isAscending() * (a.capacity - b.capacity);
    }
});



Poker.BlindsSort = Poker.SortFunction.extend({
    init : function(ascending) {
        this._super("blinds",ascending);
    },
    sort : function(a,b) {
        return this.isAscending() * (parseFloat(a.smallBlind) - parseFloat(b.smallBlind));
    }
});

Poker.BuyInSort = Poker.SortFunction.extend({
    init : function(ascending) {
        this._super("buy-in",ascending);
    },
    sort : function(a,b) {
        return this.isAscending() * (parseFloat(a.buyIn) - parseFloat(b.buyIn));
    }
});
Poker.NameSort = Poker.SortFunction.extend({
    init : function(ascending) {
        this._super("name",ascending);
    },
    sort : function(a,b) {
        if(a.name < b.name) {
            return this.isAscending() * 1;
        } else if(a.name > b.name) {
            return this.isAscending() * -1
        }
        return 0;
    }
});