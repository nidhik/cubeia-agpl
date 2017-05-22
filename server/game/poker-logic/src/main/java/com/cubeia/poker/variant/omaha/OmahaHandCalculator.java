package com.cubeia.poker.variant.omaha;

import com.cubeia.poker.hand.*;
import com.cubeia.poker.variant.texasholdem.TexasHoldemHandCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OmahaHandCalculator extends TexasHoldemHandCalculator {

    @Override
    public HandInfo getBestHandInfo(Hand hand) {
        return getBestCombinationHandStrength(hand);
    }

    /**
     * Get all possible hand combinations and rank them.
     *
     * @param hand with more than 5 cards
     * @return the best HandStrength found.
     */
    public HandStrength getBestCombinationHandStrength(Hand hand) {
        if (hand == null || hand.getCards() == null || hand.getCards().isEmpty()) {
            return new HandStrength(HandType.NOT_RANKED);
        }
        List<HandStrength> allPossibleHands = new ArrayList<HandStrength>();

        Combinator<Card> pocketCombinations = new Combinator<Card>(hand.getPocketCards(), 2);

        if(hand.getCommunityCards().size()>=3) {
            addHandsWithCommunityCards(allPossibleHands, pocketCombinations, hand.getCommunityCards());
        } else {
            addHandsOnlyPrivate(allPossibleHands,pocketCombinations);
        }


        if (allPossibleHands.isEmpty()) {
            throw new IllegalArgumentException("calculated 0 possible hands from cards: " + hand.toString());
        }

        Collections.sort(allPossibleHands, new HandStrengthComparator());
        return allPossibleHands.get(0);
    }

    private void addHandsOnlyPrivate(List<HandStrength> allPossibleHands, Combinator<Card> pocketCombinations) {
        for (List<Card> pocketCards : pocketCombinations) {
            Hand h = new Hand();
            h.addPocketCards(pocketCards);
            HandStrength handStrength = getHandStrength(h);
            allPossibleHands.add(handStrength);
        }
    }

    @Override
    public Comparator<Hand> createHandComparator(int playersInPot) {
        return Collections.reverseOrder(new OmahaHandComparator());
    }

    private void addHandsWithCommunityCards(List<HandStrength> allPossibleHands,
                                            Combinator<Card> pocketCombinations,
                                            List<Card> communityCards) {
        for (List<Card> pocketCards : pocketCombinations) {
            Combinator<Card> communityCombinations = new Combinator<Card>(communityCards, 3);
            for(List<Card> cards :  communityCombinations) {
                Hand h = new Hand();
                h.addPocketCards(pocketCards);
                h.addCommunityCards(cards);
                HandStrength handStrength = getHandStrength(h);
                allPossibleHands.add(handStrength);
            }

        }
    }




}
