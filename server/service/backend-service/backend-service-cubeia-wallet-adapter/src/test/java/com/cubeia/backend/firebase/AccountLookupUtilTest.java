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

package com.cubeia.backend.firebase;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cubeia.backoffice.accounting.api.NoSuchAccountException;
import com.cubeia.backoffice.wallet.api.config.AccountAttributes;
import com.cubeia.backoffice.wallet.api.config.AccountRole;
import com.cubeia.backoffice.wallet.api.dto.Account;
import com.cubeia.backoffice.wallet.api.dto.Account.AccountType;
import com.cubeia.backoffice.wallet.api.dto.exception.TooManyAccountsFoundException;
import com.cubeia.backoffice.wallet.api.dto.request.AccountQuery;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.network.wallet.firebase.api.WalletServiceContract;

public class AccountLookupUtilTest {
    @Mock
    private WalletServiceContract walletService;

    AccountLookupUtil lookup;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        lookup = new AccountLookupUtil(walletService);
    }

    @Test
    public void testLookupRakeAccountId() throws SystemException {
        AccountLookupUtil acl = new AccountLookupUtil(walletService);

        ArgumentCaptor<AccountQuery> requestCaptor = ArgumentCaptor.forClass(AccountQuery.class);
        Account account = new Account();
    	account.setId(22L);
    	
        when(walletService.findUniqueAccount(requestCaptor.capture())).thenReturn(account);

        long lookupRakeAccountId = acl.lookupSystemAccount("EUR", AccountRole.RAKE);
        assertThat(lookupRakeAccountId, is(22L));

        AccountQuery query = requestCaptor.getValue();
        assertThat(query.getType(), is(AccountType.SYSTEM_ACCOUNT.name()));
        assertThat(query.getCurrency(), is("EUR"));
        assertThat(query.getAttributes().get(AccountAttributes.ROLE.name()), is(AccountRole.RAKE.name()));
    }

    @Test
    public void testLookupOperatorRakeAccount() {
    	ArgumentCaptor<AccountQuery> requestCaptor = ArgumentCaptor.forClass(AccountQuery.class);
    	
    	Account account = new Account();
    	account.setId(22L);
		when(walletService.findUniqueAccount(requestCaptor.capture())).thenReturn(account);
    	long id = lookup.lookupOperatorAccount(1L, "EUR", AccountRole.RAKE);
    	assertThat(id, is(22L));
    	
    	AccountQuery query = requestCaptor.getValue();
    	assertThat(query.getCurrency(), is("EUR"));
    	assertThat(query.getOperatorId(), is(1L));
    	assertThat(query.getType(), is(AccountType.OPERATOR_ACCOUNT.name()));
    	assertThat(query.getAttributes().get(AccountAttributes.ROLE.name()), is(AccountRole.RAKE.name()));
    }
    
    @Test (expected=NoSuchAccountException.class)
    public void testLookupOperatorRakeAccountNotFound() {
    	when(walletService.findUniqueAccount(Mockito.any(AccountQuery.class))).thenThrow(new NoSuchAccountException("not found"));
    	lookup.lookupOperatorAccount(1L, "EUR", AccountRole.RAKE);
    }
    
    @Test (expected=TooManyAccountsFoundException.class)
    public void testLookupOperatorRakeAccountNotUnique() {
    	when(walletService.findUniqueAccount(Mockito.any(AccountQuery.class))).thenThrow(new TooManyAccountsFoundException("more than one"));
    	lookup.lookupOperatorAccount(1L, "EUR", AccountRole.RAKE);
    }
}
