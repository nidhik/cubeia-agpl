describe("Poker.Leaderboard Test", function(){

    var MockLeaderBoard = Poker.Leaderboard.extend({
            template : null,
             init : function(element,id,nrOfItems,opts) {
                 this._super(element,id,nrOfItems,opts);
             },
             start : function() {
                 this.template = Handlebars.compile(this.getItemTemplate());
             },
             fetchLeaderboard : function(data) {
                 var self = this;
                 $.each(data.entries,function(i,e){
                     e.position = i;
                     e.displayPosition = i+1;
                 });
                 self.updateLeaderboard(data);
             },
            loadProfiles : function(data) {
                this.displayItems(data);
            }
    });

    var leaderboard = null;
    beforeEach(function() {
        leaderboard = new MockLeaderBoard($("#leaderboardTest"),"top_winnings_trm",5,{})
        jasmine.Clock.useMock();
    });

    it("test update", function(){
        var data1 = {"id":"top_winnings_trm", "periodStart":1391259600000, "periodEnd":1391263200000, "entries":[
            {"playerId":"1", "screenName":"Michael", "value":280},
            {"playerId":"2", "screenName":"Dimple Dumanayos", "value":20},
            {"playerId":"3", "screenName":"Virgil", "value":-80},
            {"playerId":"4", "screenName":"Soussi Hassan", "value":-100},
            {"playerId":"5", "screenName":"pjpoker72", "value":-180}
        ]};
        leaderboard.fetchLeaderboard(data1);

        jasmine.Clock.tick(3000);

        expect($("#leaderboardTest .player-1").length).toEqual(1);
        expect($("#leaderboardTest .player-2").length).toEqual(1);
        expect($("#leaderboardTest .player-3").length).toEqual(1);
        expect($("#leaderboardTest .player-4").length).toEqual(1);
        expect($("#leaderboardTest .player-5").length).toEqual(1);

        var data2 = {"id":"top_winnings_trm", "periodStart":1391259600000, "periodEnd":1391263200000, "entries":[

            {"playerId":"2", "screenName":"Dimple Dumanayos", "value":20},
            {"playerId":"1", "screenName":"Michael", "value":280},
            {"playerId":"3", "screenName":"Virgil", "value":-80},
            {"playerId":"5", "screenName":"pjpoker72", "value":-180},
            {"playerId":"4", "screenName":"Soussi Hassan", "value":-100}

        ]};
        leaderboard.fetchLeaderboard(data2);

        jasmine.Clock.tick(3000);

        expect($("#leaderboardTest .player-1").length).toEqual(1);
        expect($("#leaderboardTest .player-2").length).toEqual(1);
        expect($("#leaderboardTest .player-3").length).toEqual(1);
        expect($("#leaderboardTest .player-4").length).toEqual(1);
        expect($("#leaderboardTest .player-5").length).toEqual(1);

        var data3 = {"id":"top_winnings_trm", "periodStart":1391259600000, "periodEnd":1391263200000, "entries":[
            {"playerId":"2", "screenName":"Dimple Dumanayos", "value":20},
            {"playerId":"3", "screenName":"Virgil", "value":-80},
            {"playerId":"5", "screenName":"pjpoker72", "value":-180},
            {"playerId":"4", "screenName":"Soussi Hassan", "value":-100}

        ]};
        leaderboard.fetchLeaderboard(data3);

        jasmine.Clock.tick(3000);

        expect($("#leaderboardTest .player-1").length).toEqual(0);
        expect($("#leaderboardTest .player-2").length).toEqual(1);
        expect($("#leaderboardTest .player-3").length).toEqual(1);
        expect($("#leaderboardTest .player-4").length).toEqual(1);
        expect($("#leaderboardTest .player-5").length).toEqual(1);




    });
});