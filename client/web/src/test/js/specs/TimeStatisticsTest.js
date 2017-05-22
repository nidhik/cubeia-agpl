var Poker = Poker || {};
describe("Poker.TimeStatistics Test", function(){



    it("test timing", function(){
        var stats = new Poker.TimeStatistics(5);
        stats.add(10);
        stats.add(2);

        expect(stats.getAverage()).toEqual((6).toFixed(2));
        expect(stats.max).toEqual(10);
        expect(stats.min).toEqual(2);
        expect(stats.count).toEqual(2);

        stats.add(100);
        stats.add(5);
        stats.add(10);
        stats.add(1);
        stats.add(10);
        stats.add(2);

        expect(stats.getAverage()).toEqual((5.6).toFixed(2));
        expect(stats.max).toEqual(100);
        expect(stats.min).toEqual(1);
        expect(stats.count).toEqual(8);

    });

    it("test from/toString", function(){
        var stats = new Poker.TimeStatistics(5);
        stats.fromString("3,2,5");
        expect(stats.max).toEqual(3);
        expect(stats.min).toEqual(2);
        expect(stats.count).toEqual(5);

        expect(stats.toString()).toEqual("3,2,5");
    });
});