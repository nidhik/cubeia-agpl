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

package com.cubeia.games.poker.admin.wicket.pages.tournaments.scheduled;

import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cubeia.poker.PokerVariant;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.games.poker.admin.db.AdminDAO;
import com.cubeia.games.poker.admin.wicket.BasePage;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.configuration.TournamentConfigurationPanel;
import com.cubeia.games.poker.admin.wicket.pages.tournaments.rebuy.RebuyConfigurationPanel;
import com.cubeia.games.poker.admin.wicket.util.CronExpressionValidator;
import com.cubeia.games.poker.tournament.configuration.RebuyConfiguration;
import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.TournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.TournamentSchedule;

@SuppressWarnings("serial")
public class EditTournament extends BasePage {

    public static final String PARAM_TOURNAMENT_ID = "tournamentId";

    private static final Logger log = LoggerFactory.getLogger(EditTournament.class);

    @SpringBean(name="adminDAO")
    private AdminDAO adminDAO;
    
    private ScheduledTournamentConfiguration tournament;
    private RebuyConfigurationPanel rebuyConfigurationPanel;
    private final Model<Boolean> rebuysEnabled = Model.of(Boolean.FALSE);
    private TournamentConfigurationPanel configPanel;

	private Form<ScheduledTournamentConfiguration> tournamentForm;

	private AbstractDefaultAjaxBehavior pcAjaxBehaviour;
	
	private final boolean newTournament;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public EditTournament(final PageParameters parameters) {
        super(parameters);
        final Integer tournamentId = parameters.get(PARAM_TOURNAMENT_ID).toOptionalInteger();
        
        newTournament = tournamentId == null;
        
        if (newTournament) {
            tournament = new ScheduledTournamentConfiguration();
            tournament.setSchedule(new TournamentSchedule(new Date(), new Date(), "", 0, 0, 0));
        } else {
            tournament = adminDAO.getItem(ScheduledTournamentConfiguration.class, tournamentId);
        }
        
        tournamentForm = new Form<ScheduledTournamentConfiguration>("tournamentForm", new CompoundPropertyModel<ScheduledTournamentConfiguration>(tournament)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void onSubmit() {
            	log.debug("submit");
                ScheduledTournamentConfiguration configuration = getModel().getObject();
                TournamentConfiguration tournamentConfiguration = configuration.getConfiguration();
                PokerVariant variant = tournamentConfiguration.getVariant();
                if(variant == PokerVariant.SEVEN_CARD_STUD && tournamentConfiguration.getSeatsPerTable()>7) {
                    error("Maximum number of seats per table for 7 card stud is 7");
                    return;
                }
                ScheduledTournamentConfiguration savedTournamentConfig = adminDAO.merge(configuration);
                info("Tournament updated, id = " + tournamentId);
                
                if (newTournament) {
                    setResponsePage(new EditTournament(new PageParameters().add(PARAM_TOURNAMENT_ID, savedTournamentConfig.getId())));
                }
            }
        };

        configPanel = new TournamentConfigurationPanel("configuration", tournamentForm, new PropertyModel<TournamentConfiguration>(tournament, "configuration"), false);
        tournamentForm.add(configPanel);
        tournamentForm.add(new DateField("startDate", new PropertyModel(this, "tournament.schedule.startDate")));
        tournamentForm.add(new DateField("endDate", new PropertyModel(this, "tournament.schedule.endDate")));
        tournamentForm.add(new RequiredTextField("schedule", new PropertyModel(this, "tournament.schedule.cronSchedule")).add(new CronExpressionValidator()));
        tournamentForm.add(new TextField<Integer>("minutesInAnnounced", new PropertyModel(this, "tournament.schedule.minutesInAnnounced")));
        tournamentForm.add(new TextField<Integer>("minutesInRegistering", new PropertyModel(this, "tournament.schedule.minutesInRegistering")));
        tournamentForm.add(new TextField<Integer>("minutesVisibleAfterFinished", new PropertyModel(this, "tournament.schedule.minutesVisibleAfterFinished")));

        addRebuyPanel(tournamentForm);

        add(new FeedbackPanel("feedback"));
        add(tournamentForm);
    }
    
