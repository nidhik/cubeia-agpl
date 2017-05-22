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

package com.cubeia.poker.hand.calculator;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.hand.Suit;

import java.util.Collections;
import java.util.Comparator;

public class ByRankAndSuiteCardComparator extends ByRankCardComparator {

    public static final Comparator<Card> ACES_HIGH_ASC = new ByRankAndSuiteCardComparator(true);
    public static final Comparator<Card> ACES_LOW_ASC = new ByRankAndSuiteCardComparator(false);

    private boolean acesHigh;

    public ByRankAndSuiteCardComparator(boolean acesHigh) {
        super(acesHigh);
    }

    @Override
    public int compare(Card c1, Card c2) {
        int result = super.compare(c1,c2);
        if(result == 0) {
            return getRankBySuit(c1.getSuit()) - getRankBySuit(c2.getSuit());
        } else {
            return result;
        }

    }

    private int getRankBySuit(Suit s){
        if(s == Suit.CLUBS) {
            return 1;
        } else if(s == Suit.DIAMONDS) {
            return 2;
        } else if(s == Suit.HEARTS) {
            return 3;
        } else if(s == Suit.SPADES) {
            return 4;
        } else {
            throw new IllegalArgumentException("Suit " + s + " not valid");
        }

    }

}
