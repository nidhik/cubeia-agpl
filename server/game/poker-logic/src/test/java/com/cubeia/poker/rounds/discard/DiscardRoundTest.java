package com.cubeia.poker.rounds.discard;

import com.cubeia.poker.action.DiscardAction;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.player.DefaultPokerPlayer;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.rounds.betting.PlayerToActCalculator;
import com.cubeia.poker.timing.TimingProfile;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class DiscardRoundTest {

    private DiscardRound round;
    @Mock
    private PokerContext context;
    @Mock
    private ServerAdapterHolder adapterHolder;
    @Mock
    private PlayerToActCalculator calculator;
    private SortedMap<Integer, PokerPlayer> players = new TreeMap<>();
    @Mock
    private ServerAdapter adapter;
    @Captor
    private ArgumentCaptor<DiscardAction> discardActionCaptor;

    @Before
    public void setUp() {
        initMocks(this);
        when(adapterHolder.get()).thenReturn(adapter);
        round = new DiscardRound(context, adapterHolder, calculator, 2, true);
        when(context.getTimingProfile()).thenReturn(new TimingProfile());
    }

    @Test
    public void testTimeout() {
        DefaultPokerPlayer p1 = new DefaultPokerPlayer(1);
        p1.setHasActed(false);
        p1.addPocketCard(new Card(7, "5C"), false);
        p1.addPocketCard(new Card(4, "6C"), false);
        p1.addPocketCard(new Card(9, "7C"), false);

        DefaultPokerPlayer p2 = new DefaultPokerPlayer(2);
        p2.setHasActed(false);
        p2.addPocketCard(new Card(1, "5D"), false);
        p2.addPocketCard(new Card(9, "6d"), false);
        p2.addPocketCard(new Card(12, "7D"), false);

        players.put(1, p1);
        players.put(2, p2);
        when(context.getCurrentHandSeatingMap()).thenReturn(players);
        round.timeout();

        // Verify that players now have only have 1 card left (catches bug where we didn't use the cardId).
        assertThat(p1.getPocketCards().getCards().size(), is(1));
        assertThat(p2.getPocketCards().getCards().size(), is(1));

        // Verify that the correct playerIds are used (catches bug where the playerToAct id was used).
        verify(adapter, times(2)).notifyDiscards(discardActionCaptor.capture(), isA(PokerPlayer.class));
        assertThat(discardActionCaptor.getAllValues().get(0).getPlayerId(), is(1));
        assertThat(discardActionCaptor.getAllValues().get(1).getPlayerId(), is(2));
    }

    @Test
    public void testAutoDiscardWhenAwayBeforeDiscardRoundStarts() {
        DefaultPokerPlayer p1 = new DefaultPokerPlayer(1);
        p1.setAway(true);
        p1.setHasActed(false);
        p1.addPocketCard(new Card(7, "5C"), false);
        p1.addPocketCard(new Card(4, "6C"), false);
        p1.addPocketCard(new Card(9, "7C"), false);

        DefaultPokerPlayer p2 = new DefaultPokerPlayer(2);
        p2.setHasActed(false);
        p2.addPocketCard(new Card(1, "5D"), false);
        p2.addPocketCard(new Card(9, "6d"), false);
        p2.addPocketCard(new Card(12, "7D"), false);

        when(context.getPlayersInHand()).thenReturn(Arrays.asList(new PokerPlayer[]{p1,p2}));
        //round folds players when it's created
        round = new DiscardRound(context, adapterHolder, calculator, 1, true);

        players.put(1, p1);
        players.put(2, p2);
        when(context.getCurrentHandSeatingMap()).thenReturn(players);
        assertTrue(p1.hasActed());
        assertThat(p1.getPocketCards().getCards().size(), is(2));
        Assert.assertFalse(p2.hasActed());
        assertThat(p2.getPocketCards().getCards().size(), is(3));
        round.timeout();

        // Verify that players now have only have 1 card left (catches bug where we didn't use the cardId).
        assertThat(p2.getPocketCards().getCards().size(), is(2));
        Assert.assertTrue(p2.hasActed());
        // Verify that the correct playerIds are used (catches bug where the playerToAct id was used).
        verify(adapter, times(2)).notifyDiscards(discardActionCaptor.capture(), isA(PokerPlayer.class));
        assertThat(discardActionCaptor.getAllValues().get(0).getPlayerId(), is(1));
        assertThat(discardActionCaptor.getAllValues().get(1).getPlayerId(), is(2));
    }

    @Test
    public void testAutoDiscardWhenAllIsAwayBeforeDiscardRoundStarts() {
        DefaultPokerPlayer p1 = new DefaultPokerPlayer(1);
        p1.setAway(true);
        p1.addPocketCard(new Card(7, "5C"), false);
        p1.addPocketCard(new Card(4, "6C"), false);
        p1.addPocketCard(new Card(9, "7C"), false);

        DefaultPokerPlayer p2 = new DefaultPokerPlayer(2);
        p2.setHasActed(false);
        p2.setAway(true);
        p2.addPocketCard(new Card(1, "5D"), false);
        p2.addPocketCard(new Card(9, "6d"), false);
        p2.addPocketCard(new Card(12, "7D"), false);

        when(context.getPlayersInHand()).thenReturn(Arrays.asList(new PokerPlayer[]{p1,p2}));
        //round folds players when it's created
        round = new DiscardRound(context, adapterHolder, calculator, 1, true);

        players.put(1, p1);
        players.put(2, p2);
        when(context.getCurrentHandSeatingMap()).thenReturn(players);
        assertTrue(p1.hasActed());
        assertThat(p1.getPocketCards().getCards().size(), is(2));
        assertTrue(p2.hasActed());
        assertThat(p2.getPocketCards().getCards().size(), is(2));
        assertTrue(round.isFinished());
        round.timeout();

        // Verify that players now have only have 1 card left (catches bug where we didn't use the cardId).
        assertThat(p2.getPocketCards().getCards().size(), is(2));
        assertTrue(p2.hasActed());
        // Verify that the correct playerIds are used (catches bug where the playerToAct id was used).
        verify(adapter, times(2)).notifyDiscards(discardActionCaptor.capture(), isA(PokerPlayer.class));
        assertThat(discardActionCaptor.getAllValues().get(0).getPlayerId(), is(1));
        assertThat(discardActionCaptor.getAllValues().get(1).getPlayerId(), is(2));
    }

    @Test
    public void testAutoDiscardWhenSittingOutBeforeDiscardRoundStarts() {
        DefaultPokerPlayer p1 = new DefaultPokerPlayer(1);

        p1.setHasActed(false);
        p1.addPocketCard(new Card(7, "5C"), false);
        p1.addPocketCard(new Card(4, "6C"), false);
        p1.addPocketCard(new Card(9, "7C"), false);

        DefaultPokerPlayer p2 = new DefaultPokerPlayer(2);
        p2.setHasActed(false);
        p2.setSitOutStatus(SitOutStatus.SITTING_OUT);
        p2.addPocketCard(new Card(1, "5D"), false);
        p2.addPocketCard(new Card(9, "6d"), false);
        p2.addPocketCard(new Card(12, "7D"), false);

        when(context.getPlayersInHand()).thenReturn(Arrays.asList(new PokerPlayer[]{p1,p2}));
        //round folds players when it's created
        round = new DiscardRound(context, adapterHolder, calculator, 1, true);

        players.put(1, p1);
        players.put(2, p2);
        when(context.getCurrentHandSeatingMap()).thenReturn(players);
        assertTrue(p2.hasActed());
        assertThat(p2.getPocketCards().getCards().size(), is(2));
        Assert.assertFalse(p1.hasActed());
        assertThat(p1.getPocketCards().getCards().size(), is(3));
        round.timeout();

        // Verify that players now have only have 1 card left (catches bug where we didn't use the cardId).
        assertThat(p1.getPocketCards().getCards().size(), is(2));
        Assert.assertTrue(p1.hasActed());
        // Verify that the correct playerIds are used (catches bug where the playerToAct id was used).
        verify(adapter, times(2)).notifyDiscards(discardActionCaptor.capture(), isA(PokerPlayer.class));
        assertThat(discardActionCaptor.getAllValues().get(0).getPlayerId(), is(2));
        assertThat(discardActionCaptor.getAllValues().get(1).getPlayerId(), is(1));
    }


    @Test
    public void testAutoDiscard() {
        DefaultPokerPlayer p1 = new DefaultPokerPlayer(1);

        p1.setHasActed(false);
        p1.addPocketCard(new Card(7, "5C"), false);
        p1.addPocketCard(new Card(4, "6C"), false);
        p1.addPocketCard(new Card(9, "7C"), false);

        DefaultPokerPlayer p2 = new DefaultPokerPlayer(2);
        p2.setHasActed(false);
        p2.addPocketCard(new Card(1, "5D"), false);
        p2.addPocketCard(new Card(9, "6d"), false);
        p2.addPocketCard(new Card(12, "7D"), false);

        when(context.getPlayersInHand()).thenReturn(Arrays.asList(new PokerPlayer[]{p1,p2}));
        when(context.getPlayerInCurrentHand(1)).thenReturn(p1);
        when(context.getPlayerInCurrentHand(2)).thenReturn(p2);
        //round folds players when it's created
        round = new DiscardRound(context, adapterHolder, calculator, 1, true);

        players.put(1, p1);
        players.put(2, p2);
        when(context.getCurrentHandSeatingMap()).thenReturn(players);
        Assert.assertFalse(p2.hasActed());
        assertThat(p2.getPocketCards().getCards().size(), is(3));
        Assert.assertFalse(p1.hasActed());
        assertThat(p1.getPocketCards().getCards().size(), is(3));
        round.act(new DiscardAction(p1.getId(),Arrays.asList(new Integer[]{7})));
        assertTrue(p1.hasActed());
        round.timeout();
        assertTrue(p2.hasActed());
        assertThat(p2.getPocketCards().getCards().size(), is(2));

        // Verify that players now have only have 1 card left (catches bug where we didn't use the cardId).
        assertThat(p1.getPocketCards().getCards().size(), is(2));
        Assert.assertTrue(p1.hasActed());
        // Verify that the correct playerIds are used (catches bug where the playerToAct id was used).
        verify(adapter, times(2)).notifyDiscards(discardActionCaptor.capture(), isA(PokerPlayer.class));
        assertThat(discardActionCaptor.getAllValues().get(0).getPlayerId(), is(1));
        assertThat(discardActionCaptor.getAllValues().get(1).getPlayerId(), is(2));
    }


    @Test
    public void testMultipleActsFromSameUser() {
        DefaultPokerPlayer p1 = new DefaultPokerPlayer(1);

        p1.setHasActed(false);
        p1.addPocketCard(new Card(7, "5C"), false);
        p1.addPocketCard(new Card(4, "6C"), false);
        p1.addPocketCard(new Card(9, "7C"), false);

        DefaultPokerPlayer p2 = new DefaultPokerPlayer(2);
        p2.setHasActed(false);
        p2.addPocketCard(new Card(1, "5D"), false);
        p2.addPocketCard(new Card(9, "6d"), false);
        p2.addPocketCard(new Card(12, "7D"), false);

        when(context.getPlayersInHand()).thenReturn(Arrays.asList(new PokerPlayer[]{p1,p2}));
        when(context.getPlayerInCurrentHand(1)).thenReturn(p1);
        when(context.getPlayerInCurrentHand(2)).thenReturn(p2);
        //round folds players when it's created
        round = new DiscardRound(context, adapterHolder, calculator, 1, true);

        players.put(1, p1);
        players.put(2, p2);
        when(context.getCurrentHandSeatingMap()).thenReturn(players);
        boolean res = round.act(new DiscardAction(p1.getId(),Arrays.asList(new Integer[]{7})));
        boolean res2 = round.act(new DiscardAction(p1.getId(),Arrays.asList(new Integer[]{7})));

        assertTrue(res);
        assertFalse(res2);

    }



    @Test
    public void testInvalidActions() {
        DefaultPokerPlayer p1 = new DefaultPokerPlayer(1);

        p1.setHasActed(false);
        p1.addPocketCard(new Card(7, "5C"), false);
        p1.addPocketCard(new Card(4, "6C"), false);
        p1.addPocketCard(new Card(9, "7C"), false);

        DefaultPokerPlayer p2 = new DefaultPokerPlayer(2);
        p2.setHasActed(false);
        p2.addPocketCard(new Card(1, "5D"), false);
        p2.addPocketCard(new Card(9, "6d"), false);
        p2.addPocketCard(new Card(12, "7D"), false);

        when(context.getPlayersInHand()).thenReturn(Arrays.asList(new PokerPlayer[]{p1,p2}));
        when(context.getPlayerInCurrentHand(1)).thenReturn(p1);
        when(context.getPlayerInCurrentHand(2)).thenReturn(p2);
        //round folds players when it's created
        round = new DiscardRound(context, adapterHolder, calculator, 1, true);
        players.put(1, p1);
        players.put(2, p2);

        when(context.getCurrentHandSeatingMap()).thenReturn(players);
        //non exsisting
        boolean res1 = round.act(new DiscardAction(p1.getId(),Arrays.asList(new Integer[]{99999})));
        //too few
        boolean res2 = round.act(new DiscardAction(p1.getId(),Arrays.asList(new Integer[]{})));

        //wrong action
        boolean res3 = round.act(new PokerAction(p1.getId(), PokerActionType.CHECK));
        //to many cards
        boolean res4 = round.act(new DiscardAction(p1.getId(),Arrays.asList(new Integer[]{7,4})));

        assertFalse(res1);
        assertFalse(res2);
        assertFalse(res3);
        assertFalse(res4);

        assertFalse(p1.hasActed());
        assertFalse(p2.hasActed());

    }
}
