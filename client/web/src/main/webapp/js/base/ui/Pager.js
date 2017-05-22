"use strict";
var Poker = Poker || {};

Poker.Pager = Class.extend({
    items : null,
    itemsPerPage : 20,
    activePage : 0,
    init : function(itemsPerPage){
        this.itemsPerPage = itemsPerPage;
    },
    setItems : function(items) {
        this.items = items;
        this.updateItems();
    },
    updateItems : function() {
        var items = this.filter(this.items);
        var start = this.itemsPerPage * this.activePage;
        if(start>(items.length-1)) {
            this.activePage = this.getNrOfPages()-1;
            start = this.itemsPerPage * this.activePage;
        }
        var end = this.itemsPerPage * this.activePage + this.itemsPerPage;
        if(end>items.length) {
            end = items.length;
        }

        var slice = items.slice();
        this.displayItems(slice.splice(start,end-start));
    },
    displayItems : function(items) {

    },
    setPage : function(page){
        var last = this.getNrOfPages()-1;
        if(page>last) {
            page = last;
        }
        this.activePage = page;
        this.updateItems();
    },
    getNrOfPages : function() {
        var items = this.filter(this.items);
        var pages = Math.floor(items.length/this.itemsPerPage);
        if(items.length%this.itemsPerPage!=0) {
            pages++;
        }
        return pages;
    },
    filter : function(items) {
        return items;
    },
    previous : function() {
        if(this.activePage>0) {
            this.activePage--;
            this.updateItems();
        }
    },
    next : function() {
        if(this.activePage<this.getNrOfPages()-1) {
            this.activePage++;
            this.updateItems();
        }
    }

});