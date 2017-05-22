package com.cubeia.games.poker.admin.wicket.components;

import com.cubeia.games.poker.admin.wicket.HistoricHandSearchEntity;
import com.cubeia.games.poker.admin.wicket.pages.history.ShowHand;
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
public class HistoricHandSearchPanel extends SearchResultPanel<HistoricHandSearchEntity> {

    public HistoricHandSearchPanel(String id, IModel<HistoricHandSearchEntity> model) {
        super(id, new CompoundPropertyModel<>(model.getObject()));
        HistoricHandSearchEntity hand = model.getObject();
        Label idLabel = new Label("idValue", hand.get_id());
        BookmarkablePageLink pl = new BookmarkablePageLink<>("historicHandLink", ShowHand.class,
                new PageParameters().add("handId", hand.get_id()));
        pl.add(idLabel);
        SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        add(new Label("startTime", df.format(hand.getStartTime())));
        add(new Label("endTime", df.format(hand.getEndTime())));
        add(new ListView<Player>("playerList",hand.getSeats()){
            @Override
            protected void populateItem(ListItem<Player> item) {
               item.add(new Label("playerName",item.getModelObject().getName()));
               item.add(new Label("playerId",item.getModelObject().getPlayerId()));
            }
        });
        add(pl);
    }

}
