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

package com.cubeia.poker.playerservice.impl;

import java.nio.ByteBuffer;
import java.util.Set;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.action.service.ClientServiceAction;
import com.cubeia.firebase.api.action.service.ServiceAction;
import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.RoutableService;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.cubeia.firebase.api.service.sysstate.PublicSystemStateService;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.io.protocol.Enums;
import com.cubeia.games.poker.routing.service.io.protocol.ProtocolObjectFactory;
import com.cubeia.games.poker.routing.service.io.protocol.TournamentIdRequest;
import com.cubeia.games.poker.routing.service.io.protocol.TournamentIdResponse;
import com.cubeia.poker.playerservice.api.PlayerService;

public class PlayerServiceImpl implements com.cubeia.firebase.api.service.Service, PlayerService, RoutableService {

    private static final String TOURNAMENT_ROOT = "/tournament";

    protected PublicSystemStateService systemState;

    private Logger log = Logger.getLogger(this.getClass());

    protected ServiceRouter router;

    public void init(ServiceContext con) throws SystemException {
    	systemState = con.getParentRegistry().getServiceInstance(PublicSystemStateService.class);
    }

    public void start() {
    }
    

    public void stop() {
    }

    public void destroy() {
    }

	@Override
	public void setRouter(ServiceRouter router) {
		this.router = router;
	}

	@Override
	public void onAction(ServiceAction e) {
        StyxSerializer serializer = new StyxSerializer(new ProtocolObjectFactory());
        ProtocolObject protocolRequestObject = serializer.unpack(ByteBuffer.wrap(e.getData()));
        ProtocolObject protocolResponseObject = null;

        if (protocolRequestObject instanceof TournamentIdRequest) {
        	TournamentIdRequest request = (TournamentIdRequest)protocolRequestObject;
        	log.debug("Tournament id lookup request: " + request);
        	int tournamentId = findTournamentId(request);
        	log.debug("Tournament lookup result, name["+request.name+"] -> id["+tournamentId+"]");
            protocolResponseObject = new TournamentIdResponse(tournamentId);
        } 

        if (protocolResponseObject != null) {
            byte[] responseData = serializer.pack(protocolResponseObject).array();
            ServiceAction action = new ClientServiceAction(e.getPlayerId(), -1, responseData);
            router.dispatchToPlayer(e.getPlayerId(), action);
        }
	}

	/**
	 * Find a tournament that matches name and has a valid status. The first matching tournament
	 * will be returned.
	 * 
	 * Name can be with or without spaces, e.g. 'Test Tournament' and 'TestTournament' are equal.
	 * 
	 * @param request
	 * @return
	 */
	protected int findTournamentId(TournamentIdRequest request) {
		return inspectFqn(TOURNAMENT_ROOT, request.name);
	}
	
	private int inspectFqn(String parent, String name) {
		int resultTournamentId = -1;
		Set<String> nodes = systemState.getChildren(parent);
		for (String node : nodes) {
			String fqn = parent+"/"+node;
			
			AttributeValue attribute = systemState.getAttribute(fqn, "_ID");
			if (attribute != null) {
				int tournamentId = attribute.getIntValue();
				String tournamentName = systemState.getAttribute(fqn, "NAME").getStringValue();
				String tournamentNameNoSpaces = tournamentName.replaceAll("\\s", "");
				// log.debug("Check name["+name+"] against ["+tournamentName+"] and ["+tournamentNameNoSpaces+"]");
				if (tournamentName.equalsIgnoreCase(name) || tournamentNameNoSpaces.equalsIgnoreCase(name)) {
					resultTournamentId = checkTournamentStatus(name,resultTournamentId, fqn, tournamentId);
				}
				
			} else {
				Set<String> children = systemState.getChildren(fqn);
				for (String childNode : children) {
					resultTournamentId = inspectFqn(fqn+"/"+childNode, name);
					if (resultTournamentId > 0) {
						return resultTournamentId;
					}
				}
			}
		}
		
		return resultTournamentId;
	}

	
	/**
	 * Check the status of the tournament. Valid statuses are found in the poker protocol. 
	 * 
	 * 	case 0: return TournamentStatus.ANNOUNCED;
     * 	case 1: return TournamentStatus.REGISTERING;
	 *  case 2: return TournamentStatus.RUNNING;
	 *  case 3: return TournamentStatus.ON_BREAK;
	 *  case 4: return TournamentStatus.PREPARING_BREAK;
	 *  case 5: return TournamentStatus.FINISHED;
	 *  case 6: return TournamentStatus.CANCELLED;
	 *  case 7: return TournamentStatus.CLOSED;
	 */
	private int checkTournamentStatus(String name, int resultTournamentId, String fqn, int tournamentId) {
		String status = systemState.getAttribute(fqn, "STATUS").getStringValue();
		if (status.equalsIgnoreCase(Enums.TournamentStatus.CLOSED.name()) || status.equalsIgnoreCase(Enums.TournamentStatus.CANCELLED.name())) {
			log.debug("I found a tournament named ["+name+"] but with not allowed status["+status+"]");
		} else {
			log.debug("I found a tournament named ["+name+"] with allowed status["+status+"]");
			resultTournamentId = tournamentId;	
		}
		return resultTournamentId;
	}

}