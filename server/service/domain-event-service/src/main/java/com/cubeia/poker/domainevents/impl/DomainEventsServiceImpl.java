package com.cubeia.poker.domainevents.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Map;

import com.cubeia.network.users.firebase.api.UserServiceContract;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;
import com.cubeia.backend.firebase.CashGamesBackendService;
import com.cubeia.events.client.EventClient;
import com.cubeia.events.client.EventListener;
import com.cubeia.events.event.GameEvent;
import com.cubeia.events.event.GameEventType;
import com.cubeia.events.event.SystemEvent;
import com.cubeia.events.event.achievement.BonusEvent;
import com.cubeia.events.event.poker.PokerAttributes;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.action.service.ClientServiceAction;
import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.model.MttPlayer;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.clientregistry.PublicClientRegistryService;
import com.cubeia.firebase.api.service.router.RouterService;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.common.money.Currency;
import com.cubeia.games.poker.common.money.Money;
import com.cubeia.games.poker.io.protocol.AchievementNotificationPacket;
import com.cubeia.games.poker.routing.service.io.protocol.PokerProtocolMessage;
import com.cubeia.poker.domainevents.api.BonusEventWrapper;
import com.cubeia.poker.domainevents.api.DomainEventsService;
import com.google.inject.Singleton;

@Singleton
public class DomainEventsServiceImpl implements Service, DomainEventsService, EventListener {
	
	Logger log = Logger.getLogger(getClass());
	
	EventClient client;
	
	@com.cubeia.firebase.guice.inject.Service
	RouterService router;
	
	@com.cubeia.firebase.guice.inject.Service
	PublicClientRegistryService clientRegistry;
	
	@com.cubeia.firebase.guice.inject.Service
    CashGamesBackendService cashGameBackend;

    @com.cubeia.firebase.guice.inject.Service
    UserServiceContract userService;
	
	ObjectMapper mapper = new ObjectMapper();
	
	/** We only need writing to so injected factory is needed */
	private StyxSerializer serializer = new StyxSerializer(null);
    private static final String LEVEL_ATTRIBUTE = "level";

    public void init(ServiceContext con) throws SystemException {
    }

	public void start() {
		client = new EventClient();
		client.setEventListener(this);
	}

    public void stop() {
    }
	
    public void destroy() {
    }

	@Override
	public void sendEvent(GameEvent event) {
		log.debug("DomainEvents Send GameEvent: "+event);
		client.send(event);
	}
	
	@Override
	public void sendEvent(SystemEvent event) {
		log.debug("DomainEvents Send SystemEvent: "+event);
		client.send(event);
	}

    @Override
    public void onEvent(GameEvent event) {
    }

	/**
	 * A bonus event has been triggered by the achievement service
	 */
	@Override
	public void onEvent(BonusEvent event) {
		log.debug("On Bonus Event ("+event.hashCode()+"): "+event);
		try {
			int playerId = Integer.parseInt(event.player);
			String json = mapper.writeValueAsString(event);
			BonusEventWrapper wrapper = new BonusEventWrapper(playerId, json);
			wrapper.broadcast = event.broadcast;
			
			if (event.broadcast) {
				log.debug("Send bonus event through table: "+event);
				Map<Integer, Integer> seatedTables = clientRegistry.getSeatedTables(playerId);
				for (int tableId : seatedTables.keySet()) {
					GameObjectAction action = new GameObjectAction(tableId);
					action.setAttachment(wrapper);
					router.getRouter().dispatchToGame(tableId, action );
				}
			} else {
				sendToPlayer(wrapper);
			}

            if("xp".equals(event.type) && "levelUp".equals(event.subType)) {
                updatePlayerLevel(event, playerId);
            }
			
		} catch (Exception e) {
			log.error("Failed to handle bonus event["+event+"]", e);
		}
	}

    private void updatePlayerLevel(BonusEvent event, int playerId) {
        try {
            String level = event.attributes.get(LEVEL_ATTRIBUTE);
            if(level!=null) {
                userService.updateUserAttribute(playerId, LEVEL_ATTRIBUTE, level);
            } else {
                log.debug("level is not set in bonus event");
            }
        } catch (Exception e) {
            log.info("Unable to update player level for player " + playerId);
            log.debug("Level exception",e);
        }

    }


    private void sendToPlayer(BonusEventWrapper wrapper) throws IOException {
		int playerId = wrapper.playerId;
		
		AchievementNotificationPacket notification = new AchievementNotificationPacket();
		notification.playerId = playerId;
		notification.message = wrapper.event;
		
		ByteBuffer notificationData = serializer.pack(notification);
		PokerProtocolMessage msg = new PokerProtocolMessage(notificationData.array());
		ByteBuffer msgData = serializer.pack(msg);
		
		ClientServiceAction action = new ClientServiceAction(playerId, 0, msgData.array());
		log.debug("Send bonus event as client action: "+action);
		router.getRouter().dispatchToPlayer(playerId, action);

	}


