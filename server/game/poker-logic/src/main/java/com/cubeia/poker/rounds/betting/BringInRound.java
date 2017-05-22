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

package com.cubeia.poker.rounds.betting;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rounds.Round;
import com.cubeia.poker.rounds.RoundVisitor;
import com.cubeia.poker.timing.Periods;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.math.BigDecimal;

public class BringInRound  extends BettingRound implements  Round, Serializable {

    private static final long serialVersionUID = -6452364533249060511L;

    private static transient Logger log = Logger.getLogger(BringInRound.class);

    private boolean bringInHandled = false;

    public BringInRound(PokerContext context,
                        ServerAdapterHolder serverAdapterHolder,
                        PlayerToActCalculator playerToActCalculator,
                        ActionRequestFactory actionRequestFactory,
                        FutureActionsCalculator futureActionsCalculator,
                        BetStrategy betStrategy) {

        super(context,serverAdapterHolder,playerToActCalculator,actionRequestFactory,futureActionsCalculator,betStrategy);
        requestFirstAction();

    }

    public BigDecimal getBringInAmount() {
        return getSettings().getBringInAmount();
    }

    private void requestBringInAction(PokerPlayer firstPlayerToAct) {
            playerToAct = firstPlayerToAct.getId();

            PossibleAction bringInAction = new PossibleAction(PokerActionType.BRING_IN, getBringInAmount());
            PossibleAction betAction = new PossibleAction(PokerActionType.BET, getBetStrategy().getMaxBetAmount(this, firstPlayerToAct));
            ActionRequest request = new ActionRequest();
            request.setPlayerId(firstPlayerToAct.getId());
            request.enable(bringInAction);
            request.enable(betAction);
            firstPlayerToAct.setActionRequest(request);
            if(firstPlayerToAct.isSittingOut() || firstPlayerToAct.isAway() || firstPlayerToAct.getBalance().compareTo(getBringInAmount())<=0) {
                act(new PokerAction(firstPlayerToAct.getId(),PokerActionType.BRING_IN,false));
            } else {
                getRoundHelper().requestAction(request);
            }


    }

    @Override
    protected void requestAction(PokerPlayer p) {
        if(bringInHandled == false) {
            requestBringInAction(p);
        } else {
            super.requestAction(p);
        }
    }
    @Override
    protected void performDefaultActionForPlayer(PokerPlayer player) {
        if(bringInHandled == false) {
            log.debug("Perform default action for player sitting out: " + player);
            act(new PokerAction(player.getId(), PokerActionType.BRING_IN, true));
        } else {
            super.performDefaultActionForPlayer(player);
        }
    }



    @Override
    public boolean act(PokerAction action) {
        if(bringInHandled == false) {
            return actBringIn(action);
        } else {
            return super.act(action);
        }
    }

    private boolean actBringIn(PokerAction action) {
        PokerPlayer player = getContext().getPlayerInCurrentHand(action.getPlayerId());
        if (player == null) {
            log.debug("Ignoring action from playerId " + action.getPlayerId() + " because player was null");
            return false;
        }
        if(!isValidAction(action, player)) {
            return false;
        }
        boolean handled;
        switch (action.getActionType()) {
            case BRING_IN:
                handled = bringIn(player);
                break;
            case BET:
                handled = super.bet(player, action.getBetAmount());
                break;
            default:
                log.debug(action.getActionType() + " is not legal here");
                return false;
        }
        if (handled) {
            bringInHandled = true;
            player.setHasActed(true);
            player.setCanRaise(false);
            super.nextAction(action, player);
        }
        return handled;
    }

    private boolean bringIn(PokerPlayer player) {

        BigDecimal bringIn = getBringInAmount().min(player.getBalance());
        player.addBet(bringIn);
        highBet = highBet.add(bringIn);
        sizeOfLastCompleteBetOrRaise = bringIn;
        highestCompleteBet = BigDecimal.ZERO;
        lastPlayerToPlaceBet = player;
        notifyPotSizeAndRakeInfo();

        return true;

    }



    @Override
    protected void requestFirstActionOrFinishRound() {
        //no op
    }
    private void requestFirstAction() {
        // Check if we should request actions at all
        PlayerToActCalculator playerToActCalculator = getPlayerToActCalculator();
        PokerPlayer p = playerToActCalculator.getFirstPlayerToAct(getContext().getCurrentHandSeatingMap(), getContext().getCommunityCards());
        long additionalTime = 0;
        if (p == null || super.allOtherNonFoldedPlayersAreAllIn(p)) {
            setRoundFinished(true);
            additionalTime = getContext().countNonFoldedPlayers() * getContext().getTimingProfile().getAdditionalAllInRoundDelayPerPlayer();
        } else {
            requestBringInAction(p);
        }

        if (isFinished()) {
            log.trace("scheduleRoundTimeout in: " + getContext().getTimingProfile().getTime(Periods.RIVER));
            getServerAdapter().scheduleTimeout(getContext().getTimingProfile().getTime(Periods.RIVER) + additionalTime);
        }
    }


    @Override
    public void visit(RoundVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getStateDescription() {
        return "playerToAct=" + playerToAct + " roundFinished=" + isFinished();
    }

    @Override
    public boolean flipCardsOnAllInShowdown() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setFlipCardsOnAllInShowdown(boolean flipCardsOnAllInShowdown) {
        super.setFlipCardsOnAllInShowdown(flipCardsOnAllInShowdown);
    }


}
