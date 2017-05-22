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

package com.cubeia.poker.variant.omaha;

import com.cubeia.poker.variant.GameType;
import com.cubeia.poker.variant.PokerGameBuilder;

import static com.cubeia.poker.rounds.betting.BettingRoundName.*;
import static com.cubeia.poker.variant.RoundCreators.*;

public class Omaha {

    public static GameType createGame() {

        return new PokerGameBuilder().withRounds(
                        blinds(false),
                        dealFaceDownCards(4),
                        bettingRound(PRE_FLOP, fromBigBlind(), false),
                        dealCommunityCards(3),
                        bettingRound(FLOP, false),
                        dealCommunityCards(1),
                        bettingRound(TURN),
                        dealCommunityCards(1),
                        bettingRound(RIVER)).withHandEvaluator(new OmahaHandCalculator()).build();
    }
}