    @Override
    protected void onInitialize() {
    	super.onInitialize();
    	addPreviewSchedule(tournamentForm);
    }

    private void addPreviewSchedule(Form<ScheduledTournamentConfiguration> tournamentForm) {
    	WebMarkupContainer pc = new WebMarkupContainer("previewContainer");
    	pc.setOutputMarkupId(true);
    	
    	
		final SchedulePreviewPanel previewContent = new SchedulePreviewPanel("previewContent");
    	previewContent.setOutputMarkupId(true);
    	
    	TournamentSchedule schedule = tournament.getSchedule();
    	
    	if (schedule != null) {
    	    previewContent.setSchedule(schedule);
    	}
    	
    	pcAjaxBehaviour = new AbstractDefaultAjaxBehavior() {
			@Override
			protected void respond(AjaxRequestTarget target) {
				
				IRequestParameters params = getRequestCycle().getRequest().getRequestParameters();
				
				SimpleDateFormat sdf = new SimpleDateFormat(new DateTextField("dummy").getTextFormat());
				
				String cron = params.getParameterValue("cron").toOptionalString();

                int minInAnnounced = params.getParameterValue("announceMinutes").toInt(0);
                int minInRegistering = params.getParameterValue("registeringMinutes").toInt(0);
                int minAfterClose = params.getParameterValue("visibleMinutes").toInt(0);
				
				Date start = new Date(0);
				Date end = new Date(0);
				try {
					start = sdf.parse(params.getParameterValue("start").toString());
					end = sdf.parse(params.getParameterValue("end").toString());
				} catch (ParseException e) {
					log.warn("error parsing start/end date");
				}
				
				updatePreviewValues(previewContent, cron, start, end, minInAnnounced, minInRegistering, minAfterClose);
				
		        target.add(previewContent);
		    }

		};
		pc.add(pcAjaxBehaviour);
		pc.add(previewContent);
		
		tournamentForm.add(pc);
    }
    
    private void updatePreviewValues(final SchedulePreviewPanel previewContent, String cron, Date start, Date end, 
        int minAnnounced, int minRegistering, int minVisibleAfter) {
        
        TournamentSchedule sched = new TournamentSchedule(start, end, cron, minAnnounced, minRegistering, minVisibleAfter);
        previewContent.setSchedule(sched);
    }
    
    @Override
    public void renderHead(IHeaderResponse response) {
    	super.renderHead(response);
    	
    	CharSequence callbackFunction = pcAjaxBehaviour.getCallbackFunction(explicit("start"), explicit("end"), explicit("cron"),
    	    explicit("announceMinutes"), explicit("registeringMinutes"), explicit("visibleMinutes"));
		HeaderItem onDomReadyHeaderItem = JavaScriptHeaderItem.forScript("var previewTournamentSchedule = " +  callbackFunction.toString(), null);
    	response.render(onDomReadyHeaderItem);
    }
    
    
    private void addRebuyPanel(Form<ScheduledTournamentConfiguration> tournamentForm) {
        if (tournament.getConfiguration().getRebuyConfiguration() == null) {
            tournament.getConfiguration().setRebuyConfiguration(new RebuyConfiguration());
        }
        boolean enabled = tournament.getConfiguration().getRebuyConfiguration().getNumberOfRebuysAllowed() != 0;
        rebuysEnabled.setObject(enabled);
        CheckBox enableRebuys = new CheckBox("rebuysEnabled", rebuysEnabled);
        enableRebuys.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        log.debug("rr " + tournament.getConfiguration().getRebuyConfiguration().getNumberOfRebuysAllowed());
                        rebuyConfigurationPanel.setRebuysEnabled(!rebuyConfigurationPanel.isEnabled());
                        target.add(rebuyConfigurationPanel);
                    }
                });
        tournamentForm.add(enableRebuys);

        rebuyConfigurationPanel = new RebuyConfigurationPanel("rebuyConfiguration", tournament.getConfiguration().getRebuyConfiguration(), enabled);
        rebuyConfigurationPanel.setOutputMarkupId(true);
        tournamentForm.add(rebuyConfigurationPanel);
    }

    @Override
    public String getPageTitle() {
        return newTournament ? "Create Tournament" : "Edit Tournament";
    }
    
}