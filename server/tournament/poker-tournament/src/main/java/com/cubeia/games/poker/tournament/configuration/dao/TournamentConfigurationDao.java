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

package com.cubeia.games.poker.tournament.configuration.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.SitAndGoConfiguration;
import com.google.inject.Inject;

public class TournamentConfigurationDao {

    protected EntityManager entityManager;

    @Inject
    public TournamentConfigurationDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(ScheduledTournamentConfiguration object) {
        entityManager.persist(object);
    }

    @SuppressWarnings("unchecked")
    public List<ScheduledTournamentConfiguration> getScheduledTournamentConfigurations(boolean includeArchived) {
        String qlString = "from ScheduledTournamentConfiguration" + (includeArchived ? "" : " where configuration.archived is false");
        return entityManager.createQuery(qlString).getResultList();
    }

    @SuppressWarnings("unchecked")
    public Collection<SitAndGoConfiguration> getSitAndGoConfigurations(boolean includeArchived) {
        String qlString = "from SitAndGoConfiguration" + (includeArchived ? "" : " where configuration.archived is false");
        return entityManager.createQuery(qlString).getResultList();
    }

    public ScheduledTournamentConfiguration getScheduledTournamentConfiguration(int id) {
        return entityManager.find(ScheduledTournamentConfiguration.class, id);
    }

    public SitAndGoConfiguration getSitAndGoConfiguration(int id) {
        return entityManager.find(SitAndGoConfiguration.class, id);
    }
}