package com.cubeia.poker.variant.stud;


import com.cubeia.poker.variant.GameType;
import com.cubeia.poker.variant.PokerGameBuilder;

import static com.cubeia.poker.rounds.betting.BettingRoundName.*;
import static com.cubeia.poker.variant.RoundCreators.*;

public class FiveCardStud {

    public static GameType createGame() {
        return new PokerGameBuilder().withRounds(
                ante(),
                dealFaceDownAndFaceUpCards(1,1),
                bringInRound(SINGLE_STREET,fromLowestRankingCard(),false),
                dealFaceUpCards(1),
                bettingRound(SINGLE_STREET, fromBestDefaultHand(), false),
                dealFaceUpCards(1),
                bettingRound(DOUBLE_STREET, fromBestDefaultHand(), true),
                dealFaceUpCards(1),
                bettingRound(DOUBLE_STREET, fromBestDefaultHand(), true)).build();
    }
}
