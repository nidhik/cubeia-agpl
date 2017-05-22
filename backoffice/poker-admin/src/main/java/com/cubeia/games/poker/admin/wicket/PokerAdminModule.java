package com.cubeia.games.poker.admin.wicket;

import static com.cubeia.network.shared.web.wicket.navigation.PageNodeUtils.add;
import static com.cubeia.network.shared.web.wicket.navigation.PageNodeUtils.node;
import static com.cubeia.network.shared.web.wicket.navigation.PageNodeUtils.nodeWithChildren;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.Model;
import org.springframework.stereotype.Component;

import com.cubeia.games.poker.admin.wicket.components.HistoricHandSearchPanel;
import com.cubeia.games.poker.admin.wicket.components.HistoricTournamentSearchPanel;
import com.cubeia.games.poker.admin.wicket.pages.history.HandHistory;
import com.cubeia.games.poker.admin.wicket.pages.history.ShowHand;
import com.cubeia.games.poker.admin.wicket.pages.rakes.CreateRake;
import com.cubeia.games.poker.admin.wicket.pages.rakes.EditRake;
import com.cubeia.games.poker.admin.wicket.pages.rakes.ListRakes;
import com.cubeia.games.poker.admin.wicket.pages.system.SystemManagement;
import com.cubeia.games.poker.admin.wicket.pages.system.VerifySystemAccounts;
import com.cubeia.games.poker.admin.wicket.pages.tables.CreateTable;
import com.cubeia.games.poker.admin.wicket.pages.tables.EditTable;
import com.cubeia.games.poker.admin.wicket.pages.tables.ListTables;
import com.cubeia.games.poker.admin.wicket.pages.timings.CreateTiming;
import com.cubeia.games.poker.admin.wicket.pages.timings.EditTiming;
import com.cubeia.games.poker.admin.wicket.pages.timings.ListTimings;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.blinds.CreateOrEditBlindsStructure;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.blinds.ListBlindsStructures;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.history.SearchTournamentHistory;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.payouts.CreateOrEditPayoutStructure;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.payouts.ListPayoutStructures;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.payouts.ViewPayoutStructure;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.scheduled.EditTournament;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.scheduled.ListTournaments;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.sitandgo.CreateSitAndGo;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.sitandgo.EditSitAndGo;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.sitandgo.ListSitAndGoTournaments;
import com.cubeia.network.shared.web.wicket.module.AdminWebModule;
import com.cubeia.network.shared.web.wicket.module.PanelCreator;
import com.cubeia.network.shared.web.wicket.navigation.PageNode;
import com.cubeia.network.shared.web.wicket.pages.search.SearchPage;
import com.cubeia.network.shared.web.wicket.search.SearchResultPanel;

@Component
public class PokerAdminModule extends AdminWebModule {

    private static final List<PageNode> pages = new ArrayList<>();

    static {

        add(pages, "Search","search", SearchPage.class, "icon-search");

        add(pages,"Hand History","hand-history", HandHistory.class, "icon-signal",
                node("Show Hand", ShowHand.class,false));

        add(pages,"Tournaments", "", ListTournaments.class, "icon-list-alt",
                nodeWithChildren("Scheduled Tournaments", "tournament", ListTournaments.class, "icon-calendar",
                        node("Create tournament", "create",EditTournament.class, false),
                        node("Edit Tournament", "edit",EditTournament.class, false)),
                nodeWithChildren("Sit & Go Tournaments", "sitandgo",ListSitAndGoTournaments.class, "icon-tags",
                        node("Create Sit & Go", "create",CreateSitAndGo.class, false),
                        node("Edit Sit & Go", "edit", EditSitAndGo.class, false)),
                nodeWithChildren("Blinds Structures", ListBlindsStructures.class, "icon-list-alt",
                        node("Create/Edit Blinds Structure", CreateOrEditBlindsStructure.class, false)),
                nodeWithChildren("Payout Structures", ListPayoutStructures.class, "icon-gift",
                        node("Create/Edit Payout Structure", CreateOrEditPayoutStructure.class, false),
                        node("View Payout Structure", ViewPayoutStructure.class, false)),
                node("Tournament History", SearchTournamentHistory.class, "icon-book")
        );

        add(pages,"Table Templates", ListTables.class, "icon-list-alt",
                node("Create Table Template", CreateTable.class),
                node("Edit Table Template", EditTable.class, false));
        add(pages, "Timing Configurations", ListTimings.class, "icon-list-alt",
                node("Create Timing Configuration", CreateTiming.class),
                node("Edit Timing Configuration", EditTiming.class, false));

        add(pages, "Rake Configurations", ListRakes.class, "icon-list-alt",
                node("Create Rake Configuration", CreateRake.class),
                node("Edit Rake Configuration", EditRake.class, false));

        add(pages,"System Management", "system", SystemManagement.class,"icon-list-alt",
        		node("Verify Accounts", "verify-accounts", VerifySystemAccounts.class));
    }

    public PokerAdminModule() {

        registerSearchType("historichand",HistoricHandSearchEntity.class, new PanelCreator<HistoricHandSearchEntity>() {
            @Override
            public SearchResultPanel createPanel(String wicketId, HistoricHandSearchEntity entity) {
                return new HistoricHandSearchPanel(wicketId, Model.of(entity));
            }
        });

        registerSearchType("historictournament",HistoricTournamentSearchEntity.class, new PanelCreator<HistoricTournamentSearchEntity>() {
            @Override
            public SearchResultPanel createPanel(String wicketId, HistoricTournamentSearchEntity entity) {
                return new HistoricTournamentSearchPanel(wicketId, Model.of(entity));
            }
        });
    }

    @Override
    public String getName() {
        return "operator";
    }

    @Override
    public List<PageNode> getPages() {
        return pages;
    }

}
