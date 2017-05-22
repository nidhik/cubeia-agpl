package com.cubeia.poker.playerservice.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cubeia.firebase.api.action.service.ClientServiceAction;
import com.cubeia.firebase.api.action.service.ServiceAction;
import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.cubeia.firebase.api.service.sysstate.PublicSystemStateService;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.io.protocol.Enums;
import com.cubeia.games.poker.routing.service.io.protocol.ProtocolObjectFactory;
import com.cubeia.games.poker.routing.service.io.protocol.TournamentIdRequest;
import com.cubeia.games.poker.routing.service.io.protocol.TournamentIdResponse;

public class PlayerServiceImplTest {

	@Mock PublicSystemStateService systemState;
	
	@Mock ServiceRouter router;
	
	PlayerServiceImpl service = new PlayerServiceImpl();
	
	StyxSerializer styx = new StyxSerializer(new ProtocolObjectFactory());
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service.systemState = systemState;
		service.router = router;
		
		Set<String> level_1 = new HashSet<String>(Arrays.asList(new String[]{"1"}));
		Set<String> level_2 = new HashSet<String>(Arrays.asList(new String[]{"scheduled", "sitandgo"}));
		Set<String> level_2_sitandgo = new HashSet<String>(Arrays.asList(new String[]{"10"}));
		Set<String> level_2_scheduled = new HashSet<String>(Arrays.asList(new String[]{"1", "7", "8", "11"}));
		
		String root = "/tournament";
		when(systemState.getChildren(root)).thenReturn(level_1);
		when(systemState.getChildren(root+"/1")).thenReturn(level_2);
		when(systemState.getChildren(root+"/1/scheduled")).thenReturn(level_2_scheduled);
		when(systemState.getChildren(root+"/1/sitandgo")).thenReturn(level_2_sitandgo);
		
		// when(systemState.getAttribute(Mockito.anyString(), Mockito.anyString())).thenReturn(AttributeValue.wrap(-1));
		
		when(systemState.getAttribute("/tournament/1/scheduled/1", "_ID")).thenReturn(AttributeValue.wrap(1));
		when(systemState.getAttribute("/tournament/1/scheduled/1", "NAME")).thenReturn(AttributeValue.wrap("Test Tournament"));
		when(systemState.getAttribute("/tournament/1/scheduled/1", "STATUS")).thenReturn(AttributeValue.wrap(Enums.TournamentStatus.REGISTERING.name()));
		
		when(systemState.getAttribute("/tournament/1/scheduled/8", "_ID")).thenReturn(AttributeValue.wrap(8));
		when(systemState.getAttribute("/tournament/1/scheduled/8", "NAME")).thenReturn(AttributeValue.wrap("Tournament Eight"));
		when(systemState.getAttribute("/tournament/1/scheduled/8", "STATUS")).thenReturn(AttributeValue.wrap(Enums.TournamentStatus.CANCELLED.name()));
		
		when(systemState.getAttribute("/tournament/1/scheduled/11", "_ID")).thenReturn(AttributeValue.wrap(11));
		when(systemState.getAttribute("/tournament/1/scheduled/11", "NAME")).thenReturn(AttributeValue.wrap("Tournament Eleven"));
		when(systemState.getAttribute("/tournament/1/scheduled/11", "STATUS")).thenReturn(AttributeValue.wrap(Enums.TournamentStatus.REGISTERING.name()));
		
		when(systemState.getAttribute("/tournament/1/sitandgo/10", "_ID")).thenReturn(AttributeValue.wrap(10));
		when(systemState.getAttribute("/tournament/1/sitandgo/10", "NAME")).thenReturn(AttributeValue.wrap("Heads Up PM 100"));
		when(systemState.getAttribute("/tournament/1/sitandgo/10", "STATUS")).thenReturn(AttributeValue.wrap(Enums.TournamentStatus.REGISTERING.name()));
	}
	
	@Test
	public void testFindTournament() {
		TournamentIdRequest request = new TournamentIdRequest("Test Tournament", new String[0]);
		int id = service.findTournamentId(request);
		assertThat(id, is(1));
	}
	
	@Test
	public void testFindTournament11() {
		TournamentIdRequest request = new TournamentIdRequest("Tournament Eleven", new String[0]);
		int id = service.findTournamentId(request);
		assertThat(id, is(11));
	}
	
	@Test
	public void testFindTournamentNoSpaces() {
		TournamentIdRequest request = new TournamentIdRequest("TestTournament", new String[0]);
		int id = service.findTournamentId(request);
		assertThat(id, is(1));
	}
	
	@Test
	public void testFindSitAndGoTournament() {
		TournamentIdRequest request = new TournamentIdRequest("headsuppm100", new String[0]);
		int id = service.findTournamentId(request);
		assertThat(id, is(10));
	}
	
	@Test
	public void testFindTournament11NoSpaceSmallChars() {
		TournamentIdRequest request = new TournamentIdRequest("tournamenteleven", new String[0]);
		int id = service.findTournamentId(request);
		assertThat(id, is(11));
	}
	
	@Test
	public void testFindCancelledTournament() {
		TournamentIdRequest request = new TournamentIdRequest("Tournament Eight", new String[0]);
		int id = service.findTournamentId(request);
		assertThat(id, is(-1));
	}
	
	@Test
	public void testOnAction() {
		TournamentIdRequest request = new TournamentIdRequest("Test Tournament", new String[0]);
		ByteBuffer bytes = styx.pack(request);
		ServiceAction action = new ClientServiceAction(1, 2, bytes.array());
		service.onAction(action);
		
		ArgumentCaptor<ServiceAction> argument = ArgumentCaptor.forClass(ServiceAction.class);
		Mockito.verify(router).dispatchToPlayer(Mockito.eq(1), argument.capture());
		ByteBuffer buffer = ByteBuffer.wrap(argument.getValue().getData());
		ProtocolObject protocol = styx.unpack(buffer);
		if (protocol instanceof TournamentIdResponse) {
			TournamentIdResponse response = (TournamentIdResponse) protocol;
			assertThat(response.id, is(1));
		}
		
	}
	
}
