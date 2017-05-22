package com.cubeia.games.poker.admin.wicket.login;

import com.cubeia.games.poker.admin.wicket.PokerAdminWebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class) 
@ContextConfiguration(locations={"classpath:poker-admin-spring-app.xml","classpath:poker-admin-spring-test.xml"}) 
@TransactionConfiguration 
@Transactional 
public abstract class AbstractWicketTestCase {

	@Autowired 
	protected PokerAdminWebApplication app; 
	
	protected WicketTester wicket; 
	
	@Inject DataSource dataSource;
	
	public AbstractWicketTestCase() {
		
	}

	@Before 
	public void setup() throws IllegalStateException, NamingException { 
		System.out.println("----- DS: "+dataSource);
		SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
	    builder.bind("java:comp/env/jdbc/poker", dataSource);
	    builder.activate();
		wicket = new WicketTester(app); 
	} 
}
