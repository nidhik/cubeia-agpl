package com.cubeia.games.poker.admin.wicket;

import com.cubeia.network.shared.web.wicket.search.SearchEntity;
import com.cubeia.poker.tournament.history.api.HistoricTournament;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoricTournamentSearchEntity extends HistoricTournament implements SearchEntity {

    private String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


}
