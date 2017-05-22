package com.cubeia.game.poker.bot.ai.simple;

import static com.cubeia.game.poker.bot.ai.simple.SimpleAI.Strategy.STRONG;
import static com.cubeia.game.poker.bot.ai.simple.SimpleAI.Strategy.WEAK_BLUFF;
import static com.cubeia.games.poker.io.protocol.Enums.ActionType.BET;
import static com.cubeia.games.poker.io.protocol.Enums.ActionType.CALL;
import static com.cubeia.games.poker.io.protocol.Enums.ActionType.CHECK;
import static com.cubeia.games.poker.io.protocol.Enums.ActionType.FOLD;
import static com.cubeia.games.poker.io.protocol.Enums.ActionType.RAISE;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cubeia.firebase.bot.ai.AbstractAI;
import com.cubeia.game.poker.bot.ai.GameState;
import com.cubeia.game.poker.bot.ai.PokerAI;
import com.cubeia.games.poker.io.protocol.Enums.ActionType;
import com.cubeia.games.poker.io.protocol.PerformAction;
import com.cubeia.games.poker.io.protocol.PlayerAction;
import com.cubeia.games.poker.io.protocol.RequestAction;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandStrength;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.variant.texasholdem.TexasHoldemHandCalculator;

public class SimpleAI implements PokerAI {

    private AbstractAI bot;

    private static Random rng = new Random();

    private TexasHoldemHandCalculator handCalculator = new TexasHoldemHandCalculator();

    private StrengthCalculator strengthCalculator = new StrengthCalculator();

    /** Percent chance that the bot will bluff or act out of hand strength */
    private int bluffProbability = 5;
    
    /** How aggressive the bot will bet/raise on a scale of 1->X. */
    private int aggression = 1;

	private boolean bluffedPrevious = false;

    enum Strategy {
        WEAK,
        WEAK_BLUFF,
        NEUTRAL,
        STRONG
    }

    public SimpleAI() {
        bluffProbability = bluffProbability + rng.nextInt(15);
        //aggression = aggression + rng.nextInt(10);
        aggression = aggression + NonLinearRng.nextInt(15);
    }

    @Override
    public void setBot(AbstractAI bot) {
        this.bot = bot;
    }

