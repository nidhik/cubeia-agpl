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

package com.cubeia.poker.rounds.betting;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.variant.stud.DefaultBestHandPlayerToActCalculator;
import com.cubeia.poker.variant.telesina.hand.TelesinaPlayerToActCalculator;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class DefaultBestHandPlayerToActCalculatorTest {

    @Test
    public void testFirstPlayerToActOnePublicCard() {
        DefaultBestHandPlayerToActCalculator ptac = new DefaultBestHandPlayerToActCalculator();
        SortedMap<Integer, PokerPlayer> seating = new TreeMap<Integer, PokerPlayer>();

        PokerPlayer p1 = playerWithHand(1, "7C 8C", "QS");
        PokerPlayer p2 = playerWithHand(2, "AS 8D", "TC");
        PokerPlayer p3 = playerWithHand(3, "AS 8H", "JS");

        seating.put(1, p1);
        seating.put(2, p2);
        seating.put(3, p3);

        PokerPlayer first = ptac.getFirstPlayerToAct(seating, Collections.EMPTY_LIST);
        assertEquals(1, first.getId());
    }

    @Test
    public void testFirstPlayerToActTwoPublicCard() {
        DefaultBestHandPlayerToActCalculator ptac = new DefaultBestHandPlayerToActCalculator();
        SortedMap<Integer, PokerPlayer> seating = new TreeMap<Integer, PokerPlayer>();

        PokerPlayer p1 = playerWithHand(1, "7C 8C", "TS 3S");
        PokerPlayer p2 = playerWithHand(2, "AS 8D", "TC 4S");
        PokerPlayer p3 = playerWithHand(3, "AS 8H", "8C 7C");

        seating.put(1, p1);
        seating.put(2, p2);
        seating.put(3, p3);

        PokerPlayer first = ptac.getFirstPlayerToAct(seating, Collections.EMPTY_LIST);
        assertEquals(2, first.getId());
    }



    private PokerPlayer playerWithHand(int id, String pocketCard, String publicPocketCards) {
        PokerPlayer p = mock(PokerPlayer.class);

        when(p.getId()).thenReturn(id);

        Hand pocketCardsHand = new Hand(pocketCard + " " + publicPocketCards);
        when(p.getPocketCards()).thenReturn(pocketCardsHand);

        Set<Card> publicPocketCardsSet = new HashSet<Card>(new Hand(publicPocketCards).getCards());
        when(p.getPublicPocketCards()).thenReturn(publicPocketCardsSet);

        return p;
    }

    @Test
    public void testNextPlayerToAct() {
        DefaultPlayerToActCalculator pac = new DefaultPlayerToActCalculator(1);
        SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        seatingMap.put(2, player3);

        PokerPlayer playerToAct = pac.getFirstPlayerToAct(seatingMap, Collections.EMPTY_LIST);
        assertThat(playerToAct, is(player3));

        pac = new DefaultPlayerToActCalculator(2);
        playerToAct = pac.getFirstPlayerToAct(seatingMap, Collections.EMPTY_LIST);
        assertThat(playerToAct, is(player1));
    }

}
