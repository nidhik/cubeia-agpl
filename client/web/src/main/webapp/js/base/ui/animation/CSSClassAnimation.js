"use strict";
var Poker = Poker || {};

/**
 * A simple Poker.Animation based on css classes
 * @type {Poker.CSSClassAnimation}
 */
Poker.CSSClassAnimation = Poker.Animation.extend({
    classNames : null,
    init : function(element) {
        this._super(element);
        this.classNames = [];
    },
    addClass : function(className) {
        this.classNames.push(className);
        return this;
    },
    animate : function() {
        var el =  $(this.element);
        for(var i = 0; i<this.classNames.length; i++) {
            el.addClass(this.classNames[i]);
        }
    },
    next : function(el) {
        this.nextAnimation = new Poker.CSSClassAnimation(el || this.element);
        return this.nextAnimation;
    }
});

/**
 * A simple Poker.Animation based on css classes
 * @type {Poker.CSSClassAnimation}
 */
Poker.CSSAttributeAnimation = Poker.Animation.extend({
    attributes : null,
    init : function(element) {
        this._super(element);
        this.attributes = new Poker.Map();
    },
    addAttribute : function(attr,val) {
        this.attributes.put(attr,val);
        return this;
    },
    animate : function() {
        var el =  $(this.element);
        var pairs = this.attributes.keyValuePairs();
        for(var i = 0; i<pairs.length; i++) {
            el.css(pairs[i].key,pairs[i].value);
        }
    },
    next : function(el) {
        this.nextAnimation = new Poker.CSSAttributeAnimation(el || this.element);
        return this.nextAnimation;
    }

});