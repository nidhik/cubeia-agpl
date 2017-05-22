"use strict";
var Poker = Poker || {};
Poker.ResponsiveTabView = Poker.TabView.extend({
    init : function(viewElement,name,tabIndex) {
        this._super(viewElement,name,tabIndex);
        $(viewElement).addClass("responsive-view");
    },
    calculateFontSize : function() {

    }
});