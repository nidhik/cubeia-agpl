/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
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
package com.cubeia.poker.variant.omaha;

import com.cubeia.poker.hand.*;
import com.cubeia.poker.handhistory.api.HandStrengthCommon;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.variant.texasholdem.TexasHoldemHandCalculator;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class OmahaHandCalculatorTest {

    private OmahaHandCalculator calculator;

    @Before
    public void setup() {
        calculator = new OmahaHandCalculator();
    }

    @Test
    public void testGetBestHandInfoForPocketCards() throws Exception {
        Hand hand = new Hand();
        hand.addPocketCards(new Hand("7S 8S 3S 4S").getCards());
        HandInfo info = calculator.getBestHandInfo(hand);
        assertEquals(HandType.HIGH_CARD, info.getHandType());

        hand = new Hand();
        hand.addPocketCards(new Hand("7S 7C 3S 4S").getCards());
        info = calculator.getBestHandInfo(hand);
        assertEquals(HandType.PAIR, info.getHandType());

        hand = new Hand();
        hand.addPocketCards(new Hand("7S 7C 7H 4S").getCards());
        info = calculator.getBestHandInfo(hand);
        assertEquals(HandType.PAIR, info.getHandType());
    }

    @Test
    public void testOmahaBug() throws Exception {
        Hand hand = new Hand();
        hand.addPocketCards(new Hand("TC 5C KD 8H").getCards());
        hand.addCommunityCards(new Hand("6D 9H 6S KH AC").getCards());
        HandInfo info = calculator.getBestHandInfo(hand);
        assertEquals(info.getHandType(),HandType.TWO_PAIRS);

        for(Card c : info.getCards()) {
            if(c.getRank().equals(Rank.ACE)){
                fail("ACE should not be included");
            }
        }

        hand = new Hand();
        hand.addPocketCards(new Hand("QH QC 10H 5H").getCards());
        hand.addCommunityCards(new Hand("6D 9H 6S KH AC").getCards());
        info = calculator.getBestHandInfo(hand);
        assertEquals(info.getHandType(),HandType.TWO_PAIRS);



    }


    @Test
    public void testGetBestHandInfoForFullBoard() throws Exception {
        Hand pockets = new Hand("7S 8D 2C JC");
        Hand community = new Hand("AS JS QS 4S 5S");
        Hand h = new Hand();
        h.addPocketCards(pockets.getCards());
        h.addCommunityCards(community.getCards());
        HandInfo info = calculator.getBestHandInfo(h);
        assertEquals(HandType.PAIR, info.getHandType());
    }

    @Test
    public void testGetBestHandInfoForFullBoardThreeOfAKind() throws Exception {
        Hand pockets = new Hand("TS TD 9S 3C");
        Hand community = new Hand("TH 9C 5S 8C 6C");
        Hand h = new Hand();
        h.addPocketCards(pockets.getCards());
        h.addCommunityCards(community.getCards());
        HandInfo info = calculator.getBestHandInfo(h);
        assertEquals(HandType.THREE_OF_A_KIND, info.getHandType());
    }

    @Test
    public void testGetBestHandInfoForFullBoardWithTwoPairs() throws Exception {
        Hand pockets = new Hand("TS JD 9S 3C");
        Hand community = new Hand("TH TC 5S 5C 6C");
        Hand h = new Hand();
        h.addPocketCards(pockets.getCards());
        h.addCommunityCards(community.getCards());
        HandInfo info = calculator.getBestHandInfo(h);
        assertEquals(HandType.THREE_OF_A_KIND, info.getHandType());
    }


    @Test
    public void testCheckTranslate() {

        Hand pockets = new Hand("KC QC");
        Hand community = new Hand("JC TC 9C");
        Hand hand = new Hand();
        hand.addPocketCards(pockets.getCards());
        hand.addCommunityCards(community.getCards());


        HandStrength hs = calculator.getHandStrength(hand);
        assertThat(hs.getHandType(), is(HandType.STRAIGHT_FLUSH));
        assertThat(hs.getCards().size(), is(5));

        HandStrengthCommon translate = hs.translate();

        pockets = new Hand("AC 2C");
        community = new Hand("4C 3C 5C");
        hand = new Hand();
        hand.addPocketCards(pockets.getCards());
        hand.addCommunityCards(community.getCards());


         hs = calculator.getHandStrength(hand);
        assertThat(hs.getHandType(), is(HandType.STRAIGHT_FLUSH));
        assertThat(hs.getCards().size(), is(5));

        translate = hs.translate();

    }


    public RakeSettings getRakeSetting() {
        return RakeSettings.createDefaultRakeSettings(BigDecimal.ZERO);
    }
}