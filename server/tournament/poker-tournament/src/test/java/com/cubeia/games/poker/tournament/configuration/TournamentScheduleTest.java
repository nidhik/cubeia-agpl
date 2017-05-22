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

package com.cubeia.games.poker.tournament.configuration;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TournamentScheduleTest {

    private TimeZone originalTimeZone;

    @Before
    public void setup() {
        originalTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT")));
    }

    @After
    public void after() {
        TimeZone.setDefault(originalTimeZone);
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(originalTimeZone));
    }

    @Test
    public void testNextAnnounceTime() {
        Date startDate = new DateTime(2011, 7, 5, 9, 0, 0).toDate();
        Date endDate = new DateTime(2012, 7, 5, 9, 0, 0).toDate();
        TournamentSchedule tournamentSchedule = new TournamentSchedule(startDate, endDate, "0 30 14 * * ?", 10, 20, 30);

        DateTime nextAnnounceTime = tournamentSchedule.getNextAnnounceTime(new DateTime(2012, 6, 2, 9, 0, 0));
        assertEquals(new DateTime(2012, 6, 2, 14, 0, 0), nextAnnounceTime);
    }
    
    @Test
    public void testNextRegisteringTime() {
        Date startDate = new DateTime(2011, 7, 5, 9, 0, 0).toDate();
        Date endDate = new DateTime(2012, 7, 5, 9, 0, 0).toDate();
        TournamentSchedule tournamentSchedule = new TournamentSchedule(startDate, endDate, "0 30 14 * * ?", 10, 20, 30);

        DateTime nextRegisteringTime = tournamentSchedule.getNextRegisteringTime(new DateTime(2012, 6, 2, 9, 0, 0));
        assertEquals(new DateTime(2012, 6, 2, 14, 10, 0), nextRegisteringTime);
    }
    

    @Test
    public void testNoMoreTournamentsAfterEndDate() {
        Date start = new DateTime(2012, 6, 5, 9, 0, 0).toDate();
        Date end = new DateTime(2012, 7, 5, 9, 0, 0).toDate();
        TournamentSchedule tournamentSchedule = new TournamentSchedule(start, end, "0 30 14 * * ?", 10, 20, 30);

        DateTime nextAnnounceTime = tournamentSchedule.getNextAnnounceTime(new DateTime(2012, 7, 9, 9, 0, 0));
        assertNull("Should be null, but was " + nextAnnounceTime, nextAnnounceTime);
    }

    @Test
    public void test10MinuteSchedule() {
        Date start = new DateTime(2011, 7, 5, 9, 0, 0).toDate();
        Date end = new DateTime(2013, 7, 5, 9, 0, 0).toDate();
        TournamentSchedule tournamentSchedule = new TournamentSchedule(start, end, "0 */10 * * * ?", 3, 5, 5);

        DateTime nextAnnounceTime = tournamentSchedule.getNextStartTime(new DateTime(2012, 7, 9, 15, 3, 0));
        assertEquals(new DateTime(2012, 7, 9, 15, 10, 0), nextAnnounceTime);
    }
    
    @Test
    public void testNextStartAndAnnounceMondayOnly() {
        Date start = new DateTime(2013, 12, 16, 1, 0, 0).toDate();
        Date end = new DateTime(2013, 12, 16, 1, 0, 0).toDate();
        TournamentSchedule tournamentSchedule = new TournamentSchedule(start, end, "0 0 1 * * ?", 8640, 2880, 120);

        DateTime now = new DateTime(2013, 12, 11, 14, 48);
        DateTime nextStart = tournamentSchedule.getNextStartTime(now);
        assertThat(nextStart, is(new DateTime(2013, 12, 16, 1, 0, 0)));
        
        DateTime nextAnnounceTime = tournamentSchedule.getNextAnnounceTime(now);
        
        assertThat(nextAnnounceTime, is(new DateTime(2013, 12, 16, 1, 0, 0).minusMinutes(8640).minusMinutes(2880)));
        
        DateTime nextStart2 = tournamentSchedule.getNextStartTime(nextStart);
        assertThat(nextStart2, CoreMatchers.nullValue());
    }
    
    @Test
    public void testCalculateStartTimes() {
        Date start = new DateTime(2014, 1, 1, 1, 0, 0).toDate();
        Date end = new DateTime(2014, 2, 1, 1, 0, 0).toDate();
        TournamentSchedule ts = new TournamentSchedule(start, end, "0 0 1 ? * MON", 10, 20, 30);
        
        List<DateTime> starts = ts.calculateStartTimes(new DateTime(2014, 1, 1, 0, 0, 0), 100);
        assertThat(starts, is(asList(
            new DateTime(2014, 1,  6, 1, 0, 0),
            new DateTime(2014, 1, 13, 1, 0, 0),
            new DateTime(2014, 1, 20, 1, 0, 0),
            new DateTime(2014, 1, 27, 1, 0, 0))));

        starts = ts.calculateStartTimes(new DateTime(2014, 2, 1, 0, 0, 0), 100);
        assertThat(starts.isEmpty(), is(true));
        
        starts = ts.calculateStartTimes(new DateTime(2014, 1, 27, 0, 0, 0), 100);
        assertThat(starts, is(asList(new DateTime(2014, 1, 27, 1, 0, 0))));
        
        starts = ts.calculateStartTimes(new DateTime(2014, 1, 1, 0, 0, 0), 2);
        assertThat(starts, is(asList(
            new DateTime(2014, 1,  6, 1, 0, 0),
            new DateTime(2014, 1, 13, 1, 0, 0))));
        
        
    }
    
}
