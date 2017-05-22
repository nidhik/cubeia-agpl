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

package com.cubeia.poker.variant.omaha;

import com.cubeia.games.poker.common.money.Currency;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.PokerVariant;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.betting.BetStrategyType;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.hand.*;
import com.cubeia.poker.handhistory.api.Results;
import com.cubeia.poker.model.BlindsLevel;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.result.Result;
import com.cubeia.poker.settings.PokerSettings;
import com.cubeia.poker.settings.RakeSettings;
import com.cubeia.poker.timing.impl.DefaultTimingProfile;
import com.cubeia.poker.variant.GameType;
import com.cubeia.poker.variant.HandFinishedListener;
import com.cubeia.poker.variant.PokerGameBuilder;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.cubeia.poker.action.PokerActionType.*;
import static com.cubeia.poker.rounds.betting.BettingRoundName.*;
import static com.cubeia.poker.variant.RoundCreators.*;
import static com.cubeia.poker.variant.RoundCreators.bettingRound;
import static com.cubeia.poker.variant.RoundCreators.dealCommunityCards;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OmahaTest {

    @Mock
    private Deck mockDeck;



    @Mock
    private ServerAdapterHolder serverAdapterHolder;


    private HandFinishedListener handFinishedListener;

    @Mock
    private ServerAdapter serverAdapter;

    private int winnerPlayerId;
    private HandType winningHand = null;

    private GameType omaha;

    private RakeSettings rakeSettings;

    private MockPlayer[] p;

    private Random randomizer = new Random();

    @Before
    public void setup() {
        initMocks(this);
        rakeSettings = RakeSettings.createDefaultRakeSettings(BigDecimal.valueOf(0));

        handFinishedListener = new HandFinishedListener() {
            @Override
            public void handFinished(HandResult result, HandEndStatus status) {
                Map<PokerPlayer,Result> results = result.getResults();
                for(PokerPlayer p : results.keySet()) {
                   if(p.getId() == winnerPlayerId) {
                       BigDecimal netResult = results.get(p).getNetResult();
                       Assert.assertTrue("player " + p.getId() + " expected to win but had net result = " + netResult, netResult.compareTo(BigDecimal.ZERO)>0);

                   }
                }
                if(winningHand!=null) {
                    List<RatedPlayerHand> playerHands = result.getPlayerHands();
                    for(RatedPlayerHand ph : playerHands) {
                        if(ph.getPlayerId() == winnerPlayerId) {
                            Assert.assertEquals(ph.getHandInfo().getHandType(),winningHand);
                        }
                    }
                }

            }
        };


        when(serverAdapterHolder.get()).thenReturn(serverAdapter);
        when(serverAdapter.getSystemRNG()).thenReturn(randomizer);

        omaha = createOmaha();
        omaha.addHandFinishedListener(handFinishedListener);
    }

    @Test
    public void testBasicHand() {
        when(mockDeck.deal()).thenReturn(Card.fromString("7H"),new Hand("8S 2D 2S KD 5C 8H TC QH QC TH 5H 6D 9H 6S KH AC").getCards().toArray(new Card[0]));
        winnerPlayerId = 101;
        startHand(prepareContext(3,new BigDecimal[]{new BigDecimal("747.92"), new BigDecimal("248.19"), new BigDecimal("1000.00")}));

        act(p[1], SMALL_BLIND);
        act(p[2], BIG_BLIND);
        timeout();
        ensureCards(p[0], new Hand("7H 8S 2D 2S").getCards());
        ensureCards(p[1], new Hand("KD 5C 8H TC").getCards());
        ensureCards(p[2], new Hand("QH QC TH 5H").getCards());

        act(p[0], CALL);
        act(p[1], RAISE, new BigDecimal("40.00"),new BigDecimal("40.00"));
        act(p[2], RAISE, new BigDecimal("130.00"), new BigDecimal("130.00"));
        act(p[0],FOLD);
        act(p[1], RAISE, new BigDecimal("248.19"), new BigDecimal("248.19"));
        Assert.assertTrue(p[0].hasActed());
        Assert.assertTrue(p[1].hasActed());
        act(p[2],CALL);

        Assert.assertTrue(p[0].hasFolded());
        timeout();

        //flop
        timeout();
        timeout();

        // Turn
        timeout();
        timeout();

        // River
        timeout();
        timeout();
        //Assert.assertTrue(p[1].getBalance().compareTo(BigDecimal.ZERO)>0);

        Assert.assertEquals(4,p[0].getPrivatePocketCards().size());
        Assert.assertEquals(4,p[1].getPrivatePocketCards().size());
        Assert.assertEquals(4,p[2].getPrivatePocketCards().size());


    }

    @Test
    public void testBasicHandTwo() {
        when(mockDeck.deal()).thenReturn(Card.fromString("7S"),new Hand("7D AS 6S 8S 8C 2S 8D 4H 4D 3D KS 3C KC 5D AH AD").getCards().toArray(new Card[0]));
        winnerPlayerId = 100;
        winningHand = HandType.THREE_OF_A_KIND;

        startHand(prepareContext(3,new BigDecimal[]{new BigDecimal("218.64"), new BigDecimal("520.25"), new BigDecimal("55.21")}));
        act(p[1], SMALL_BLIND);
        act(p[2], BIG_BLIND);
        timeout();
        ensureCards(p[0], new Hand("7S 7D AS 6S").getCards());
        ensureCards(p[1], new Hand("8S 8C 2S 8D").getCards());
        ensureCards(p[2], new Hand("4H 4D 3D KS").getCards());

        act(p[0], CALL);
        act(p[1], CALL);
        act(p[2], CHECK);

        timeout();

        //flop
        timeout();
        act(p[0], CHECK);
        act(p[1], CHECK);
        act(p[2], CHECK);


        // Turn
        timeout();
        act(p[0], CHECK);
        act(p[1], CHECK);
        act(p[2], CHECK);

        // River
        timeout();
        act(p[0], CHECK);
        act(p[1], CHECK);
        act(p[2], CHECK);
        //Assert.assertTrue(p[1].getBalance().compareTo(BigDecimal.ZERO)>0);

        Assert.assertEquals(4,p[0].getPrivatePocketCards().size());
        Assert.assertEquals(4,p[1].getPrivatePocketCards().size());
        Assert.assertEquals(4,p[2].getPrivatePocketCards().size());


    }

    private void ensureCards(MockPlayer mockPlayer, List<Card> cards) {
        Hand pocketCards = mockPlayer.getPocketCards();
        for(Card c : cards){
            hasCard(pocketCards.getCards(), c);
        }
    }

    private void hasCard(List<Card> cards, Card mustHave) {
        for(Card c : cards) {
            if(c.getRank().equals(mustHave.getRank()) && c.getSuit().equals(mustHave.getSuit())) {
                return;
            }
        }
        Assert.fail("Player didn't have card " + mustHave + ", actual = " + cards);
    }


    private void timeout() {
        omaha.timeout();
    }

    private boolean act(MockPlayer player, PokerActionType actionType) {
        return omaha.act(new PokerAction(player.getId(), actionType));
    }
    private boolean act(MockPlayer player, PokerActionType actionType, BigDecimal raise, BigDecimal bet) {
        PokerAction action = new PokerAction(player.getId(), actionType);
        if(raise!=null) {
            action.setRaiseAmount(raise);
        }
        if(bet!=null) {
            action.setBetAmount(bet);
        }
        return omaha.act(action);
    }

    private void act(PokerActionType actionType, int value) {
        act(actionType,new BigDecimal(value));
    }
    private void act(PokerActionType actionType, BigDecimal value) {
        PokerPlayer playerToAct = getPlayerToAct();
        omaha.act(new PokerAction(playerToAct.getId(), actionType, value));
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

    public GameType createOmaha() {
        return new PokerGameBuilder().withRounds(
                blinds(false),
                dealFaceDownCards(4),
                bettingRound(PRE_FLOP, fromBigBlind(), false),
                dealCommunityCards(3),
                bettingRound(FLOP, false),
                dealCommunityCards(1),
                bettingRound(TURN),
                dealCommunityCards(1),
                bettingRound(RIVER)).withDeckProvider(new DeckProvider() {
            @Override
            public Deck createNewDeck(Random randomizer, int playersAtTable) {
                return getDeck();
            }
        }).withHandEvaluator(new OmahaHandCalculator()).build();



    }
    public Deck getDeck(){
        return mockDeck;
    }
    private void startHand(PokerContext context) {
        Predicate<PokerPlayer> allGood = Predicates.alwaysTrue();
        context.prepareHand(allGood);
        omaha.startHand();
    }

    private PokerContext prepareContext(int numberOfPlayers, BigDecimal... balances) {
        BlindsLevel level = new BlindsLevel(new BigDecimal("5.00"), new BigDecimal("10.00"), BigDecimal.ZERO);
        BetStrategyType betStrategy = BetStrategyType.NO_LIMIT;
        PokerSettings settings = new PokerSettings(PokerVariant.OMAHA,level, betStrategy, new BigDecimal(100), new BigDecimal(5000), new DefaultTimingProfile(), 6, rakeSettings, new Currency("EUR",2), null);
        PokerContext context = new PokerContext(settings);
        omaha.setPokerContextAndServerAdapter(context, serverAdapterHolder);
        p = TestUtils.createMockPlayers(numberOfPlayers,balances);
        for (PokerPlayer player : p) {
            player.setHasPostedEntryBet(true);
            context.addPlayer(player);
        }
        return context;
    }


}
