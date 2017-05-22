var Poker = Poker || {};
describe("Poker.LobbyData Test", function(){
    Poker.MockLobbyValidator = Poker.LobbyDataValidator.extend({
        validate : function(item) {
            return true;
        },
        shouldRemoveItem : function(item) {
            return false;
        }
    });

    var lobbyData;

    beforeEach(function() {
        lobbyData = new Poker.LobbyData(
            new Poker.MockLobbyValidator(),
            function(items){},
            function(item){}
        );
        lobbyData.addSort(new Poker.CapacitySort(false));
        lobbyData.addSort(new Poker.BlindsSort(false));

        var items = [
            { id : 1, capacity : 6, seated : 1, smallBlind : 1 },
            { id : 2, capacity : 10, seated : 5, smallBlind : 2 },
            { id : 3, capacity : 10, seated : 3, smallBlind : 1 },
            { id : 4, capacity : 6, seated : 3, smallBlind : 2 }


        ];
        lobbyData.addItems(items);
    });

    it("test sorting", function(){

        lobbyData.setSortBy("capacity");
        var sorted = lobbyData.getFilteredItems();
        expect(sorted.length).toEqual(4);
        expect(sorted[0].id).toEqual(2);
        expect(sorted[1].id).toEqual(3);
        expect(sorted[2].id).toEqual(4);
        expect(sorted[3].id).toEqual(1);

        lobbyData.setSortBy("blinds");
        sorted = lobbyData.getFilteredItems();
        expect(sorted.length).toEqual(4);
        expect(sorted[0].id).toEqual(2);
        expect(sorted[1].id).toEqual(4);
        expect(sorted[2].id).toEqual(3);
        expect(sorted[3].id).toEqual(1);
    });
});