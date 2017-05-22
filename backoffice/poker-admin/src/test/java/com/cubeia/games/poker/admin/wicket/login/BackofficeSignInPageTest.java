package com.cubeia.games.poker.admin.wicket.login;

import com.cubeia.network.shared.web.wicket.pages.login.LoginPage;
import org.junit.Ignore;
import org.junit.Test;

import com.cubeia.games.poker.admin.wicket.pages.tournaments.scheduled.ListTournaments;

@Ignore
public class BackofficeSignInPageTest extends AbstractWicketTestCase {

	@Test
	public void test() {
		wicket.startPage(ListTournaments.class);
		wicket.assertRenderedPage(LoginPage.class);
	}

	
}
