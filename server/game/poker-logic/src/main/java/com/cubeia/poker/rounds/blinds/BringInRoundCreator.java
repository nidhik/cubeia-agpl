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

package com.cubeia.poker.rounds.blinds;

import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.rounds.Round;
import com.cubeia.poker.rounds.RoundCreator;
import com.cubeia.poker.rounds.betting.*;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.variant.texasholdem.TexasHoldemFutureActionsCalculator;

import java.io.Serializable;

public class BringInRoundCreator implements RoundCreator, Serializable {

    boolean flipCardsOnAllInShowdown;
    BettingRoundName roundName;
    PlayerToActCalculatorFactory playerToActCalculatorFactory;
    public BringInRoundCreator(BettingRoundName roundName, PlayerToActCalculatorFactory playerToActCalculatorFactory, boolean flipCardsOnAllInShowdown) {
        this.roundName = roundName;
        this.playerToActCalculatorFactory = playerToActCalculatorFactory;
        this.flipCardsOnAllInShowdown = flipCardsOnAllInShowdown;
    }

    @Override
    public Round create(PokerContext context, ServerAdapterHolder serverAdapterHolder) {
        PlayerToActCalculator playerToActCalculator = playerToActCalculatorFactory.createPlayerToActCalculator(context);
        PokerSettings settings = context.getSettings();
        BetStrategy betStrategy = BetStrategyFactory.createBetStrategy(settings.getBetStrategyType(), settings.getBigBlindAmount(),roundName.isDoubleBetRound());
        ActionRequestFactory requestFactory = new ActionRequestFactory(betStrategy);
        TexasHoldemFutureActionsCalculator futureActionsCalculator = new TexasHoldemFutureActionsCalculator(betStrategy.getType());
        BringInRound r =  new BringInRound(context,serverAdapterHolder, playerToActCalculator, requestFactory, futureActionsCalculator,betStrategy);
        r.setFlipCardsOnAllInShowdown(this.flipCardsOnAllInShowdown);
        return r;
    }
}
