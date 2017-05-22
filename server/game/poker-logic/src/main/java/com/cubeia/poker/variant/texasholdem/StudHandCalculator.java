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
import com.cubeia.poker.hand.calculator.ByRankCardComparator;
import com.cubeia.poker.hand.calculator.HandCalculator;
import com.cubeia.poker.hand.eval.HandTypeCheckCalculator;
import com.cubeia.poker.variant.stud.StudHandComparator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.min;


public class StudHandCalculator extends  TexasHoldemHandCalculator implements HandCalculator, HandTypeEvaluator, Serializable {

    @Override
    public HandInfo getBestHandInfo(Hand hand) {
        int minCards = min(5, hand.getCards().size());
        return getBestCombinationHandStrength(hand, minCards);
    }

    @Override
    public Comparator<Hand> createHandComparator(int playersInPot) {
        return Collections.reverseOrder(new StudHandComparator());
    }

}
