package com.cubeia.games.poker.tournament.activator;

import static com.cubeia.firebase.io.protocol.Enums.TournamentAttributes.NAME;
import static com.cubeia.games.poker.tournament.PokerTournamentLobbyAttributes.STATUS;
import static com.cubeia.games.poker.tournament.status.PokerTournamentStatus.ANNOUNCED;
import static com.cubeia.games.poker.tournament.status.PokerTournamentStatus.REGISTERING;
import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.mtt.lobby.MttLobbyObject;
import com.cubeia.games.poker.tournament.configuration.SitAndGoConfiguration;
import com.cubeia.games.poker.tournament.status.PokerTournamentStatus;
import com.google.inject.Singleton;

@Singleton
public class CreationAndCancellationCalculator {
    
    @SuppressWarnings("unused")
    private static final transient Logger log = Logger.getLogger(TournamentScanner.class);
    
    public static final List<PokerTournamentStatus> STATUS_PRE_RUNNING = asList(ANNOUNCED, REGISTERING);

    
    public static class SitAndGoResults {
        private Map<String, MttLobbyObject> tournamentsToCancel = new HashMap<String, MttLobbyObject>();
        private Set<String> missingTournaments = new LinkedHashSet<String>();
        
        public SitAndGoResults(Map<String, MttLobbyObject> tournamentsToCancel, Set<String> missingTournaments) {
            this.tournamentsToCancel = tournamentsToCancel;
            this.missingTournaments = missingTournaments;
        }

        public Map<String, MttLobbyObject> getTournamentsToCancel() {
            return tournamentsToCancel;
        }

        public Set<String> getTournamentsToCreate() {
            return missingTournaments;
        }
    }
    
    /**
     * Calculate which sit and go's should be cancelled and which existing that should be cancelled.
     * 
     * A new sit and go should be created if it has no instances and the configuration is not archived.
     * 
     * An existing tournament should be cancelled if it is hasn't been started and it's configuration
     * is archived.
     * 
     * @param configurations sit and go configurations, must include archived
     * @param tournamentInstances existing tournament instances
     * @param configurationTournamentNames names of existing tournaments
     * @return results
     */
    public SitAndGoResults calculateCreationAndCancellation(Map<String, SitAndGoConfiguration> configurations, 
        MttLobbyObject[] tournamentInstances) {
        
        Map<String, MttLobbyObject> tournamentsToCancel = new HashMap<String, MttLobbyObject>();
        
        Set<String> missingTournaments = new LinkedHashSet<String>();
        for (Map.Entry<String, SitAndGoConfiguration> entry : configurations.entrySet()) {
            if (!entry.getValue().getConfiguration().isArchived()) {
                missingTournaments.add(entry.getKey());
            }
        }
        
        for (MttLobbyObject mtt : tournamentInstances) {
            PokerTournamentStatus status = PokerTournamentStatus.valueOf(getStringAttribute(mtt, STATUS.name()));
            String name = getStringAttribute(mtt, NAME.name());
            SitAndGoConfiguration cfg = configurations.get(name);
            boolean isASitAndGoTournament = cfg != null;
    
            if (isASitAndGoTournament  &&  STATUS_PRE_RUNNING.contains(status)) {
                if (cfg.getConfiguration().isArchived()) {
                    tournamentsToCancel.put(name, mtt);
                } else {
                    missingTournaments.remove(name);
                }
            }
        }
        
        return new SitAndGoResults(tournamentsToCancel, missingTournaments);
    }
    
    private String getStringAttribute(MttLobbyObject tournament, String attributeName) {
        AttributeValue value = tournament.getAttributes().get(attributeName);

        if (value == null || value.getType() != AttributeValue.Type.STRING) {
            return "";
        }
        return value.getStringValue();
    }
    
}
