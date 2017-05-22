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

package com.cubeia.poker.variant.texasholdem;

import com.cubeia.games.poker.common.money.Currency;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNG;
import com.cubeia.poker.PokerVariant;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.betting.BetStrategyType;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Deck;
import com.cubeia.poker.hand.DeckProvider;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.model.BlindsLevel;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.rake.LinearRakeWithLimitCalculator;
import com.cubeia.poker.result.RevealOrderCalculator;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.timing.impl.DefaultTimingProfile;
import com.cubeia.poker.variant.*;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static com.cubeia.poker.action.PokerActionType.*;
import static com.cubeia.poker.rounds.betting.BettingRoundName.*;
import static com.cubeia.poker.variant.RoundCreators.*;
import static junit.framework.Assert.assertFalse;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TexasHoldemTournamentAllInTest {

//    private TexasHoldem texas;

    private GenericPokerGame texas;

    @Mock
    private PokerContext context;

    @Mock
    private PokerSettings settings;

    @Mock
    private Random random;
    
    @Mock
    private ServerAdapterHolder serverAdapterHolder;

    @Mock
    private ServerAdapter serverAdapter;

    @Mock
    private HandFinishedListener listener;

    @Mock
    private RevealOrderCalculator revealOrderCalculator;

    @Mock
    private Predicate<PokerPlayer> filter;

    private MockPlayer player1;

    private MockPlayer player2;

    private PotHolder potHolder;

    private TreeMap<Integer, PokerPlayer> seatingMap;

    private Map<Integer, PokerPlayer> playerMap;

    private RakeSettings rakeSettings;

    private MockPlayer[] p;

    @Mock
    private Deck mockDeck;

    @Before
    public void setup() {
        initMocks(this);
        rakeSettings = new RakeSettings(new BigDecimal("0.06"), bd(500), bd(150));
        potHolder = new PotHolder(new LinearRakeWithLimitCalculator(rakeSettings, new Currency("EUR",2)));
        when(context.getPotHolder()).thenReturn(potHolder);
        when(serverAdapterHolder.get()).thenReturn(serverAdapter);
        when(serverAdapter.getSystemRNG()).thenReturn(new NonRandomRNG());
        when(context.getSettings()).thenReturn(settings);
        when(settings.getCurrency()).thenReturn(new Currency("EUR",2));

        texas = (GenericPokerGame) createTexasHoldem();
        texas.setPokerContextAndServerAdapter(context, serverAdapterHolder);
        texas.addHandFinishedListener(listener);

        player1 = new MockPlayer(1);
        player2 = new MockPlayer(2);
        when(mockDeck.deal()).thenReturn(Card.fromString("8S"),new Hand("KC QS 9S 3S 8C 8D 3C 2C QC TC 5S 7D").getCards().toArray(new Card[0]));
    }


    public GameType createTexasHoldem() {
        return new PokerGameBuilder().withRounds(
                blinds(),
                dealFaceDownCards(2),
                bettingRound(PRE_FLOP, fromBigBlind()),
                dealCommunityCards(3),
                bettingRound(FLOP),
                dealCommunityCards(1),
                bettingRound(TURN),
                dealCommunityCards(1),
                bettingRound(RIVER)).withDeckProvider(new DeckProvider() {
            @Override
            public Deck createNewDeck(Random randomizer, int playersAtTable) {
                return getDeck();
            }
        }).build();

    }
    public Deck getDeck(){
        return mockDeck;
    }

    private BigDecimal bd(int i) {
        return new BigDecimal(i).setScale(2);
    }

    private void prepareContext(PokerPlayer ... players) {
        seatingMap = new TreeMap<Integer, PokerPlayer>();
        playerMap = new HashMap<Integer, PokerPlayer>();
        for (PokerPlayer player : players) {
            seatingMap.put(player.getSeatId(), player);
            playerMap.put(player.getId(), player);
        }
        when(context.getCurrentHandSeatingMap()).thenReturn(seatingMap);
        when(context.getCurrentHandPlayerMap()).thenReturn(playerMap);
    }
    @Test
    public void testAllInOnBigBlind() {

        BlindsLevel level = new BlindsLevel(bd(160), bd(320), bd(0));
        BetStrategyType betStrategy = BetStrategyType.NO_LIMIT;
        PokerSettings settings = new PokerSettings(PokerVariant.TEXAS_HOLDEM,level, betStrategy, bd(0), bd(50000), new DefaultTimingProfile(), 6, rakeSettings, new Currency("EUR",2), null);
        PokerContext context = new PokerContext(settings);

        context.setTournamentId(1);
        context.setTournamentTable(true);
        context.getBlindsInfo().setDealerButtonSeatId(0);

        texas.setPokerContextAndServerAdapter(context, serverAdapterHolder);
        MockPlayer[] p  = new MockPlayer[2];

        TestUtils.createMockPlayer(new BigDecimal("1000.00"),p,0,0,0);
        TestUtils.createMockPlayer(new BigDecimal("120.00"),p,1,5,1);


        for (PokerPlayer player : p) {
            player.setHasPostedEntryBet(false);
            context.addPlayer(player);
        }
        startHand(context);

        act(p[0], SMALL_BLIND);
        act(p[1], BIG_BLIND);


        texas.timeout();
        texas.timeout();
        texas.timeout();

        assertTrue(p[1].isAllIn());
        assertThat(p[1].getBalance(), is(new BigDecimal("0.00")));

        assertFalse(p[0].isAllIn());
        assertThat(p[0].getBalance(), is(new BigDecimal("880.00")));

    }
    @Test
    public void testAllInOnSmallBlind() {

        BlindsLevel level = new BlindsLevel(bd(160), bd(320), bd(0));
        BetStrategyType betStrategy = BetStrategyType.NO_LIMIT;
        PokerSettings settings = new PokerSettings(PokerVariant.TEXAS_HOLDEM,level, betStrategy, bd(0), bd(50000), new DefaultTimingProfile(), 6, rakeSettings, new Currency("EUR",2), null);
        PokerContext context = new PokerContext(settings);

        context.setTournamentId(1);
        context.setTournamentTable(true);
        context.getBlindsInfo().setDealerButtonSeatId(0);

        texas.setPokerContextAndServerAdapter(context, serverAdapterHolder);
        MockPlayer[] p  = new MockPlayer[2];

        TestUtils.createMockPlayer(new BigDecimal("120.00"),p,0,0,0);
        TestUtils.createMockPlayer(new BigDecimal("1000.00"),p,1,5,1);


        for (PokerPlayer player : p) {
            player.setHasPostedEntryBet(false);
            context.addPlayer(player);
        }
        startHand(context);

        act(p[0], SMALL_BLIND);
        act(p[1], BIG_BLIND);


        texas.timeout();
        texas.timeout();
        texas.timeout();

        assertTrue(p[0].isAllIn());
        assertThat(p[0].getBalance(), is(new BigDecimal("0.00")));

        assertFalse(p[1].isAllIn());
        assertThat(p[1].getBalance(), is(new BigDecimal("880.00")));

    }

    @Test
    public void testAllInOnSmallAndBigBlindInTournament() {

        BlindsLevel level = new BlindsLevel(bd(160), bd(320), bd(0));
        BetStrategyType betStrategy = BetStrategyType.NO_LIMIT;
        PokerSettings settings = new PokerSettings(PokerVariant.TEXAS_HOLDEM,level, betStrategy, bd(0), bd(50000), new DefaultTimingProfile(), 6, rakeSettings, new Currency("EUR",2), null);
        PokerContext context = new PokerContext(settings);

        context.setTournamentId(1);
        context.setTournamentTable(true);
        context.getBlindsInfo().setDealerButtonSeatId(0);

        texas.setPokerContextAndServerAdapter(context, serverAdapterHolder);
        MockPlayer[] p  = new MockPlayer[4];

        TestUtils.createMockPlayer(new BigDecimal("12920.00"),p,0,0,0);
        TestUtils.createMockPlayer(new BigDecimal("120.00"),p,1,5,1);
        TestUtils.createMockPlayer(new BigDecimal("80.00"),p,2,6,2);
        TestUtils.createMockPlayer(new BigDecimal("1880.00"),p,3,7,3);

        for (PokerPlayer player : p) {
            player.setHasPostedEntryBet(false);
            context.addPlayer(player);
        }
        startHand(context);



        act(p[1], SMALL_BLIND);
        assertTrue(p[1].isAllIn());
        assertThat(p[1].getBalance(), is(new BigDecimal("0.00")));

        act(p[2], BIG_BLIND);
        assertTrue(p[2].isAllIn());
        assertThat(p[2].getBalance(), is(new BigDecimal("0.00")));

        texas.timeout();

        act(p[3], FOLD);

        act(p[0], CALL);

        assertThat(context.countNonFoldedPlayers(), is(3));
        assertThat(p[1].getBalance(),is(new BigDecimal("0.00")));
        assertThat(p[2].getBalance(),is(new BigDecimal("0.00")));
        assertThat(p[0].getBalance(),is(new BigDecimal("12800.00")));
        assertThat(context.getTotalPotSize(), is(new BigDecimal("320.00")));

    }



    private Predicate<PokerPlayer> mockFilterThatAllows(MockPlayer... players) {
        for (PokerPlayer player : players) {
            when(filter.apply(player)).thenReturn(true);
        }
        return filter;
    }

    private void createPot() {
        potHolder.getActivePot().bet(player1, bd(600));
        potHolder.getActivePot().bet(player2, bd(600));
    }

    private void startHand(PokerContext context) {
        startHand(context, Predicates.<PokerPlayer>alwaysTrue());
    }

    private void startHand(PokerContext context, Predicate<PokerPlayer> filter) {
        context.prepareHand(filter);
        texas.startHand();
    }

    private void act(MockPlayer player, PokerActionType actionType) {
        texas.act(new PokerAction(player.getId(), actionType));
    }

    private void act(MockPlayer player, PokerActionType actionType, int value) {
        texas.act(new PokerAction(player.getId(), actionType, new BigDecimal(value)));
    }
    private PokerContext prepareContext(int numberOfPlayers) {
        return prepareContext(numberOfPlayers,false);
    }

    private PokerContext prepareContext(int numberOfPlayers, boolean tournament) {
        BlindsLevel level = new BlindsLevel(bd(10), bd(20), bd(0));
        BetStrategyType betStrategy = BetStrategyType.NO_LIMIT;
        PokerSettings settings = new PokerSettings(PokerVariant.TEXAS_HOLDEM,level, betStrategy, bd(100), bd(5000), new DefaultTimingProfile(), 6, rakeSettings, new Currency("EUR",2), null);
        PokerContext context = new PokerContext(settings);
        if(tournament) {
            context.setTournamentId(1);
            context.setTournamentTable(true);
        }
        texas.setPokerContextAndServerAdapter(context, serverAdapterHolder);
        p = TestUtils.createMockPlayers(numberOfPlayers);
        for (PokerPlayer player : p) {
            if(tournament) {
                player.setHasPostedEntryBet(true);
            }
            context.addPlayer(player);
        }
        return context;
    }


}
