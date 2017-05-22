package com.cubeia.game.poker.bot.ai.simple;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.cubeia.game.poker.bot.ai.GameState;
import com.cubeia.game.poker.bot.ai.simple.SimpleAI.Strategy;
import com.cubeia.games.poker.io.protocol.Enums.HandPhaseHoldem;
import com.cubeia.poker.hand.HandStrength;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.Rank;

public class StrengthCalculatorTest {

	StrengthCalculator calc = new StrengthCalculator();
	
	private GameState state;
	
	@Before
	public void setup() {
		state = new GameState();
	}
	
	@Test
	public void testHandStrengthEqualToCommunityStrength() {
		HandStrength handStrength = new HandStrength(HandType.STRAIGHT);
		HandStrength communityHandStrength = new HandStrength(HandType.STRAIGHT);
		Strategy strategy = calc.getStrategy(null, null, handStrength, communityHandStrength);
		assertThat(strategy, is(Strategy.WEAK));
	}
	
	@Test
	public void testPreFlop() {
		state.setPhase(HandPhaseHoldem.PREFLOP);
		HandStrength handStrength = new HandStrength(HandType.HIGH_CARD);
		handStrength.setHighestRank(Rank.JACK);
		HandStrength communityHandStrength = new HandStrength(HandType.NOT_RANKED);
		Strategy strategy = calc.getStrategy(null, state, handStrength, communityHandStrength);
		assertThat(strategy, is(Strategy.NEUTRAL));
	}
	
	@Test
	public void testPreFlop2() {
		state.setPhase(HandPhaseHoldem.PREFLOP);
		HandStrength handStrength = new HandStrength(HandType.PAIR);
		handStrength.setHighestRank(Rank.JACK);
		HandStrength communityHandStrength = new HandStrength(HandType.NOT_RANKED);
		Strategy strategy = calc.getStrategy(null, state, handStrength, communityHandStrength);
		assertThat(strategy, is(Strategy.STRONG));
	}
	
	@Test
	public void testCombination() {
		state.setPhase(HandPhaseHoldem.FLOP);
		HandStrength handStrength = new HandStrength(HandType.STRAIGHT);
		HandStrength communityHandStrength = new HandStrength(HandType.PAIR);
		Strategy strategy = calc.getStrategy(null, state, handStrength, communityHandStrength);
		assertThat(strategy, is(Strategy.STRONG));
	}

}