    @Override
    public PerformAction onActionRequest(RequestAction request, GameState state) {
        HandStrength handStrength = getHandStrength(state);
        HandStrength communityHandStrength = getCommunityHandStrength(state);

        PerformAction response = new PerformAction();
        response.seq = request.seq;
        response.player = bot.getBot().getPid();

        PlayerAction playerAction = null;

        // Always post blinds
        for (PlayerAction action : request.allowedActions) {
            switch (action.type) {
                case BIG_BLIND:
                case SMALL_BLIND:
                case ANTE:
                case ENTRY_BET:
                    playerAction = action;
                    bluffedPrevious = false;
                    break;
                default:
            }
        }

        if (playerAction != null) {
            // Blind or Ante
            response.action = playerAction;
            return response;
        }

        // We need to act
        Strategy strategy = strengthCalculator.getStrategy(request, state, handStrength, communityHandStrength);

        boolean bluff = false;
        if (doBluff()) {
            bluff = true;
            switch (strategy) {
                case WEAK:
                    strategy = STRONG;
                    break;
                case NEUTRAL:
                    strategy = STRONG;
                    break;
                case STRONG:
                    strategy = WEAK_BLUFF;
                    break;
			default:
				break;
            }
        }

        // Keep track of bluffing. If we bluffed once we want to increase
        // the chance of bluffing again.
        bluffedPrevious = bluff;
        
        int amountModifier = getRelativeCallSizeModifier(request, state);
        BigDecimal potSizeModifier = getRelativePotSizeModifier(request, state);
        int betModifier = potSizeModifier.intValue()+1;
        //bot.getBot().logInfo("Relative Call Amount Modifier: "+amountModifier);
        //bot.getBot().logInfo("Bet Amount Modifier: "+betModifier);
        
        BigDecimal betAmount = BigDecimal.ZERO;

        if (strategy == Strategy.WEAK) {
            if (hasPlayerAction(CHECK, request)) {
                playerAction = new PlayerAction(CHECK, "0", "0");
            } else {
                playerAction = new PlayerAction(FOLD, "0", "0");
            }
        }

        if (strategy == Strategy.WEAK_BLUFF) {
            if (hasPlayerAction(CHECK, request)) {
                playerAction = new PlayerAction(CHECK, "0", "0");

            } else if (hasPlayerAction(CALL, request)) {
                playerAction = getPlayerAction(CALL, request);

            } else {
                playerAction = getPlayerAction(FOLD, request);

            }
        }
        
        if (strategy == Strategy.NEUTRAL) {
            if (prob(70-amountModifier*2+aggression/4) && hasPlayerAction(CALL, request)) {
                playerAction = getPlayerAction(CALL, request);
                betAmount = new BigDecimal(playerAction.minAmount);

            } else if (prob(75-betModifier*2+aggression/4) && hasPlayerAction(BET, request)) {
                playerAction = getPlayerAction(BET, request);
                betAmount = calculateBet(playerAction, request, strategy).multiply(new BigDecimal(betModifier));

            } else if (hasPlayerAction(CHECK, request)) {
                playerAction = getPlayerAction(CHECK, request);

            } else {
                playerAction = getPlayerAction(FOLD, request);

            }
        }

        if (strategy == Strategy.STRONG) {
            if (prob(50-amountModifier+aggression/3) && hasPlayerAction(RAISE, request)) {
                playerAction = getPlayerAction(RAISE, request);
                betAmount = calculateBet(playerAction, request, strategy);

            } else if (prob(80+aggression/3) && hasPlayerAction(BET, request)) {
                playerAction = getPlayerAction(BET, request);
                betAmount = calculateBet(playerAction, request, strategy).multiply(new BigDecimal(betModifier));

            } else if (prob(94-amountModifier) && hasPlayerAction(CALL, request) && !bluff) { 
                playerAction = getPlayerAction(CALL, request);
                betAmount = new BigDecimal(playerAction.minAmount);

            } else if (hasPlayerAction(CHECK, request)) {
                playerAction = getPlayerAction(CHECK, request);

            } else {
                playerAction = getPlayerAction(FOLD, request);

            }
        }

        if (playerAction == null) {
            bot.getBot().logWarn("Player Action was not set! Allowed Actions: " + request.allowedActions);
            playerAction = request.allowedActions.get(0);
            bot.getBot().logInfo("Simple AI. Hand Strength: " + handStrength + ", State: " + state + ", PA: " + playerAction);
        }


        response.action = playerAction;
        response.betAmount = betAmount.toPlainString();

        HandType handType = handStrength.getHandType();
        Rank highestRank = handStrength.getHighestRank();
        ActionType playerActionType = playerAction.type;

        if (!bluff) {
            bot.getBot().logInfo("Simple AI. I got " + handType + " " + highestRank + " on the " + state
                    .getPhase() + ". I am feeling " + strategy + ". I will " + playerActionType + ", with bet amout " + betAmount);
        } else {
            bot.getBot().logInfo("Simple AI. I got " + handType + " " + highestRank + " on the " + state
                    .getPhase() + ". I am bluffing as " + strategy + ". I will " + playerActionType + ", with bet amout " + betAmount);
        }

        return response;
    }

	private int getRelativeCallSize(RequestAction request, GameState state) {
        if (hasPlayerAction(CALL, request)) {
        	PlayerAction action = getPlayerAction(CALL, request);
        	BigDecimal callAmount = new BigDecimal(action.minAmount);
        	BigDecimal bigBlind = state.getBigBlind();
        	if (bigBlind != null && bigBlind.intValue() > 0) {
            	return callAmount.divide(bigBlind, new MathContext(2,RoundingMode.HALF_DOWN)).intValue();
            }
        }
		return 1;
	}

	/**
	 * Suppress aggression depending on pot size. I.e. don't keep on raising with high stakes. 
	 * 
	 * @param request
	 * @param state
	 * @return
	 */
	private int getRelativeCallSizeModifier(RequestAction request, GameState state) {
		int relativeCallSize = getRelativeCallSize(request, state);
		if (relativeCallSize <= 2) {
			return 0;
		} else if (relativeCallSize > 100) {
			return 50;
		} else {
			return relativeCallSize/2;
		} 
	}
	
	private BigDecimal getRelativePotSizeModifier(RequestAction request, GameState state) {
		BigDecimal pot = new BigDecimal(request.currentPotSize);
		BigDecimal bigBlind = state.getBigBlind();
		if (bigBlind != null && bigBlind.intValue() > 0) {
			return pot.divide(bigBlind,2,RoundingMode.DOWN).divide(new BigDecimal(10),2,RoundingMode.DOWN);
		} 
		return new BigDecimal(0);
	}

