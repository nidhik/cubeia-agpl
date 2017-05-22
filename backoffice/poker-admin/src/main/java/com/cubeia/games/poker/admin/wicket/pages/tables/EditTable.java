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

package com.cubeia.games.poker.admin.wicket.pages.tables;

import com.cubeia.poker.PokerVariant;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.cubeia.games.poker.admin.db.AdminDAO;
import com.cubeia.games.poker.admin.wicket.BasePage;
import com.cubeia.games.poker.entity.TableConfigTemplate;

@AuthorizeInstantiation({"ADMIN"})
public class EditTable extends BasePage {

    private static final long serialVersionUID = 6896786450236805072L;

    @SpringBean(name="adminDAO")
    private AdminDAO adminDAO;
    
    private TableConfigTemplate table;
    
    @SuppressWarnings("serial")
	public EditTable(final PageParameters parameters) {
        super(parameters);
        final Integer templateId = parameters.get("templateId").toInt();
        loadFormData(templateId);
        TableForm tableForm = new TableForm("tableForm", table) {

            @Override
            protected void onSubmit(TableConfigTemplate config) {
                if(config.getVariant() == PokerVariant.SEVEN_CARD_STUD && config.getSeats()>7) {
                    error("Maximum number of seats per table for 7 card stud is 7");
                    return;
                }
                adminDAO.merge(config);
                // info("Table template updated, id = " + templateId);
                setResponsePage(ListTables.class);
            }

            @Override
            public String getActionLabel() {
                return "Save";
            }
        };

        add(tableForm);
        add(new FeedbackPanel("feedback"));
    }

    private void loadFormData(final Integer templateId) {
        table = adminDAO.getItem(TableConfigTemplate.class, templateId);
    }

    @Override
    public String getPageTitle() {
        return "Edit Table";
    }
}
