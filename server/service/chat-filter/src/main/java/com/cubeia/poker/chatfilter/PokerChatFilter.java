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

package com.cubeia.poker.chatfilter;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.action.TableChatAction;
import com.cubeia.firebase.api.action.chat.ChannelChatAction;
import com.cubeia.firebase.api.action.service.ServiceAction;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.RoutableService;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.cubeia.firebase.api.service.chat.ChatFilter;

public class PokerChatFilter implements ChatFilter, Service, RoutableService {

    private static final Logger log = Logger.getLogger(PokerChatFilter.class);
    
    private static final Logger chatLog = Logger.getLogger("CHAT_LOG");

	@Override
	public void init(ServiceContext con) throws SystemException {}

	@Override
	public void destroy() {}

	@Override
	public void start() {
		log.info("Poker chat logger started. Will log to appender CHAT_LOG if defined on level DEBUG");
	}

	@Override
	public void stop() {}

	@Override
	public void setRouter(ServiceRouter router) {}

	@Override
	public void onAction(ServiceAction e) {}

	@Override
	public void channelCreated(int playerId, int channelId) {}

	@Override
	public ChannelChatAction filter(ChannelChatAction action) {
		return action;
	}

	@Override
	public TableChatAction filter(TableChatAction action) {
		chatLog.debug("playerId;"+action.getPlayerId()+";tableId,"+action.getTableId()+";message;\""+action.getMessage()+"\"");
		return action;
	}

	@Override
	public void channelDestroyed(int channelId) {}
   
}
