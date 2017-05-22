describe("Poker.Pager Test", function(){
    var lastPagerResult = null;

    var items = null;
    var pager = null;

    beforeEach(function() {
        lastPagerResult = null;
        var TestPager = Poker.Pager.extend({
            init : function(itemsPerPage) {
                this._super(itemsPerPage);
            },
            displayItems : function(items) {
                lastPagerResult = items;
            }

        });

        items = [
            1,2,3,
            4,5,6,
            7,8,9,
            10];
        pager = new TestPager(3);

    });

    it("test Last Page", function(){
        pager.setItems(items);
        expect(pager.getNrOfPages()).toEqual(4);
        expect(lastPagerResult).toEqual([1,2,3]);
    });
    it("test change items", function(){
        pager.setItems(items);
        expect(pager.getNrOfPages()).toEqual(4);
        pager.setPage(pager.getNrOfPages()-1);
        expect(pager.activePage).toEqual(3);
        expect(lastPagerResult).toEqual([10]);
        pager.setItems([
            1,2,3,
            4,5,6,
            7]);
        expect(pager.activePage).toEqual(2);
        expect(lastPagerResult).toEqual([7]);
        pager.setItems([
            1,2,3,
            4,5,6,
            7,8,9,
            10
        ]);
        expect(pager.activePage).toEqual(2);
    });

    it("test next and prev", function(){
        pager.setItems(items);
        expect(pager.activePage).toEqual(0);
        expect(pager.getNrOfPages()).toEqual(4);
        expect(lastPagerResult).toEqual([1,2,3]);
        pager.next();
        expect(lastPagerResult).toEqual([4,5,6]);
        pager.next();
        pager.next();
        pager.next();//nothing should happen
        pager.previous();
        expect(lastPagerResult).toEqual([7,8,9]);
    });
});