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

package com.cubeia.games.poker.tournament.configuration.provider;

import static com.cubeia.games.poker.common.jpa.TransactionHelper.doInTrasaction;

import java.util.Collection;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;

import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.SitAndGoConfiguration;
import com.cubeia.games.poker.tournament.configuration.dao.TournamentConfigurationDao;
import com.google.inject.Inject;

public class RealTournamentScheduleProvider implements TournamentScheduleProvider {

    private TournamentConfigurationDao dao;
    
    private EntityManager entityManager;
    
    @Inject
    public RealTournamentScheduleProvider(TournamentConfigurationDao dao, EntityManager entityManager) {
        this.dao = dao;
        this.entityManager = entityManager;
    }

    @Override
    public Collection<ScheduledTournamentConfiguration> getTournamentSchedule(final boolean includeArchived) {
        return doInTrasaction(entityManager, new Callable<Collection<ScheduledTournamentConfiguration>>() {
            @Override public Collection<ScheduledTournamentConfiguration> call() throws Exception {
                return dao.getScheduledTournamentConfigurations(includeArchived);
            }
        });
    }

    @Override
    public ScheduledTournamentConfiguration getScheduledTournamentConfiguration(final int id) {
        return doInTrasaction(entityManager, new Callable<ScheduledTournamentConfiguration>() {
            @Override public ScheduledTournamentConfiguration call() throws Exception {
                return dao.getScheduledTournamentConfiguration(id);
            }
        });
    }

    @Override
    public SitAndGoConfiguration getSitAndGoTournamentConfiguration(final int id) {
        return doInTrasaction(entityManager, new Callable<SitAndGoConfiguration>() {
            @Override public SitAndGoConfiguration call() throws Exception {
                return dao.getSitAndGoConfiguration(id);
            }
        });
    }
    
}
