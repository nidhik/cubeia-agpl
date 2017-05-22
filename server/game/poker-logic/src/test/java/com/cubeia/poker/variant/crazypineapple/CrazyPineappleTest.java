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

package com.cubeia.poker.variant.crazypineapple;

import com.cubeia.games.poker.common.money.Currency;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.PokerVariant;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.DiscardAction;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.betting.BetStrategyType;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.model.BlindsLevel;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.timing.impl.DefaultTimingProfile;
import com.cubeia.poker.variant.GameType;
import com.cubeia.poker.variant.HandFinishedListener;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Random;

import static com.cubeia.poker.action.PokerActionType.*;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CrazyPineappleTest {

    @Mock
    private ServerAdapterHolder serverAdapterHolder;

    @Mock
    private HandFinishedListener handFinishedListener;

    @Mock
    private ServerAdapter serverAdapter;

    private GameType crazyPineapple;

    private RakeSettings rakeSettings;

    private MockPlayer[] p;

    private Random randomizer = new Random();

    @Before
    public void setup() {
        initMocks(this);
        rakeSettings = RakeSettings.createDefaultRakeSettings(BigDecimal.valueOf(0));
        crazyPineapple = CrazyPineapple.createGame();
        crazyPineapple.addHandFinishedListener(handFinishedListener);

        when(serverAdapterHolder.get()).thenReturn(serverAdapter);
        when(serverAdapter.getSystemRNG()).thenReturn(randomizer);
    }

    @Test
    public void testBasicHand() {
        startHand(prepareContext(3));

        act(p[1], SMALL_BLIND);
        act(p[2], BIG_BLIND);
        timeout();
        act(p[0], CALL);
        act(p[1], CALL);
        act(p[2], CHECK);

        // Flop
        timeout();
        act(p[1], CHECK);
        act(p[2], CHECK);
        act(p[0], CHECK);

        // Discard round
        Assert.assertTrue(discard(p[1], 3));
        Assert.assertTrue(discard(p[2], 6));
        Assert.assertTrue(discard(p[0], 0));

        // Turn
        timeout();
        act(p[1], CHECK);
        act(p[2], CHECK);
        act(p[0], CHECK);

        // River
        timeout();
        act(p[1], CHECK);
        act(p[2], CHECK);
        act(p[0], CHECK);

        Assert.assertEquals(2,p[0].getPrivatePocketCards().size());
        Assert.assertEquals(2,p[1].getPrivatePocketCards().size());
        Assert.assertEquals(2,p[2].getPrivatePocketCards().size());

        verify(handFinishedListener).handFinished(Mockito.<HandResult>any(), eq(HandEndStatus.NORMAL));
    }

    @Test
    public void testBugHand() {
        startHand(prepareContext(6));

        act(p[1], SMALL_BLIND);
        act(p[2], BIG_BLIND);

        timeout();
        Assert.assertTrue(act(p[3], CALL));
        Assert.assertTrue(act(p[4], FOLD));
        Assert.assertTrue(act(p[5], RAISE,null, new BigDecimal(600)));
        Assert.assertTrue(act(p[0], FOLD));
        Assert.assertTrue(act(p[1], FOLD));
        Assert.assertTrue(act(p[2], CALL));
        Assert.assertTrue(act(p[3], FOLD));

        // Flop
        timeout();

        Assert.assertTrue(act(p[2], CHECK));
        Assert.assertTrue(act(p[5], BET, null,new BigDecimal(300)));
        Assert.assertTrue(act(p[2], CALL));

        Assert.assertTrue(discard(p[2],8));
        Assert.assertTrue(discard(p[5],15));

        // Turn
        timeout();
        Assert.assertTrue(act(p[2], CHECK));
        Assert.assertTrue(act(p[5], CHECK));

        // River
        timeout();
        Assert.assertTrue(act(p[2], CHECK));
        Assert.assertTrue(act(p[5], CHECK));


        verify(handFinishedListener).handFinished(Mockito.<HandResult>any(), eq(HandEndStatus.NORMAL));
    }




    private void timeout() {
        crazyPineapple.timeout();
    }

    private boolean act(MockPlayer player, PokerActionType actionType) {
        return crazyPineapple.act(new PokerAction(player.getId(), actionType));
    }
    private boolean act(MockPlayer player, PokerActionType actionType, BigDecimal raise, BigDecimal bet) {
        PokerAction action = new PokerAction(player.getId(), actionType);
        if(raise!=null) {
            action.setRaiseAmount(raise);
        }
        if(bet!=null) {
            action.setBetAmount(bet);
        }
        return crazyPineapple.act(action);
    }

    private boolean discard(MockPlayer player, int card) {
        return crazyPineapple.act(new DiscardAction(player.getId(), singletonList(card)));
    }
    private void act(PokerActionType actionType, int value) {
        act(actionType,new BigDecimal(value));
    }
    private void act(PokerActionType actionType, BigDecimal value) {
        PokerPlayer playerToAct = getPlayerToAct();
        crazyPineapple.act(new PokerAction(playerToAct.getId(), actionType, value));
    }

    private PokerPlayer getPlayerToAct() {
        ArgumentCaptor<ActionRequest> captor = ArgumentCaptor.forClass(ActionRequest.class);
        verify(serverAdapter, atLeastOnce()).requestAction(captor.capture());
        int playerId = captor.getValue().getPlayerId();
        for (MockPlayer mockPlayer : p) {
            if (mockPlayer.getId() == playerId) {
                return mockPlayer;
            }
        }
        return null;
    }

    private void startHand(PokerContext context) {
        Predicate<PokerPlayer> allGood = Predicates.alwaysTrue();
        context.prepareHand(allGood);
        crazyPineapple.startHand();
    }

    private PokerContext prepareContext(int numberOfPlayers) {
        BlindsLevel level = new BlindsLevel(new BigDecimal(50), new BigDecimal(100), BigDecimal.ZERO);
        BetStrategyType betStrategy = BetStrategyType.NO_LIMIT;
        PokerSettings settings = new PokerSettings(PokerVariant.CRAZY_PINEAPPLE,level, betStrategy, new BigDecimal(100), new BigDecimal(5000), new DefaultTimingProfile(), 6, rakeSettings, new Currency("EUR",2), null);
        PokerContext context = new PokerContext(settings);
        crazyPineapple.setPokerContextAndServerAdapter(context, serverAdapterHolder);
        p = TestUtils.createMockPlayers(numberOfPlayers);
        for (PokerPlayer player : p) {
            player.setHasPostedEntryBet(true);
            context.addPlayer(player);
        }
        return context;
    }


}