    /**
     * Probability to do something. The return value of true is considered an aggressive move so high aggression value
     * will increase the probability to act. This means that the probability to act will almost always be higher than
     * the supplied probability since aggression is an added factor. 
     * 
     * Real Probability In Percent = probability + aggression.
     * 
     * 
     * @param probability
     * @return
     */
    private boolean prob(int probability) {
		return rng.nextInt(100) <  probability + aggression;
	}

	private BigDecimal calculateBet(PlayerAction playerAction, RequestAction request, Strategy strategy) {
        BigDecimal minAmount = new BigDecimal(playerAction.minAmount);
        BigDecimal maxAmount = new BigDecimal(playerAction.maxAmount);
        BigDecimal pot = new BigDecimal(request.currentPotSize);
        
        BigDecimal betAmount;
        
        if (strategy == STRONG) {
            betAmount = calculateStrongBetAmount(pot, minAmount);
        } else {
            betAmount = calculateNeutralBetAmount(minAmount, pot);
        }

        // Adjust if outside boundaries.
        if (betAmount.compareTo(minAmount) < 0) {
            betAmount = minAmount;
        } else if (betAmount.compareTo(maxAmount) > 0) {
            betAmount = maxAmount;
        }

        return betAmount;
    }

	private BigDecimal calculateStrongBetAmount(BigDecimal pot, BigDecimal minAmount) {
		BigDecimal betAmount;
		
		// Check if all-in/betting pot level. % chance of all in/pot bet is same as aggression level
		if (rng.nextInt(100) < aggression) {
			int potMultiplier = NonLinearRng.nextInt(aggression/4 + 1);
			betAmount = pot.multiply(new BigDecimal(potMultiplier));
			//bot.getBot().logInfo("Betting STRONG - ALL IN/POT LEVEL. Multiplier["+potMultiplier+"] Pot["+pot+"]");
			
		} else {
			int multiplier = NonLinearRng.nextInt(aggression/2 + 1);
			betAmount = minAmount.multiply(new BigDecimal(multiplier));
			//bot.getBot().logInfo("Betting STRONG - USING MIN BET LEVEL. Multiplier["+multiplier+"] minAmount["+minAmount+"]");
		}
		
		
		
		bot.getBot().logInfo("Betting STRONG. Aggression["+aggression+"] pot["+pot+"] betAmount["+betAmount+"]");
		return betAmount;
	}

	private BigDecimal calculateNeutralBetAmount(BigDecimal minAmount, BigDecimal pot) {
		BigDecimal betAmount;
		int minBetMultiplier = NonLinearRng.nextInt(aggression/3+1);
		betAmount = minAmount.multiply(new BigDecimal(minBetMultiplier));
		if (betAmount.compareTo(pot) > 1) {
			betAmount = minAmount;
			//bot.getBot().logInfo("Betting NEUTRAL. Aggression["+aggression+"] Capping betAmount["+betAmount+"] to minBet["+minAmount+"]");
		}
		//bot.getBot().logInfo("Betting NEUTRAL Aggression["+aggression+"]. minBetMultiplier["+minBetMultiplier+"] minBet["+minAmount+"] betAmount["+betAmount+"]");
		return betAmount;
	}

    private boolean doBluff() {
    	int probability = bluffProbability;
    	if (bluffedPrevious) {
    		probability += 15;
    	}
        return rng.nextInt(100) < probability;
    }

    private PlayerAction getPlayerAction(ActionType type, RequestAction request) {
        for (PlayerAction action : request.allowedActions) {
            if (action.type == type) {
                return action;
            }
        }
        return null;
    }

    private boolean hasPlayerAction(ActionType type, RequestAction request) {
        return getPlayerAction(type, request) != null;
    }

    public HandStrength getHandStrength(GameState state) {
        List<Card> cards = new ArrayList<Card>(state.getPrivateCards());
        cards.addAll(state.getCommunityCards());
        Hand hand = new Hand(cards);
        return handCalculator.getHandStrength(hand);
    }
    
    public HandStrength getCommunityHandStrength(GameState state) {
        List<Card> cards = new ArrayList<Card>(state.getCommunityCards());
        Hand hand = new Hand(cards);
        return handCalculator.getHandStrength(hand);
    }
}