	@Override
	public void sendTournamentPayoutEvent(MttPlayer player, BigDecimal buyIn, BigDecimal fee, BigDecimal payout, String currencyCode, int position, MttInstance instance, boolean paidOutAsBonus) {
		try {
			int tournamentId = instance.getState().getId();
			String tournamentName = instance.getState().getName();
			int registeredPlayersCount = instance.getState().getRegisteredPlayersCount();


			int playerId = player.getPlayerId();
			Integer operatorId = clientRegistry.getOperatorId(playerId);
			
			log.debug("Tournament payout event. Operator id: "+operatorId);
			
			if (operatorId == null) {
				log.error("Failed to send domain event for tournament payout. Client registry returned null as the OperatorId. Player["+player.getPlayerId()+":"+player.getScreenname()+"] MTT["+instance.getId()+":"+instance.getState().getName()+"]");
				return; 
			}
			
			// We don't want to push events for operator id 0 which is reserved for bots and internal users.
			// You can disable this by setting an system property events.bots to anything, e.g. -Devents.bots = true.
			if (operatorId == 0 && System.getProperty("events.bots") == null) {
				return; 
			}
			
			Money accountBalance = new Money(new BigDecimal(-1), new Currency(currencyCode, 2));
			try {
				log.debug("sendTournamentPayoutEvent - Cash game backend: "+cashGameBackend);
				accountBalance = cashGameBackend.getAccountBalance(playerId, currencyCode);
			} catch (GetBalanceFailedException e) {
				log.error("Failed to get balance for player["+playerId+"] and currency["+currencyCode+"]", e);
			}
			
			GameEvent event = new GameEvent();
			event.game = PokerAttributes.poker.name();
			event.player = playerId+"";
			event.type = GameEventType.tournamentPayout.name();
			event.operator = operatorId+"";
			
			event.attributes.put(PokerAttributes.stake.name(), buyIn +"");
            event.attributes.put(PokerAttributes.rake.name(),fee+"");
			event.attributes.put(PokerAttributes.winAmount.name(), payout +"");
			event.attributes.put(PokerAttributes.netResult.name(), payout.subtract(buyIn) +"");
			event.attributes.put(PokerAttributes.tournamentId.name(), tournamentId+"");
			event.attributes.put(PokerAttributes.tournamentName.name(), tournamentName);
			event.attributes.put(PokerAttributes.tournamentPosition.name(), position+"");
			
			event.attributes.put(PokerAttributes.accountBalance.name(), accountBalance.getAmount().add(payout)+"");
			event.attributes.put(PokerAttributes.accountCurrency.name(), currencyCode);  
			event.attributes.put(PokerAttributes.tournamentPlayerCount.name(), registeredPlayersCount+"");
			
			event.attributes.put(PokerAttributes.screenname.name(), player.getScreenname());
            event.attributes.put(PokerAttributes.paidOutAsBonus.name(),String.valueOf(paidOutAsBonus));
			
			log.debug("Send Player Session ended event: " + event);
			sendEvent(event);
		} catch (Exception e) {
			log.warn("Failed to send domain event for tournament payout. Player["+player.getPlayerId()+"] MTT["+instance.getId()+"]", e);
		}
	}
	
	public void sendEndPlayerSessionEvent(int playerId, String screenname, int operatorId, Money accountBalance) {
		try {
			log.debug("Event Player Session ended. Player["+playerId+"], Balance["+accountBalance+"]");
			
			// We don't want to push events for operator id 0 which is reserved for bots and internal users.
			// TODO: Perhaps make excluded operators configurable
			if (operatorId == 0 && System.getProperty("events.bots") == null) {
				return; 
			}
			
			GameEvent event = new GameEvent();
			event.game = PokerAttributes.poker.name();
			event.player = playerId+"";
			event.type = GameEventType.leaveTable.name();
			event.operator = operatorId+"";
			
			event.attributes.put(PokerAttributes.accountBalance.name(), accountBalance.getAmount()+"");
			event.attributes.put(PokerAttributes.accountCurrency.name(), accountBalance.getCurrencyCode());
			event.attributes.put(PokerAttributes.screenname.name(), screenname);
			
			log.debug("Send Player Session ended event: "+event);
			sendEvent(event);
		} catch (Exception e) {
			log.warn("Failed to send domain event for end player session event. Player["+playerId+":"+screenname+"] operatorId["+operatorId+"] accountBalance["+accountBalance+"]");
		}
	}
	
}
