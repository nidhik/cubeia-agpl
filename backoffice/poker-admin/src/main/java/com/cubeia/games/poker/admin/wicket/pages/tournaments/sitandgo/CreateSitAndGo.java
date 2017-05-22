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

package com.cubeia.games.poker.admin.wicket.pages.tournaments.sitandgo;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.games.poker.admin.db.AdminDAO;
import com.cubeia.games.poker.admin.wicket.BasePage;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.configuration.TournamentConfigurationPanel;
import com.cubeia.games.poker.tournament.configuration.SitAndGoConfiguration;
import com.cubeia.games.poker.tournament.configuration.TournamentConfiguration;

@SuppressWarnings("serial")
@AuthorizeInstantiation({"ADMIN"})
public class CreateSitAndGo extends BasePage {

    private static final transient Logger log = LoggerFactory.getLogger(CreateSitAndGo.class);

    @SpringBean(name="adminDAO")
    private AdminDAO adminDAO;
    
    private SitAndGoConfiguration tournament = new SitAndGoConfiguration();

    @SuppressWarnings("rawtypes")
	public CreateSitAndGo(final PageParameters parameters) {
        super(parameters);
        resetFormData();
        
        Form tournamentForm = new Form("tournamentForm") {

            @Override
            protected void onSubmit() {
                tournament.getConfiguration().setMaxPlayers(tournament.getConfiguration().getMinPlayers());
                adminDAO.merge(tournament);
                log.debug("created tournament config with id = " + tournament);
                setResponsePage(ListSitAndGoTournaments.class);
            }
        };

        tournamentForm.add(new TournamentConfigurationPanel("configuration", tournamentForm,new PropertyModel<TournamentConfiguration>(tournament, "configuration"), true));

        add(tournamentForm);
        add(new FeedbackPanel("feedback"));
    }
    
    private void resetFormData() {
        tournament = new SitAndGoConfiguration();
    }

    @Override
    public String getPageTitle() {
        return "Create Sit & Go Tournament";
    }

}
