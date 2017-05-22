package com.cubeia.games.poker.admin.wicket;

import org.apache.wicket.request.mapper.parameter.PageParameters;

@SuppressWarnings("serial")
public abstract class BasePage extends com.cubeia.network.shared.web.wicket.BasePage {

    public BasePage() {
        this(null);
    }

    public BasePage(PageParameters p) {
        super();

    }

}
