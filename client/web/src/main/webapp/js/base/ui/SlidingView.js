"use strict";
Poker = Poker || {};
Poker.SlidingView = Class.extend({
    viewElement : null,
    init : function(viewElement) {
        this.viewElement = viewElement;
    },
    toggle : function() {
        $(".view-container").toggleClass("no-overflow");
        $(".view-port").toggleClass("no-overflow-x");
        $(this.viewElement).toggleClass('visible');
        $(".view-container").toggleClass("slided");
        $(".menu-overlay").toggle();
    }
});