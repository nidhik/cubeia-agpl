(function ( $ ) {
    "use strict";
    $.fn.cs_leaderboard = function(opts) {
        if(typeof(opts)==undefined) {
            opts={};
        }
        return this.each(function(el){
            var id = $(this).data("leaderboard-id");
            var nrOfItems = $(this).data("nr-of-items")
            var leaderboard = new Poker.Leaderboard($(this),id,nrOfItems,opts);
            leaderboard.start();
        });
    };
}( jQuery ));

var Poker = Poker || {};

Poker.Leaderboard = Class.extend({
    opts : null,
    id : null,
    element : null,
    nrOfItems : null,
    timer : null,
    updateCount : 0,
    template : null,
    lastUpdate : null,
    currentUpdate : null,
    cssUtils : null,
    playerApi : null,

    init : function(element,id,nrOfItems,opts) {

        this.playerApi = Poker.AppCtx.getPlayerApi();

        var DEFAULT_OPTS = {
            url : "/cubeia-social/leaderboard/",
            updateInterval : 60*1000,
            template : null,
            decimals : 2
        };
        this.opts = $.extend(DEFAULT_OPTS, opts);

        this.id = id;
        this.element = element;
        this.nrOfItems = nrOfItems;

        this.element.height(nrOfItems*67);
        this.timer = null;
        this.updateCount = 0;
        this.template = null;
        this.lastUpdate = null;
        this.currentUpdate = null;
        this.cssUtils = new Poker.CSSUtils();
        this.start();

    },
    getDirection : function(player) {
        var from = -1;
        var to = -1;

        var playerId = ""+player.playerId;
        if(this.lastUpdate == null) {
            return { from : -1, to : player.position, first : true};
        }
        if(typeof(this.lastUpdate[playerId])=="undefined") {
            return { from : -1, to : player.position, first : false};
        }
        var res =  { from : this.lastUpdate[playerId], to : player.position, first : false};

        return res;
    },

    fetchLeaderboard : function() {
        var self = this;
        if(this.playerApi.isLeaderboardEnabled()==false ) {
            $("#leaderboardContainer").hide();
            return;
        }
        $("#leaderboardContainer").show();
        this.playerApi.requestLeaderboard(self.id,self.opts.global,function(data){
            if(data.length == 0 ) {
                console.log("leaderboard was reset, keeping list as is");
                if(self.lastUpdate!=null) {
                    return;
                }
            }
            $.each(data.entries,function(i,e){
                e.position = i;
                e.displayPosition = i+1;
            });
            self.updateLeaderboard(data);
        },function(e){
            console.log("error fetching leaderboard " + self.id, e);
            if(self.updateCount==0) {
                console.log("Stopping leaderboard update");
                self.stop();
                self.displayError();
            }
        });
    },

    displayError : function() {
        this.element.html("Unable to fetch leaderboard");
    },

    shuffleItems : function(data) {
        var entries = data.entries;
        var shuffled = [];
        while(entries.length>0) {
            var d = entries.splice(Math.floor(Math.random()*entries.length),1)[0];
            shuffled.push(d);
        }

        return { entries : shuffled };
    },
    updateLeaderboard : function(data) {
        var self = this;
        self.updateCount++;
        self.lastUpdate = this.currentUpdate;
        self.currentUpdate = [];
        var last = data.entries.length < self.nrOfItems ? data.entries.length : self.nrOfItems;
        data.entries = data.entries.slice(0,last);

        $.each(data.entries,function(i,e){
            self.currentUpdate[""+ e.playerId] = i;
        });

        this.loadProfiles(data);
    },
    loadProfiles : function(data) {
        var self = this;
        var count = data.entries.length;
        var profileLoaded = function() {
            if(count == 0) {
                self.displayItems(data);
            }
        };
        $.each(data.entries, function(i,e){
            self.playerApi.requestPlayerProfile(e.playerId,null,
                function(profile){
                    count--;
                    e.avatarUrl = profile.externalAvatarUrl;
                    e.level = profile.level;
                    profileLoaded();
                },function(){
                    count--;
                    profileLoaded();
                });
        });
    },
    displayItems : function(data) {
        var self = this;
        var lastUpdateToRemove= [];
        for(var playerId in this.lastUpdate) {
            lastUpdateToRemove[playerId] = this.lastUpdate[playerId];
        }
        var playerHandled = function(playerId) {
            delete lastUpdateToRemove[""+playerId];
        };
        for(var i = 0; i<this.nrOfItems && i<data.entries.length; i++) {
            var e = data.entries[i];
            var direction = this.getDirection(e);
            this.animate(e,direction);
            playerHandled(e.playerId);
        }
        for(var playerId in lastUpdateToRemove) {
            this.animateRemove(playerId);
        }


    },
    animateRemove : function(playerId) {
        var self = this;
        var toRemove = this.element.find(".player-"+playerId);
        var called = false;
        var removeFunction = function() {
            if(called == false) {
                self.cssUtils.removeTransitionCallback(toRemove);
                toRemove.remove();
                called = true;
            }
        };
        this.cssUtils.addTransitionCallback(toRemove,function(){
            removeFunction();
        });
        setTimeout(function(){
            removeFunction();
        },2000);

        this.cssUtils.setTranslate3d(toRemove,0,this.element.height(),0,"px");
    },

    animate : function(player, direction) {
        var self = this;
        if(direction.from == -1) {
            var pe = $(self.template(player));
            self.cssUtils.setTranslate3d(pe,0,self.element.height(),0,"px");
            if(direction.first==false) {
                pe.find(".arrow").addClass("arrow-up");
            }
            this.element.append(pe);
            setTimeout(function(){
                self.cssUtils.setTranslate3d(self.element.find(".player-"+player.playerId),0,67*player.position,0,"px");
            },50);
        } else if(direction.from!=direction.to && direction.from != -1) {
            var el = self.element.find(".player-"+player.playerId);
            el.find(".position").html(player.displayPosition);
            self.cssUtils.setTranslate3d(el,0,67*player.position,0,"px");
            if(direction.from>direction.to) {
                el.find(".arrow").removeClass("arrow-down").addClass("arrow-up");
            } else {
                el.find(".arrow").addClass("arrow-down").removeClass("arrow-up");
            }
        } else {
            self.element.find(".player-"+player.playerId + " .arrow").removeClass("arrow-down").removeClass("arrow-up");
        }
    },
    start : function() {
        var self = this;
        if(self.timer==null) {
            self.template = Handlebars.compile(self.getItemTemplate());
            self.timer = setInterval(function(){
                self.fetchLeaderboard();
            },self.opts.updateInterval);
            self.fetchLeaderboard();
        } else {
            console.log("Leaderboard update already running for leaderboard " + self.id);
        }
    },

    stop : function() {
        var self = this;
        if(self.timer!=null)  {
            clearInterval(self.timer);
            self.timer = null;
        }
    },
    getItemTemplate : function() {
        var self = this;
        if(self.opts.template!=null) {
            return $(self.opts.template).html();
        } else {
            return '<div class="player {{direction}} player-{{playerId}}">' +
                '<div class="position">{{displayPosition}}</div>' +
                '<div class="lb-avatar-container">' +
                "<div class=\"lb-avatar\" style=\"background-image:url('{{{avatarUrl}}}')\">&nbsp;</div>" +
                '<div class="level level-{{level}}"></div>' +
                '</div>'+
                '<div class="name">{{screenName}}</div>' +
                '<div class="arrow"></div>'
            '</div>'
        }
    },

    getTemplate : function() {
        var self = this;
        if(self.opts.template!=null) {
            return $(self.opts.template).html();
        } else {
            return '<div class="leaderboard"></div>';
        }
    }
});