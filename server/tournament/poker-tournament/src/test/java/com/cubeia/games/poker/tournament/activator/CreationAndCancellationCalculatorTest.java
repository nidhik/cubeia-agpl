package com.cubeia.games.poker.tournament.activator;

import static com.cubeia.games.poker.tournament.activator.CreationAndCancellationCalculator.STATUS_PRE_RUNNING;
import static com.cubeia.games.poker.tournament.status.PokerTournamentStatus.ANNOUNCED;
import static com.cubeia.games.poker.tournament.status.PokerTournamentStatus.REGISTERING;
import static com.cubeia.games.poker.tournament.status.PokerTournamentStatus.RUNNING;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.mtt.lobby.MttLobbyObject;
import com.cubeia.firebase.io.protocol.Enums.TournamentAttributes;
import com.cubeia.games.poker.tournament.activator.CreationAndCancellationCalculator.SitAndGoResults;
import com.cubeia.games.poker.tournament.configuration.SitAndGoConfiguration;
import com.cubeia.games.poker.tournament.status.PokerTournamentStatus;

public class CreationAndCancellationCalculatorTest {

    private CreationAndCancellationCalculator calc;

    @Before
    public void setup() {
        calc = new CreationAndCancellationCalculator();
    }
    
    @Test
    public void testPreRunningStatuses() {
        assertThat(STATUS_PRE_RUNNING, is(Arrays.asList(ANNOUNCED, REGISTERING)));
    }
    
    @Test
    public void testCalculateShouldCreateMissing() {
        Map<String, SitAndGoConfiguration> configurations = createConfigs();
        MttLobbyObject[] tournamentInstances = new MttLobbyObject[] {};
        
        SitAndGoResults result = calc.calculateCreationAndCancellation(configurations, tournamentInstances);
        
        assertThat(result.getTournamentsToCreate().size(), is(1));
        assertThat(result.getTournamentsToCreate(), hasItem("cfg2"));
        
        assertThat(result.getTournamentsToCancel().isEmpty(), is(true));
    }

    @Test
    public void testCalculateCancelArchived() {
        Map<String, SitAndGoConfiguration> configurations = createConfigs();
        MttLobbyObject mtt1 = mock(MttLobbyObject.class);
        MttLobbyObject mtt2 = mock(MttLobbyObject.class);
        Mockito.when(mtt1.getAttributes()).thenReturn(createAttribs("cfg1", REGISTERING));
        Mockito.when(mtt2.getAttributes()).thenReturn(createAttribs("cfg2", REGISTERING));
        MttLobbyObject[] tournamentInstances = new MttLobbyObject[] { mtt1, mtt2 };
        
        SitAndGoResults result = calc.calculateCreationAndCancellation(configurations, tournamentInstances);
        
        assertThat(result.getTournamentsToCreate().isEmpty(), is(true));
        
        assertThat(result.getTournamentsToCancel().size(), is(1));
        assertThat(result.getTournamentsToCancel().containsKey("cfg1"), is(true));
        assertThat(result.getTournamentsToCancel().get("cfg1"), is(mtt1));
    }
    
    @Test
    public void testCalculateShouldCreateAndCancel() {
        Map<String, SitAndGoConfiguration> configurations = createConfigs();
        MttLobbyObject mtt1 = mock(MttLobbyObject.class);
        Mockito.when(mtt1.getAttributes()).thenReturn(createAttribs("cfg1", REGISTERING));
        MttLobbyObject[] tournamentInstances = new MttLobbyObject[] { mtt1 };
        
        SitAndGoResults result = calc.calculateCreationAndCancellation(configurations, tournamentInstances);
        
        assertThat(result.getTournamentsToCreate().size(), is(1));
        assertThat(result.getTournamentsToCreate(), hasItem("cfg2"));
        
        assertThat(result.getTournamentsToCancel().size(), is(1));
        assertThat(result.getTournamentsToCancel().containsKey("cfg1"), is(true));
        assertThat(result.getTournamentsToCancel().get("cfg1"), is(mtt1));
    }
    
    @Test
    public void testCalculateShouldNotCancelIfRunning() {
        Map<String, SitAndGoConfiguration> configurations = createConfigs();
        MttLobbyObject mtt1 = mock(MttLobbyObject.class);
        Mockito.when(mtt1.getAttributes()).thenReturn(createAttribs("cfg1", RUNNING));
        MttLobbyObject[] tournamentInstances = new MttLobbyObject[] { mtt1 };
        
        SitAndGoResults result = calc.calculateCreationAndCancellation(configurations, tournamentInstances);
        
        assertThat(result.getTournamentsToCancel().isEmpty(), is(true));
    }    
    
    @Test
    public void testCalculateShouldNotCreateIfCreated() {
        Map<String, SitAndGoConfiguration> configurations = createConfigs();
        MttLobbyObject mtt2 = mock(MttLobbyObject.class);
        Mockito.when(mtt2.getAttributes()).thenReturn(createAttribs("cfg2", REGISTERING));
        MttLobbyObject[] tournamentInstances = new MttLobbyObject[] { mtt2 };
        
        SitAndGoResults result = calc.calculateCreationAndCancellation(configurations, tournamentInstances);
        
        assertThat(result.getTournamentsToCreate().isEmpty(), is(true));
    }    
    
    @Test
    public void testCalculateShouldBeCreatedIfRunning() {
        Map<String, SitAndGoConfiguration> configurations = createConfigs();
        MttLobbyObject mtt2 = mock(MttLobbyObject.class);
        Mockito.when(mtt2.getAttributes()).thenReturn(createAttribs("cfg2", RUNNING));
        MttLobbyObject[] tournamentInstances = new MttLobbyObject[] { mtt2 };
        
        SitAndGoResults result = calc.calculateCreationAndCancellation(configurations, tournamentInstances);
        
        assertThat(result.getTournamentsToCreate().size(), is(1));
        assertThat(result.getTournamentsToCreate(), hasItem("cfg2"));
    }    
    
    private Map<String, AttributeValue> createAttribs(String name, PokerTournamentStatus status) {
        Map<String, AttributeValue> mttAttribs = new HashMap<>();
        mttAttribs.put(TournamentAttributes.NAME.name(), new AttributeValue(name));
        mttAttribs.put(TournamentAttributes.STATUS.name(), new AttributeValue(status.name()));
        return mttAttribs;
    }
    
    private Map<String, SitAndGoConfiguration> createConfigs() {
        Map<String, SitAndGoConfiguration> configurations = new HashMap<String, SitAndGoConfiguration>();
        SitAndGoConfiguration cfg1 = new SitAndGoConfiguration();
        cfg1.getConfiguration().setName("cfg1");
        cfg1.getConfiguration().setArchived(true);
        configurations.put("cfg1", cfg1);
        
        SitAndGoConfiguration cfg2 = new SitAndGoConfiguration();
        cfg2.getConfiguration().setName("cfg2");
        cfg2.getConfiguration().setArchived(false);
        configurations.put("cfg2", cfg2);
        
        return configurations;
    }

}
