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
import com.cubeia.poker.hand.Suit;
import com.cubeia.poker.player.PokerPlayer;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import java.util.Arrays;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FromLowestCardRankCalculatorTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testFirstPlayerToAct() {
        FromLowestCardRankCalculator pac = new FromLowestCardRankCalculator(1);
        SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);

        when(player1.getPublicPocketCards()).thenReturn(Sets.newSet(new Card(Rank.ACE, Suit.CLUBS)));
        when(player2.getPublicPocketCards()).thenReturn(Sets.newSet(new Card(Rank.KING, Suit.CLUBS)));
        when(player3.getPublicPocketCards()).thenReturn(Sets.newSet(new Card(Rank.QUEEN, Suit.CLUBS)));

        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        seatingMap.put(2, player3);


        PokerPlayer playerToAct = pac.getFirstPlayerToAct(seatingMap, Collections.EMPTY_LIST);
        assertThat(playerToAct, is(player3));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFirstPlayerToActSameRank() {
        FromLowestCardRankCalculator pac = new FromLowestCardRankCalculator(1);
        SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);

        when(player1.getPublicPocketCards()).thenReturn(Sets.newSet(new Card(Rank.ACE, Suit.CLUBS)));
        when(player2.getPublicPocketCards()).thenReturn(Sets.newSet(new Card(Rank.QUEEN, Suit.CLUBS)));
        when(player3.getPublicPocketCards()).thenReturn(Sets.newSet(new Card(Rank.QUEEN, Suit.HEARTS)));

        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        seatingMap.put(2, player3);


        PokerPlayer playerToAct = pac.getFirstPlayerToAct(seatingMap, Collections.EMPTY_LIST);
        assertThat(playerToAct, is(player2));


        when(player1.getPublicPocketCards()).thenReturn(Sets.newSet(new Card(Rank.QUEEN, Suit.DIAMONDS)));
        when(player2.getPublicPocketCards()).thenReturn(Sets.newSet(new Card(Rank.QUEEN, Suit.SPADES)));
        when(player3.getPublicPocketCards()).thenReturn(Sets.newSet(new Card(Rank.QUEEN, Suit.HEARTS)));


        playerToAct = pac.getFirstPlayerToAct(seatingMap, Collections.EMPTY_LIST);
        assertThat(playerToAct, is(player1));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFirstPlayerToActSameRankMultipleCards() {
        FromLowestCardRankCalculator pac = new FromLowestCardRankCalculator(1);
        SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);

        when(player1.getPublicPocketCards()).thenReturn(Sets.newSet(new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.TEN,Suit.HEARTS)));
        when(player2.getPublicPocketCards()).thenReturn(Sets.newSet(new Card(Rank.QUEEN, Suit.CLUBS), new Card(Rank.THREE,Suit.HEARTS)));
        when(player3.getPublicPocketCards()).thenReturn(Sets.newSet(new Card(Rank.TWO,Suit.HEARTS),new Card(Rank.QUEEN, Suit.HEARTS)));

        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        seatingMap.put(2, player3);


        PokerPlayer playerToAct = pac.getFirstPlayerToAct(seatingMap, Collections.EMPTY_LIST);
        assertThat(playerToAct, is(player3));


        when(player1.getPublicPocketCards()).thenReturn(Sets.newSet(new Card(Rank.TWO, Suit.CLUBS), new Card(Rank.TEN,Suit.HEARTS)));
        when(player2.getPublicPocketCards()).thenReturn(Sets.newSet(new Card(Rank.QUEEN, Suit.CLUBS), new Card(Rank.THREE,Suit.HEARTS)));
        when(player3.getPublicPocketCards()).thenReturn(Sets.newSet(new Card(Rank.TWO,Suit.HEARTS),new Card(Rank.QUEEN, Suit.HEARTS)));


        playerToAct = pac.getFirstPlayerToAct(seatingMap, Collections.EMPTY_LIST);
        assertThat(playerToAct, is(player1));

    }

}
