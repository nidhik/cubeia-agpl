package com.cubeia.game.poker.bot.ai.simple;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.cubeia.game.poker.bot.ai.GameState;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.HandStrength;
import com.cubeia.poker.hand.HandType;

public class SimpleAITest {

	SimpleAI ai = new SimpleAI();
	
	@Test
	public void testGetCommunityHandStrength() {
		GameState state = new GameState();
		state.addCommunityCard(new Card("As"));
		state.addCommunityCard(new Card("5s"));
		state.addCommunityCard(new Card("Ad"));
		HandStrength strength = ai.getCommunityHandStrength(state);
		assertThat(strength.getHandType(), is(HandType.PAIR));
	}
	
	@Test
	public void testGetHandStrength() {
		GameState state = new GameState();
		state.addCommunityCard(new Card("As"));
		state.addCommunityCard(new Card("5s"));
		state.addCommunityCard(new Card("Ad"));
		state.addPrivateCard(new Card("4h"));
		state.addPrivateCard(new Card("5h"));
		HandStrength strength = ai.getHandStrength(state);
		assertThat(strength.getHandType(), is(HandType.TWO_PAIRS));
	}

	@Test
	public void testGetCommunityHandStrengthPreFlop() {
		GameState state = new GameState();
		HandStrength strength = ai.getCommunityHandStrength(state);
		assertThat(strength.getHandType(), is(HandType.NOT_RANKED));
	}
	
	@Test
	public void testGetHandStrengthPreFlop() {
		GameState state = new GameState();
		state.addPrivateCard(new Card("4h"));
		state.addPrivateCard(new Card("5h"));
		HandStrength strength = ai.getHandStrength(state);
		assertThat(strength.getHandType(), is(HandType.HIGH_CARD));
	}
}
