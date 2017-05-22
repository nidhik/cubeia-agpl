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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.backoffice.accounting.api.NoSuchAccountException;
import com.cubeia.backoffice.wallet.api.config.AccountAttributes;
import com.cubeia.backoffice.wallet.api.config.AccountRole;
import com.cubeia.backoffice.wallet.api.dto.Account;
import com.cubeia.backoffice.wallet.api.dto.Account.AccountType;
import com.cubeia.backoffice.wallet.api.dto.exception.TooManyAccountsFoundException;
import com.cubeia.backoffice.wallet.api.dto.request.AccountQuery;
import com.cubeia.network.wallet.firebase.api.WalletServiceContract;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class AccountLookupUtil {

	Logger log = LoggerFactory.getLogger(getClass());
	
	
	private LoadingCache<AccountQuery, Long> accountIdCache = CacheBuilder.newBuilder()
            .concurrencyLevel(4)
            .maximumSize(10000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<AccountQuery, Long>() {
                public Long load(AccountQuery key) {
                    return remoteLookupUniqueAccountId(key);
                }
            });


	private final WalletServiceContract walletService;
	
    public AccountLookupUtil(WalletServiceContract walletService) {
		this.walletService = walletService;
	}

    /**
     * Gets the account id for the account with the given playerId and currency code.
     *
     * @param walletService the service to use for doing the remote call
     * @param playerId the id of the player who owns the account
     * @param currency the currency code that the account should have
     * @return the accountId of the matching account, or -1 if none found
     */
    public long lookupMainAccountIdForPlayer(Long playerId, String currency) {
    	try {
    		return lookupUniqueAccountId(null, playerId, currency, AccountType.STATIC_ACCOUNT, createRoleMap(AccountRole.MAIN));
    	} catch (NoSuchAccountException e) {
    		log.warn("No account found for playerId["+playerId+"] currency["+currency+"] Role["+AccountRole.MAIN+"]");
    		return -1;
    	}
    }
    
    public long lookupBonusAccountIdForPlayer(Long playerId, String currency) {
    	try {
    		return lookupUniqueAccountId(null, playerId, currency, AccountType.STATIC_ACCOUNT, createRoleMap(AccountRole.BONUS));
    	} catch (NoSuchAccountException e) {
    		log.warn("No account found for playerId["+playerId+"] currency["+currency+"] Role["+AccountRole.BONUS+"]");
    		return -1;
    	}
    }
    
    /**
     * Look up a unique account for operator.
     * 
     * @param walletClient
     * @param operatorId
     * @param currencyCode
     * @param role
     * @return Account id
     * @throws NoSuchAccountException if no account found
     * @throws RuntimeException if multiple accounts found for the query
     */
    public long lookupOperatorAccount(long operatorId, String currencyCode, AccountRole role) {
    	log.debug("Lookup Operator account. Operator["+operatorId+"] currency["+currencyCode+"] Role["+role+"]");
    	Map<String, String> attributes = createRoleMap(role);
    	long accountId = lookupUniqueAccountId(operatorId, null, currencyCode, AccountType.OPERATOR_ACCOUNT, attributes);
		log.debug("Lookup Operator account. Operator["+operatorId+"] currency["+currencyCode+"] Role["+role+"], Result: "+accountId);
		return accountId;
    }

    /**
     * Look up a unique system account
     * 
     * @param currencyCode
     * @param role
     * @return Account id
     * @throws NoSuchAccountException if no account found
     * @throws RuntimeException if multiple accounts found for the query
     */
    public long lookupSystemAccount(String currencyCode, AccountRole role) {
    	log.debug("Lookup System account. Currency["+currencyCode+"] Role["+role+"]");
    	Map<String, String> attributes = createRoleMap(role);
    	long accountId = lookupUniqueAccountId(null, null, currencyCode, AccountType.SYSTEM_ACCOUNT, attributes);
		log.debug("Lookup System account. Currency["+currencyCode+"] Role["+role+"], Result: "+accountId);
		return accountId;
    }
    
    /**
     * Lookup a unique account
     * 
     * @param walletClient
     * @param operatorId
     * @param userId
     * @param currencyCode
     * @param type
     * @param attributes
     * @return Account id
     * @throws NoSuchAccountException if no account found
     * @throws RuntimeException if multiple accounts found for the query
     */
    private long lookupUniqueAccountId(Long operatorId, Long userId, String currencyCode, AccountType type, Map<String, String> attributes) {
    	AccountQuery query = new AccountQuery();
    	query.setOperatorId(operatorId);
    	query.setCurrency(currencyCode);
    	query.setUserId(userId);
    	query.setType(type.name());
		query.setAttributes(attributes);
    	return lookupUniqueAccountId(query);
    }
    
    private long lookupUniqueAccountId(AccountQuery query) {
		try {
			return accountIdCache.get(query);
		} catch (Exception e) {
			Throwables.propagateIfPossible(e.getCause(), NoSuchAccountException.class, TooManyAccountsFoundException.class);
		    throw new IllegalStateException(e);
		}
	}
    
    protected Long remoteLookupUniqueAccountId(AccountQuery query) {
		Account account = walletService.findUniqueAccount(query);
		if (account == null) {
			throw new NoSuchAccountException("No account matches the query: "+query);
		}
		return account.getId();
	}
    
    private Map<String, String> createRoleMap(AccountRole role) {
		Map<String, String> attributes = new HashMap<String, String>();
    	attributes.put(AccountAttributes.ROLE.name(), role.name());
		return attributes;
	}

}
