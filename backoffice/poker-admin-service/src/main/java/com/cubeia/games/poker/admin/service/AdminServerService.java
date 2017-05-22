/**
 * Copyright (C) 2013 Cubeia Ltd <info@cubeia.com>
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

package com.cubeia.games.poker.admin.service;

import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import org.apache.log4j.Logger;
import com.cubeia.jetty.JettyEmbed;

public class AdminServerService implements AdminServerContract, Service {

    public static final int WAR_PORT = 8088;
    public static final String WAR_FILE = "poker-admin*.war";
    
    private static final Logger log = Logger.getLogger(AdminServerService.class);   
        
    @Override
    public void init(ServiceContext con) throws SystemException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void start() {
        JettyEmbed je = new JettyEmbed(this, WAR_PORT, WAR_FILE, "/", "admin");
        try {
            je.start();

        } catch (Exception ex) {
            log.debug(ex, ex);
        }
    }

    @Override
    public void stop() {
    }
}
