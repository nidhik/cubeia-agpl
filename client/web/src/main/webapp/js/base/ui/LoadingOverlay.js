"use strict";
var Poker = Poker || {};

Poker.LoadingOverlay = Class.extend({
    element : null,
    init : function() {
        this.element = $("#topLoadingOverlay");
    },
    show : function(){
        this.element.show();
    },
    hide : function() {
        this.element.hide();
    }
});