/**
 * Copyright (C) 2012 Cubeia Ltd <info@cubeia.com>
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

package com.cubeia.games.poker.admin.wicket.pages.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.backoffice.accounting.api.NoSuchAccountException;
import com.cubeia.backoffice.operator.api.OperatorDTO;
import com.cubeia.backoffice.operator.client.OperatorServiceClient;
import com.cubeia.backoffice.wallet.api.config.AccountRole;
import com.cubeia.backoffice.wallet.api.dto.Account.AccountType;
import com.cubeia.backoffice.wallet.api.dto.Currency;
import com.cubeia.backoffice.wallet.api.dto.CurrencyListResult;
import com.cubeia.backoffice.wallet.client.WalletServiceClient;
import com.cubeia.backoffice.wallet.util.AccountLookup;
import com.cubeia.games.poker.admin.wicket.BasePage;

@SuppressWarnings("serial")
public class VerifySystemAccounts extends BasePage {

	static Logger log = LoggerFactory.getLogger(VerifySystemAccounts.class);
	
	@SpringBean(name="walletClient")
    private WalletServiceClient walletService;
	
	@SpringBean(name="operatorClient")
    private OperatorServiceClient operatorService;
	
	public VerifySystemAccounts(PageParameters p) {
		super(p);
		initPage();
	}
	
	private void initPage() {
		AccountLookup accountLookup = new AccountLookup(walletService);
		
		List<AccountInformation> systemAccountList = new ArrayList<>();
		List<AccountInformation> operatorAccountList = new ArrayList<>();
		CurrencyListResult supportedCurrencies = walletService.getSupportedCurrencies();
		
		List<AccountRole> systemRoles = new ArrayList<>();
		systemRoles.add(AccountRole.MAIN);
		systemRoles.add(AccountRole.PROMOTION);
		systemRoles.add(AccountRole.RAKE);
		systemRoles.add(AccountRole.BONUS);
		
		List<AccountRole> operatorRoles = new ArrayList<>();
		operatorRoles.add(AccountRole.MAIN);
		operatorRoles.add(AccountRole.RAKE);
		operatorRoles.add(AccountRole.BONUS);
		
		// Lookup system accounts
		AccountType type = AccountType.SYSTEM_ACCOUNT;
		boolean errorAlert = false;
		for (Currency currency : supportedCurrencies.getCurrencies()) {
			for (AccountRole role : systemRoles) {
				AccountInformation info = new AccountInformation();
				info.setCurrency(currency.getCode());
				info.setType(type);
				info.setRole(role);
				
				try {
					long accountId = accountLookup.lookupSystemAccount(currency.getCode(), role);
					if (accountId > 0) {
						info.setId(accountId);
						info.setFound(true);
					}
				} catch (NoSuchAccountException e) {
					info.setFound(false);
					errorAlert = true;
				}
				systemAccountList.add(info);	
			}
		}
		
		final WebMarkupContainer alert = new WebMarkupContainer("errorAlert");
		alert.setVisibilityAllowed(errorAlert);
		add(alert);
		
		// Lookup operator accounts
		List<OperatorDTO> operators = operatorService.getOperators();
		
		type = AccountType.OPERATOR_ACCOUNT;
		for (OperatorDTO operator : operators) {
			for (Currency currency : supportedCurrencies.getCurrencies()) {
				for (AccountRole role : operatorRoles) {
					AccountInformation info = new AccountInformation();
					info.setCurrency(currency.getCode());
					info.setType(type);
					info.setRole(role);
					info.setOperator(operator.getId()+":"+operator.getName());
					try {
						long accountId = accountLookup.lookupOperatorAccount(operator.getId(), currency.getCode(), role);
						if (accountId > 0) {
							info.setId(accountId);
							info.setFound(true);
						}
					} catch (NoSuchAccountException e) {
						info.setFound(false);
					}
					
					operatorAccountList.add(info);	
				}
			}
		}
		
		ListView<AccountInformation> systemList = new ListView<AccountInformation>("systemList", systemAccountList) {
		    protected void populateItem(ListItem<AccountInformation> item) {
		    	AccountInformation info = item.getModel().getObject();
		    	item.add(new Label("role", info.getRole()));
		    	item.add(new Label("currency", info.getCurrency()));
		    	Label found = new Label("accountFound", info.isFound() ? "ID "+info.getId() : "Missing");
		    	if (info.isFound()) {
		    		found.add(new AttributeModifier("class", "label label-success"));
		    	} else {
		    		found.add(new AttributeModifier("class", "label label-warning"));
		    	}
		    	item.add(found);
		        
		    }
		};
		add(systemList);
		
		ListView<AccountInformation> operatorList = new ListView<AccountInformation>("operatorList", operatorAccountList) {
		    protected void populateItem(ListItem<AccountInformation> item) {
		    	AccountInformation info = item.getModel().getObject();
		    	item.add(new Label("operator", info.getOperator()));
		    	item.add(new Label("role", info.getRole()));
		    	item.add(new Label("currency", info.getCurrency()));
		    	Label found = new Label("accountFound", info.isFound() ? "ID "+info.getId() : "Missing");
		    	if (info.isFound()) {
		    		found.add(new AttributeModifier("class", "label label-success"));
		    	} else {
		    		found.add(new AttributeModifier("class", "label label-warning"));
		    	}
		    	item.add(found);
		        
		    }
		};
		add(operatorList);
	}
	

	@Override
	public String getPageTitle() {
		return "Verify Accounts";
	}
	
	private class AccountInformation implements Serializable {
		private Long id;
		private String operator;
		private boolean found = false;
		private String currency;
		private AccountRole role;
		private AccountType type;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public boolean isFound() {
			return found;
		}
		public void setFound(boolean found) {
			this.found = found;
		}
		public String getCurrency() {
			return currency;
		}
		public void setCurrency(String currency) {
			this.currency = currency;
		}
		public AccountRole getRole() {
			return role;
		}
		public void setRole(AccountRole role) {
			this.role = role;
		}
		@SuppressWarnings("unused")
		public AccountType getType() {
			return type;
		}
		public void setType(AccountType type) {
			this.type = type;
		}
		public String getOperator() {
			return operator;
		}
		public void setOperator(String operator) {
			this.operator = operator;
		}
	}
}
