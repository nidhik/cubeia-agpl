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

package com.cubeia.poker.rounds.blinds;

import com.cubeia.games.poker.common.money.Currency;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rounds.betting.*;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.timing.impl.DefaultTimingProfile;
import com.google.common.base.Predicate;
import junit.framework.TestCase;
import org.hamcrest.Description;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;
import java.util.SortedMap;

import static com.cubeia.poker.action.PokerActionType.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class BringInRoundTest extends TestCase {

    private BigDecimal minBet;

    private PokerContext context;

    @Mock
    private ServerAdapterHolder adapterHolder;

    @Mock
    private ServerAdapter adapter;

    @Mock
    private PokerSettings settings;

    private ActionRequest requestedAction;

    private BringInRound round;

    @Mock
    private PlayerToActCalculator mockPlayerToAct;

    @Mock
    private FutureActionsCalculator mockFutureActionsCalculator;

    private ActionRequestFactory actionRequestFactory;

    FixedLimitBetStrategy betStrategy = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initMocks(this);


        when(settings.getBringInAmount()).thenReturn(BigDecimal.ONE);
        when(settings.getTiming()).thenReturn(new DefaultTimingProfile());
        when(settings.getRakeSettings()).thenReturn(RakeSettings.createDefaultRakeSettings(new BigDecimal(0.01)));
        when(settings.getCurrency()).thenReturn(new Currency("EUR", 2));
        context = new PokerContext(settings);
        when(adapterHolder.get()).thenReturn(adapter);
        minBet = new BigDecimal(2);
        betStrategy = new FixedLimitBetStrategy(minBet, false);
        actionRequestFactory = new ActionRequestFactory(betStrategy);
    }

    public void testHeadsUpBringIn() {
        MockPlayer[] p = TestUtils.createMockPlayers(2,500);
        when(mockPlayerToAct.getFirstPlayerToAct(isA(SortedMap.class),anyListOf(Card.class))).thenReturn(p[1]);
        when(mockPlayerToAct.getNextPlayerToAct(anyInt(),isA(SortedMap.class))).thenReturn(p[0]);
        preparePlayers(p);
        assertFalse("Round should not be finished.", round.isFinished());
        verifyAndAct(p[1], BRING_IN, new BigDecimal("1.00"));
        verifyAndAct(p[0],CALL,new BigDecimal("1.00"));
        assertTrue(round.isFinished());
    }
    public void testHeadsUpBetOnBringIn() {
        MockPlayer[] p = TestUtils.createMockPlayers(2,500);
        when(mockPlayerToAct.getFirstPlayerToAct(isA(SortedMap.class),anyListOf(Card.class))).thenReturn(p[1]);
        when(mockPlayerToAct.getNextPlayerToAct(anyInt(),isA(SortedMap.class))).thenReturn(p[0]);
        preparePlayers(p);
        assertFalse("Round should not be finished.", round.isFinished());
        verifyAndAct(p[1], BET, new BigDecimal("2.00"));
        verifyAndAct(p[0],CALL,new BigDecimal("2.00"));
        assertTrue(round.isFinished());
    }

    public void testHeadsUpBetOnBringInAndFold() {
        MockPlayer[] p = TestUtils.createMockPlayers(2,500);
        when(mockPlayerToAct.getFirstPlayerToAct(isA(SortedMap.class),anyListOf(Card.class))).thenReturn(p[1]);
        when(mockPlayerToAct.getNextPlayerToAct(anyInt(),isA(SortedMap.class))).thenReturn(p[0]);
        preparePlayers(p);
        assertFalse("Round should not be finished.", round.isFinished());
        verifyAndAct(p[1], BET, new BigDecimal("2.00"));
        verifyAndAct(p[0],FOLD,new BigDecimal("0"));
        assertTrue(round.isFinished());
    }

    public void testBringIn() {
        MockPlayer[] p = TestUtils.createMockPlayers(4,500);
        when(mockPlayerToAct.getFirstPlayerToAct(isA(SortedMap.class),anyListOf(Card.class))).thenReturn(p[1]);
        when(mockPlayerToAct.getNextPlayerToAct(anyInt(),isA(SortedMap.class))).thenReturn(p[2],p[3],p[0],p[1],p[2]);
        preparePlayers(p);
        assertFalse("Round should not be finished.", round.isFinished());
        verifyAndAct(p[1], BRING_IN, new BigDecimal("1.00"));
        verifyAndAct(p[2],CALL,new BigDecimal("1.00"));
        verifyAndAct(p[3],RAISE,new BigDecimal("2.00"));
        verifyAndAct(p[0],CALL,new BigDecimal("2.00"));
        verifyAndAct(p[1],CALL,new BigDecimal("1.00"));
        verifyAndAct(p[2],CALL,new BigDecimal("1.00"));
        assertTrue(round.isFinished());
    }

    public void testBringInAllIn() {
        MockPlayer[] p = TestUtils.createMockPlayers(4,100);
        p[2].setBalance(new BigDecimal("0.00"));

        when(mockPlayerToAct.getFirstPlayerToAct(isA(SortedMap.class), anyListOf(Card.class))).thenReturn(p[1]);
        when(mockPlayerToAct.getNextPlayerToAct(anyInt(),isA(SortedMap.class))).thenReturn(p[3],p[0],p[1],p[2]);
        preparePlayers(p);
        assertFalse("Round should not be finished.", round.isFinished());
        verifyAndAct(p[1], BRING_IN, new BigDecimal("1.00"));
        verifyAndAct(p[3],RAISE,new BigDecimal("2.00"));
        verifyAndAct(p[0],CALL,new BigDecimal("2.00"));
        verifyAndAct(p[1],CALL,new BigDecimal("1.00"));
        verifyAndAct(p[2],CALL,new BigDecimal("1.00"));
        assertTrue(round.isFinished());
    }
    public void testBringInGoAllIn() {
        MockPlayer[] p = TestUtils.createMockPlayers(4,100);
        p[2].setBalance(new BigDecimal("0.50"));

        when(mockPlayerToAct.getFirstPlayerToAct(isA(SortedMap.class), anyListOf(Card.class))).thenReturn(p[1]);
        when(mockPlayerToAct.getNextPlayerToAct(anyInt(),isA(SortedMap.class))).thenReturn(p[2],p[3],p[0],p[1],p[2]);
        preparePlayers(p);
        assertFalse("Round should not be finished.", round.isFinished());
        verifyAndAct(p[1], BRING_IN, new BigDecimal("1.00"));
        verifyAndAct(p[2], CALL, new BigDecimal("0.50"));
        verifyAndAct(p[3],RAISE,new BigDecimal("2.00"));
        verifyAndAct(p[0],CALL,new BigDecimal("2.00"));
        verifyAndAct(p[1],CALL,new BigDecimal("1.00"));
        verifyAndAct(p[2],CALL,new BigDecimal("1.00"));
        assertTrue(round.isFinished());
    }

    public void testAllInWhenBalanceLessOrEqualToBringIn() {
        MockPlayer[] p = TestUtils.createMockPlayers(2,1);
        when(mockPlayerToAct.getFirstPlayerToAct(isA(SortedMap.class),anyListOf(Card.class))).thenReturn(p[1]);
        when(mockPlayerToAct.getNextPlayerToAct(anyInt(),isA(SortedMap.class))).thenReturn(p[0]);
        preparePlayers(p);
        assertFalse("Round should NOT be finished", round.isFinished());
        assertEquals(p[1].getBalance(), BigDecimal.ZERO);
        assertEquals(p[1].getBetStack(),BigDecimal.ONE);
        assertTrue(p[1].isAllIn());
        assertFalse(p[0].isAllIn());
        verifyAndAct(p[0],CALL,new BigDecimal("1.00"));
        assertTrue("Round should be finished", round.isFinished());
    }

    // HELPERS
    private void act(MockPlayer player, PokerActionType action, BigDecimal amount) {
        PokerAction a = new PokerAction(player.getId(), action);
        a.setBetAmount(amount);
        round.act(a);
        requestedAction = getRequestedAction();
    }

    private void verifyAndAct(MockPlayer player, PokerActionType action, BigDecimal amount) {
        requestedAction = getRequestedAction();
        assertTrue("Tried to " + action + " but available actions were: " + player.getActionRequest().getOptions(),
                   player.getActionRequest().isOptionEnabled(action));
        assertTrue(requestedAction.isOptionEnabled(action));
        assertEquals(player.getId(), requestedAction.getPlayerId());
        act(player, action, amount);
    }

    private ActionRequest getRequestedAction() {
        ArgumentCaptor<ActionRequest> captor = ArgumentCaptor.forClass(ActionRequest.class);
        verify(adapter, atLeastOnce()).requestAction(captor.capture());
        return captor.getValue();
    }

    @SuppressWarnings("rawtypes")
    class IsListOfNElements extends ArgumentMatcher<List> {
        private final int n;

        public IsListOfNElements(int n) {
            this.n = n;
        }

        public boolean matches(Object list) {
            return ((List) list).size() == n;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Should be a list of " + n + " elements");
        }
    }

    private void preparePlayers(PokerPlayer... p) {
        for (PokerPlayer player : p) {
            context.addPlayer(player);
        }
        context.prepareHand(readyPlayerFilter());

        round = createRound(betStrategy);
    }

    private void preparePlayers(BetStrategy betStrategy, PokerPlayer... p) {
        for (PokerPlayer player : p) {
            context.addPlayer(player);
        }
        context.prepareHand(readyPlayerFilter());
        round = createRound(betStrategy);
    }

    private Predicate<PokerPlayer> readyPlayerFilter() {
        return new Predicate<PokerPlayer>() {
            @Override
            public boolean apply(@Nullable PokerPlayer pokerPlayer) {
                return true;
            }
        };
    }

    private PokerContext createMockContext() {
        PokerContext mock = mock(PokerContext.class);
        when(mock.getTimingProfile()).thenReturn(new DefaultTimingProfile());
        return mock;
    }

    private MockPlayer[] createAndAddPlayersToBettingRound(int numberOfPlayers) {
        MockPlayer[] p = TestUtils.createMockPlayers(numberOfPlayers);
        preparePlayers(p);
        return p;
    }

    private void actMax(PokerActionType action) {
        requestedAction = getRequestedAction();
        PossibleAction option = requestedAction.getOption(action);
        PokerAction a = new PokerAction(requestedAction.getPlayerId(), action, option.getMaxAmount());
        round.act(a);
    }

    private BringInRound createRound(BetStrategy betStrategy) {
        return new BringInRound(context, adapterHolder, mockPlayerToAct,actionRequestFactory,mockFutureActionsCalculator, betStrategy);
    }
}
