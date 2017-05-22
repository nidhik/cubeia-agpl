"use strict";

var Poker = Poker || {};

Poker.BasicMenu = Class.extend({
    container : null,
    items : null,
    init : function(container) {
        this.container = $(container);
        this.items = new Poker.Map();
    },
    addItem : function(item,clickFunction) {
        var self = this;
        this.items.put(item,clickFunction);
        this.container.find(item).off().click(function(evt){
            self.selectItem(item);
        });
    },
    selectItem : function(item) {
        console.log("select item : ", item);
        this.activateItem(item);
        this.items.get(item)();
    },
    activateItem : function(item) {
        this.container.show();
        this.container.find(".active").removeClass("active");
        var activeItem = this.container.find(item);
        activeItem.addClass("active");
        this.container.find(".nav-active-item").html(activeItem.find("a").html());
    },
    hide : function() {
        this.container.hide();
    }

});