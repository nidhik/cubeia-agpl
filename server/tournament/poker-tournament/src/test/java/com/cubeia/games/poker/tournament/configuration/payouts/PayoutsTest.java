/**
 * Copyright (C) 2012 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.games.poker.tournament.configuration.payouts;

import com.cubeia.games.poker.common.money.Currency;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class PayoutsTest {

    private PayoutStructure structure;
    private Currency eur = new Currency("EUR",2);

    @Before
    public void setup() {
        this.structure = PayoutStructureParserTest.createTestStructure();
    }

    @Test
    public void testPlayerNotInTheMoneyGetsZero() {
        Payouts payouts = structure.getPayoutsForEntrantsAndPrizePool(56, bd(20000), eur, bd(1));
        assertThat(payouts.getPayoutForPosition(56), is(bd("0")));
    }

    @Test
    public void testBubbleGetsBubble() {
        Payouts sitAndGoPayouts = structure.getPayoutsForEntrantsAndPrizePool(10, bd(1000), eur, bd(1));
        assertThat(sitAndGoPayouts.getPayoutList().size(), is(3));
        assertThat(sitAndGoPayouts.getPayoutForPosition(3), not(bd("0")));
        assertThat(sitAndGoPayouts.getPayoutForPosition(4), is(BigDecimal.ZERO));
    }

    @Test
    public void testZeroBuyIn() {
        Payouts decimalPayouts = structure.getPayoutsForEntrantsAndPrizePool(6, bd("1.20"), eur, bd("0"));
        assertThat(decimalPayouts.getPayoutForPosition(1), is(bd("0.81")));
        assertThat(decimalPayouts.getPayoutForPosition(2), is(bd("0.39")));
    }

    @Test
    public void testRounding() {
        Payouts decimalPayouts = structure.getPayoutsForEntrantsAndPrizePool(6, bd("1.20"), eur, bd("0.20"));
        assertThat(decimalPayouts.getPayoutForPosition(1), is(bd("0.80")));
        assertThat(decimalPayouts.getPayoutForPosition(2), is(bd("0.40")));
    }

    @Test
    public void testDistributionOfRemainingCents() {
        Payouts decimalPayouts = structure.getPayoutsForEntrantsAndPrizePool(6, bd("1.23"), eur, bd("0.20"));
        assertThat(decimalPayouts.getPayoutForPosition(1), is(bd("0.83")));
        assertThat(decimalPayouts.getPayoutForPosition(2), is(bd("0.40")));
    }

    @Test
    public void testNormalSitAndGo() {
        Payouts decimalPayouts = structure.getPayoutsForEntrantsAndPrizePool(6, bd("60"), eur, bd("10"));
        assertThat(decimalPayouts.getPayoutForPosition(1), is(bd("40")));
        assertThat(decimalPayouts.getPayoutForPosition(2), is(bd("20")));
    }

    @Test
    public void testPokerStars() {
        // These payouts are taken from a Poker Stars 9 handed sit&go.
        Payouts decimalPayouts = structure.getPayoutsForEntrantsAndPrizePool(9, bd("493.20"), eur, bd("54.80"));
        assertThat(decimalPayouts.getPayoutForPosition(1), is(bd("246.60")));
        assertThat(decimalPayouts.getPayoutForPosition(2), is(bd("147.96")));
        assertThat(decimalPayouts.getPayoutForPosition(3), is(bd("98.64")));
    }

    @Test
    public void testRangedPayouts() {
        // 0.72% of $5000 = 36
        Payouts payoutsWithRange = structure.getPayoutsForEntrantsAndPrizePool(235, bd(500000), eur, bd(1));
        assertThat(payoutsWithRange.getPayoutForPosition(36), is(bd(3600)));
    }
    private BigDecimal bd(String s) {
        return new BigDecimal(s);
    }
    private BigDecimal bd(int i) {
        return new BigDecimal(i).setScale(2);
    }

}
