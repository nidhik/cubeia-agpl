/**
 * Copyright (C) 2012 BetConstruct
 */

package com.cubeia.games.poker.admin.wicket.pages.timings;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.cubeia.games.poker.admin.db.AdminDAO;
import com.cubeia.games.poker.admin.wicket.BasePage;
import com.cubeia.poker.timing.TimingProfile;

@AuthorizeInstantiation({"ADMIN"})
public class CreateTiming extends BasePage {

	private static final long serialVersionUID = 1L;

	@SpringBean(name="adminDAO")
    private AdminDAO adminDAO;

    private TimingProfile timing;

    public CreateTiming(final PageParameters parameters) {
        super(parameters);
        timing = new TimingProfile();
        Form<TimingProfile> timingForm = new Form<TimingProfile>("timingForm", new CompoundPropertyModel<TimingProfile>(timing)) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                TimingProfile object = getModel().getObject();
                adminDAO.persist(object);
                // info("Timing configuration created: " + object);
                setResponsePage(ListTimings.class);
            }
        };

        timingForm.add(new RequiredTextField<String>("name"));
        timingForm.add(new RequiredTextField<Long>("pocketCardsTime"));
        timingForm.add(new RequiredTextField<Long>("flopTime"));
        timingForm.add(new RequiredTextField<Long>("turnTime"));
        timingForm.add(new RequiredTextField<Long>("riverTime"));
        timingForm.add(new RequiredTextField<Long>("startNewHandTime"));
        timingForm.add(new RequiredTextField<Long>("actionTimeout"));
        timingForm.add(new RequiredTextField<Long>("autoPostBlindDelay"));
        timingForm.add(new RequiredTextField<Long>("latencyGracePeriod"));
        timingForm.add(new RequiredTextField<Long>("disconnectExtraTime"));
        timingForm.add(new RequiredTextField<Long>("additionalAllInRoundDelayPerPlayer"));

        add(timingForm);


        add(new FeedbackPanel("feedback"));

    }

    @Override
    public String getPageTitle() {
        return "Create Timing Configuration";
    }
}
