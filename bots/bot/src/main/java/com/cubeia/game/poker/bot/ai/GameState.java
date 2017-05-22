package com.cubeia.game.poker.bot.ai;

import com.cubeia.games.poker.io.protocol.Enums.HandPhaseHoldem;
import com.cubeia.poker.hand.Card;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GameState {

    private List<Card> privateCards = new ArrayList<Card>();

    private List<Card> communityCards = new ArrayList<Card>();

    private HandPhaseHoldem phase = HandPhaseHoldem.PREFLOP;

    private BigDecimal bigBlind;
    
    public void clear() {
        privateCards.clear();
        communityCards.clear();
    }

    public void addPrivateCard(Card card) {
        privateCards.add(card);
    }

    public void addCommunityCard(Card card) {
        communityCards.add(card);
    }

    public List<Card> getPrivateCards() {
        return privateCards;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }

    public void setPhase(HandPhaseHoldem phase) {
        this.phase = phase;
    }

    public HandPhaseHoldem getPhase() {
        return phase;
    }
    
    public BigDecimal getBigBlind() {
		return bigBlind;
	}
    
    public void setBigBlind(BigDecimal bigBlind) {
		this.bigBlind = bigBlind;
	}

    public void advancePhase() {
        switch (phase) {
            case PREFLOP:
                phase = HandPhaseHoldem.FLOP;
                break;
            case FLOP:
                phase = HandPhaseHoldem.TURN;
                break;
            default:
                phase = HandPhaseHoldem.RIVER;
                break;
        }
    }
}
