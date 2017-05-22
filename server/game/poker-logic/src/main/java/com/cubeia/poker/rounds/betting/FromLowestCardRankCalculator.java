package com.cubeia.poker.rounds.betting;

import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.calculator.ByRankAndSuiteCardComparator;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.util.PokerUtils;

import java.util.*;

public class FromLowestCardRankCalculator extends DefaultPlayerToActCalculator {
    Comparator<Card> acesHighAsc = ByRankAndSuiteCardComparator.ACES_HIGH_ASC;
    int dealerButtonSeatId;
    public FromLowestCardRankCalculator(int dealerButtonSeatId) {
        super(dealerButtonSeatId);
        this.dealerButtonSeatId = dealerButtonSeatId;
    }

    @Override
    public PokerPlayer getFirstPlayerToAct(SortedMap<Integer, PokerPlayer> seatingMap, List<Card> communityCards) {
        return fromLowestCard(seatingMap);
    }

    private PokerPlayer fromLowestCard(SortedMap<Integer, PokerPlayer> seatingMap) {
        PokerPlayer player  = null;
        for(PokerPlayer p : seatingMap.values()){
            if(player == null) {
                player = p;
            } else {
                Card c1 = getLowestVisibleCard(p);
                Card c2 = getLowestVisibleCard(player);
                if(acesHighAsc.compare(c1,c2)<0) {
                    player = p;
                }
            }
        }
        return player;
    }

    private Card getLowestVisibleCard(PokerPlayer p) {
        Set<Card> publicPocketCards = p.getPublicPocketCards();
        Card lowest = null;
        for(Card c : publicPocketCards) {
            if(lowest == null) {
                lowest = c;
            } else {

                if(acesHighAsc.compare(c,lowest)<0) {
                    lowest = c;
                }
            }
        }
        return lowest;
    }

}
