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
package com.cubeia.poker.variant.texasholdem;

import com.cubeia.poker.hand.*;
import com.cubeia.poker.handhistory.api.HandStrengthCommon;
import com.cubeia.poker.settings.RakeSettings;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TexasHoldemHandCalculatorTest {

    private TexasHoldemHandCalculator calculator;

    @Before
    public void setup() {
        calculator = new TexasHoldemHandCalculator();
    }

    @Test
    public void testGetBestHandInfoForPocketCards() throws Exception {
        HandInfo info = calculator.getBestHandInfo(new Hand("7S 8S"));
        assertEquals(HandType.HIGH_CARD, info.getHandType());
    }

    @Test
    public void testGetBestHandInfoForFullBoard() throws Exception {
        HandInfo info = calculator.getBestHandInfo(new Hand("7S 8S 2S JC QD 4S 5S"));
        assertEquals(HandType.FLUSH, info.getHandType());
    }

    @Test
    public void testCheckTranslate() {
        Hand hand = new Hand("KC QC JC TC 9C");
        HandStrength hs = calculator.getHandStrength(hand);
        assertThat(hs.getHandType(), is(HandType.STRAIGHT_FLUSH));
        assertThat(hs.getCards().size(), is(5));

        HandStrengthCommon translate = hs.translate();


        hand = new Hand("AC 2C 4C 3C 5C");
        hs = calculator.getHandStrength(hand);
        assertThat(hs.getHandType(), is(HandType.STRAIGHT_FLUSH));
        assertThat(hs.getCards().size(), is(5));

        translate = hs.translate();


        hand = new Hand("KC QC JC TC 8C");
        hs = calculator.getHandStrength(hand);
        assertThat(hs.getHandType(), is(HandType.FLUSH));
        assertThat(hs.getCards().size(), is(5));

        translate = hs.translate();

        hand = new Hand("KC QC JC TC 9H");
        hs = calculator.getHandStrength(hand);
        assertThat(hs.getHandType(), is(HandType.STRAIGHT));
        assertThat(hs.getCards().size(), is(5));

        translate = hs.translate();

        hand = new Hand("KC KH KD TC TH");
        hs = calculator.getHandStrength(hand);
        assertThat(hs.getHandType(), is(HandType.FULL_HOUSE));
        assertThat(hs.getCards().size(), is(5));

        translate = hs.translate();

        hand = new Hand("KC KH KD 9C TH");
        hs = calculator.getHandStrength(hand);
        assertThat(hs.getHandType(), is(HandType.THREE_OF_A_KIND));
        assertThat(hs.getCards().size(), is(5));

        translate = hs.translate();

        hand = new Hand("KC KH 9D 9C TH");
        hs = calculator.getHandStrength(hand);
        assertThat(hs.getHandType(), is(HandType.TWO_PAIRS));
        assertThat(hs.getCards().size(), is(5));

        translate = hs.translate();

        hand = new Hand("KC KH 8D 9C 2H");
        hs = calculator.getHandStrength(hand);
        assertThat(hs.getHandType(), is(HandType.PAIR));
        assertThat(hs.getCards().size(), is(5));

        translate = hs.translate();

        hand = new Hand("KC 3H 7D 4C TH");
        hs = calculator.getHandStrength(hand);
        assertThat(hs.getHandType(), is(HandType.HIGH_CARD));
        assertThat(hs.getCards().size(), is(5));

        translate = hs.translate();


    }

    @Test
    public void testHighCard() {

        Hand h1 = new Hand("8S KC 2C QC TC 5S 7D");

        HandInfo bestHandInfo = calculator.getBestHandInfo(h1);
        assertThat(bestHandInfo.getHandType(),is(HandType.HIGH_CARD));
        assertThat(bestHandInfo.getCards().get(0).getRank(),is(Rank.KING));

        Hand h2 = new Hand("3S 8C 2C QC TC 5S 7D");
        bestHandInfo = calculator.getBestHandInfo(h2);
        assertThat(bestHandInfo.getHandType(),is(HandType.HIGH_CARD));
        assertThat(bestHandInfo.getCards().get(0).getRank(),is(Rank.QUEEN));


    }

    /*
    @Test
    public void testRatedPlayerHands() {
        HandResultCreator creator = new HandResultCreator(calculator);
        List<Card> communityCards = new Hand("2C QC TC 5S 7D").getCards();
        Currency currency = new Currency("TRM", 0);

        LinearRakeWithLimitCalculator rakeCalculator = new LinearRakeWithLimitCalculator(getRakeSetting(), currency);

        PotHolder potHolder = new PotHolder(rakeCalculator);
        potHolder.

        Map<Integer, PokerPlayer> seatingMap = new HashMap<>();
        DefaultPokerPlayer player80 = new DefaultPokerPlayer(80);
        player80.addPocketCard(Card.fromString("3S"),false);
        player80.addPocketCard(Card.fromString("8C"), false);
        seatingMap.put(80, player80);

        DefaultPokerPlayer player1627 = new DefaultPokerPlayer(1627);
        player1627.addPocketCard(Card.fromString("8S"),false);
        player1627.addPocketCard(Card.fromString("KC"),false);
        seatingMap.put(1627, player1627);

        HandResult handResult = creator.createHandResult(communityCards, new HandResultCalculator(calculator), potHolder, seatingMap,
                new ArrayList<Integer>(), new HashSet<PokerPlayer>(), currency);

        Map<PokerPlayer,Result> results = handResult.getResults();
       assertThat(results.get(player1627).getNetResult(),is(new BigDecimal("160")));


    } */

    public RakeSettings getRakeSetting() {
        return RakeSettings.createDefaultRakeSettings(BigDecimal.ZERO);
    }
}