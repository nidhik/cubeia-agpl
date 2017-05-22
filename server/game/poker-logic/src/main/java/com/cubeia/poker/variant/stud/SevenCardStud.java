package com.cubeia.poker.variant.stud;

import com.cubeia.poker.variant.GameType;
import com.cubeia.poker.variant.PokerGameBuilder;

import static com.cubeia.poker.rounds.betting.BettingRoundName.DOUBLE_STREET;
import static com.cubeia.poker.rounds.betting.BettingRoundName.SINGLE_STREET;
import static com.cubeia.poker.variant.RoundCreators.*;
import static com.cubeia.poker.variant.RoundCreators.dealFaceUpCards;


public class SevenCardStud {

    public static GameType createGame() {
        return new PokerGameBuilder().withRounds(
                ante(),
                dealFaceDownAndFaceUpCards(2, 1),
                bringInRound(SINGLE_STREET,fromLowestRankingCard(),true),
                dealFaceUpCards(1),
                bettingRound(SINGLE_STREET, fromBestDefaultHand(), true),
                dealFaceUpCards(1),
                bettingRound(SINGLE_STREET, fromBestDefaultHand(), true),
                dealFaceUpCards(1),
                bettingRound(DOUBLE_STREET, fromBestDefaultHand(), true),
                dealFaceDownCards(1),
                bettingRound(DOUBLE_STREET, fromBestDefaultHand(), true)).build();
    }
}
