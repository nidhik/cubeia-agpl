"use strict"
var Poker = Poker || {};
Poker.SkinConfiguration = {
    operatorId : 1,
    name : "mirch",
    preLoadImages : null,
    title : "Mirch Poker",
    onLoad : function() {
        var toggle = function() {
            if($("#handRankingsView").is(":visible")) {
                $(".nicescroll-rails").remove();
            }
            $(".nice-scroll").niceScroll();
            $("#handRankingsView").toggle();
        };

        $("#closeHandRankings").click(function(){
            toggle();
        });
        $(".hand-ranking-icon").click(function(){
            toggle();
        });
    }
};