package com.cubeia.games.poker.admin.wicket.pages.tournaments.scheduled;

import static java.lang.System.currentTimeMillis;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.games.poker.tournament.configuration.TournamentSchedule;

@SuppressWarnings("serial")
public class SchedulePreviewPanel extends Panel {

    private static final Logger log = LoggerFactory.getLogger(SchedulePreviewPanel.class);

    private static final int MAX = 100;

    private IModel<TournamentSchedule> schedule = new Model<>();
//    private IModel<DateTime> nowModel = Model.of(new DateTime());
    private int index = 0;

    private List<DateTime> startTimes = new ArrayList<>();

    private boolean valid = false;

    private String error;

    public SchedulePreviewPanel(String id) {
        super(id);
//        add(new Label("now", nowModel));
        setOutputMarkupId(true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        
        TournamentSchedule sched = schedule.getObject();
        
        if (sched == null) {
            addOrReplace(new Fragment("instance", "noDataFragment", SchedulePreviewPanel.this));
        } else if (!valid) {
            Fragment frag = new Fragment("instance", "invalidCronFragment", SchedulePreviewPanel.this);
            frag.add(new Label("errorMsg", Model.of(error)));
            addOrReplace(frag);
        } else if (startTimes.isEmpty()) {
            addOrReplace(new Fragment("instance", "noInstancesFragment", SchedulePreviewPanel.this));
        } else {
            DateTime now = startTimes.get(index).minusMillis(1);
            
            Fragment instanceContainer = new Fragment("instance", "instanceFragment", SchedulePreviewPanel.this);
            addOrReplace(instanceContainer);
            
            instanceContainer.add(new Label("schedStart", sched.getStartDate()), new Label("schedEnd", sched.getEndDate()),
                new Label("schedCron", sched.getCronSchedule()), new Label("tz", creatTzString()));
            
            
            instanceContainer.add(new Label("instanceNumber", Model.of(index + 1)));
            instanceContainer.add(new Label("instanceCount", Model.of(startTimes.size() > MAX ? "" + MAX + "+" : startTimes.size())));
            
            instanceContainer.add(new Label("announce", sched.getNextAnnounceTime(now)));
            instanceContainer.add(new Label("register", sched.getNextRegisteringTime(now)));
            instanceContainer.add(new Label("start", sched.getNextStartTime(now)));
            
            instanceContainer.add(new NavLink("first", -MAX));
            instanceContainer.add(new NavLink("prev", -1));
            instanceContainer.add(new NavLink("next", 1));
            instanceContainer.add(new NavLink("last", MAX));
        }

    }

    private String creatTzString() {
        TimeZone tz = TimeZone.getDefault();
        double offset = ((double) tz.getOffset(currentTimeMillis())) / 3600 / 1000;
        try {
            return String.format("%s, %s (UTC %+.0g)", tz.getID(), tz.getDisplayName(true, TimeZone.SHORT), offset);
        } catch (Exception e) {
            log.error("error generating timezone string: tz = {}, offset = {}, display name = {}, tz id = {}", 
                new Object[] { tz, offset, tz.getDisplayName(true, TimeZone.SHORT), tz.getID() });
            return "N/A";
        }
    }
    
    private void moveIndex(int offset) {
        index = index + offset;
        if (index >= startTimes.size()) {
            index = startTimes.size() - 1;
        } else if (index < 0) {
            index = 0;
        }
    }

    public void setSchedule(TournamentSchedule sched) {
        index = 0;
        schedule.setObject(sched);
        
        try {
            startTimes = sched.calculateStartTimes(new DateTime(), MAX + 1);
            valid = true;
        } catch (Exception e) {
            startTimes = new ArrayList<>();
            valid = false;
            error = e.getMessage();
        }
    }
    

    private final class NavLink extends AjaxLink<Void> {
        private int offset;

        private NavLink(String id, int offset) {
            super(id);
            this.offset = offset;
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            moveIndex(offset);
            target.add(SchedulePreviewPanel.this);
        }
    }
    

}
