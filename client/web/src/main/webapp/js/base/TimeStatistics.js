"use strict";
var Poker = Poker || {};

Poker.TimeStatistics = Class.extend({
    max : 0,
    min : 0,
    times : null,
    count : 0,
    size : 0,
    pos : 0,
    init : function(size) {
        this.size = size;
        this.min = Number.MAX_VALUE;
        this.max = Number.MIN_VALUE;
        this.times = new Array();
    },
    add : function(time) {
        if(time>this.max) {
            this.max = time;
        }
        if(time<this.min) {
            this.min = time;
        }
        this.times[this.pos] = time;

        this.count++;
        if(this.pos+1==this.size) {
            this.pos = 0;
        } else {
            this.pos++;
        }
    },
    getAverage : function() {
        var sum = 0;
        var count = 0;
        for(var i = 0; i<this.times.length; i++) {
            if(typeof(this.times[i])!="undefined") {
                sum=sum+this.times[i];
                count++;
            }
        }
        return count == 0 ? 0 : (sum/count).toFixed(2);
    },
    fromString : function(str) {
        var values = str.split(",");
        this.max = parseInt(values[0]);
        this.min = parseInt(values[1]);
        this.count = parseInt(values[2]);
    },
    toString : function() {
        return this.max + ","+this.min+","+this.count;
    }

});