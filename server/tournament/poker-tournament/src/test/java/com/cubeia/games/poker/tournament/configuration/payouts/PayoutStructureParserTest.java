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
import org.apache.log4j.Logger;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PayoutStructureParserTest {

    private static final Logger log = Logger.getLogger(PayoutStructureParserTest.class);

    Currency eur = new Currency("EUR", 2);

    @Test
    public void testSimpleStructureHeadsUp() {
        // See simple.csv
        PayoutStructureParser parser = new PayoutStructureParser();
        PayoutStructure structure = parser.parsePayouts("simple.csv");
        Payouts payouts = structure.getPayoutsForEntrantsAndPrizePool(2, bd(100), eur, bd(50));
        assertThat(payouts.getPayoutForPosition(1), is(bd(100)));
    }

    // bd = BigDecimal
    private BigDecimal bd(int i) {
        return new BigDecimal(i).setScale(2);
    }

    private BigDecimal bd(String i) {
        return new BigDecimal(i).setScale(2);
    }

    @Test
    public void testSimpleStructure10Players() {
        // See simple.csv
        PayoutStructureParser parser = new PayoutStructureParser();
        PayoutStructure structure = parser.parsePayouts("simple.csv");
        Payouts payouts = structure.getPayoutsForEntrantsAndPrizePool(10, bd(10), eur, bd(1));
        assertThat(payouts.getPayoutForPosition(1), is(bd(6)));
        assertThat(payouts.getPayoutForPosition(2), is(bd(3)));
        assertThat(payouts.getPayoutForPosition(3), is(bd(1)));
    }

    @Test
    public void test6PlayersWithDecimals() {
        // See simple.csv
        PayoutStructureParser parser = new PayoutStructureParser();
        PayoutStructure structure = parser.parsePayouts("complex.csv");
        Payouts payouts = structure.getPayoutsForEntrantsAndPrizePool(6, bd("1.20"), eur, bd("0.20"));
        assertThat(payouts.getPayoutForPosition(1), is(bd("0.80")));
        assertThat(payouts.getPayoutForPosition(2), is(bd("0.40")));
        assertThat(payouts.getPayoutForPosition(3), is(BigDecimal.ZERO));
    }

    @Test
    public void testComplex() {
        // See complex.csv
        PayoutStructureParser parser = new PayoutStructureParser();
        PayoutStructure structure = parser.parsePayouts("complex.csv");
        Payouts payouts = structure.getPayoutsForEntrantsAndPrizePool(10, bd(10), eur, bd(1));
        log.debug("Payouts: " + payouts);
        structure.verify();
        Payouts payoutsForLotsOfPlayers = structure.getPayoutsForEntrantsAndPrizePool(3502, bd(100), eur, bd(35));
        assertThat(payoutsForLotsOfPlayers.getPayoutList().get(18).getPercentage(), is(BigDecimal.valueOf(0.75)));
    }

    public static PayoutStructure createTestStructure() {
        PayoutStructureParser structure = new PayoutStructureParser();
        return structure.parsePayouts("complex.csv");
    }

}
