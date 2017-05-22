package com.cubeia.games.poker.admin.wicket.components;

import com.cubeia.games.poker.admin.wicket.HistoricHandSearchEntity;
import com.cubeia.games.poker.admin.wicket.HistoricTournamentSearchEntity;
import com.cubeia.games.poker.admin.wicket.pages.history.ShowHand;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.history.ShowTournament;
import com.cubeia.network.shared.web.wicket.search.SearchResultPanel;
import com.cubeia.poker.handhistory.api.Player;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.text.SimpleDateFormat;


@SuppressWarnings("serial")
public class HistoricTournamentSearchPanel extends SearchResultPanel<HistoricTournamentSearchEntity> {

    public HistoricTournamentSearchPanel(String id, IModel<HistoricTournamentSearchEntity> model) {
        super(id, new CompoundPropertyModel<>(model.getObject()));
        HistoricTournamentSearchEntity tournament = model.getObject();
        Label idLabel = new Label("idValue", tournament.get_id());
        BookmarkablePageLink pl = new BookmarkablePageLink<>("historicTournamentLink", ShowTournament.class,
                new PageParameters().add("historicTournamentId", tournament.get_id()));
        pl.add(idLabel);
        add(new Label("tournamentName", tournament.getTournamentName()));
        SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        add(new Label("startTime", df.format(tournament.getStartTime())));

        add(pl);
    }

}
