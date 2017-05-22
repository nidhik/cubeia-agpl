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

package com.cubeia.poker.settings;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.cubeia.games.poker.common.money.Currency;
import com.cubeia.poker.PokerVariant;
import com.cubeia.poker.betting.BetStrategyType;
import com.cubeia.poker.model.BlindsLevel;
import com.cubeia.poker.timing.TimingProfile;

public class PokerSettings implements Serializable {

    private static final long serialVersionUID = -8524532061876453809L;

    private BlindsLevel blindsLevel;

    private final BigDecimal minBuyIn;

    private final BigDecimal maxBuyIn;

    private final BetStrategyType betStrategyType;

    private final TimingProfile timing;

    private final int tableSize;

    private final RakeSettings rakeSettings;

    private final Map<Serializable, Serializable> attributes;

    private final Currency currency;

    private final PokerVariant variant;

    private long sitoutTimeLimitMilliseconds = 5 * 60 * 1000;

	private String tableName;
	
	private long ratholingTimeOutMinutes = 60;

    public PokerSettings(PokerVariant variant, BlindsLevel blindsLevel, BetStrategyType betStrategyType, BigDecimal minBuyIn, BigDecimal maxBuyIn, TimingProfile timing,
            int tableSize, RakeSettings rakeSettings, Currency currency, Map<Serializable, Serializable> attributes) {
        this.variant = variant;
        this.blindsLevel = blindsLevel;
        this.minBuyIn = minBuyIn;
        this.maxBuyIn = maxBuyIn;
        this.betStrategyType = betStrategyType;
        this.timing = timing;
        this.tableSize = tableSize;
        this.rakeSettings = rakeSettings;
        this.attributes = attributes;
        this.currency = currency;
    }

    public Map<Serializable, Serializable> getAttributes() {
        return attributes;
    }

    public BigDecimal getAnteAmount() {
        return blindsLevel.getAnteAmount();
    }

    public BetStrategyType getBetStrategyType() {
        return betStrategyType;
    }

    public BigDecimal getMaxBuyIn() {
        return maxBuyIn;
    }

    public TimingProfile getTiming() {
        return timing;
    }

    public int getTableSize() {
        return tableSize;
    }

    public BigDecimal getMinBuyIn() {
        return minBuyIn;
    }

    public RakeSettings getRakeSettings() {
        return rakeSettings;
    }

    public long getSitoutTimeLimitMilliseconds() {
        return sitoutTimeLimitMilliseconds;
    }

    public void setSitoutTimeLimitMilliseconds(long sitoutTimeLimitMilliseconds) {
        this.sitoutTimeLimitMilliseconds = sitoutTimeLimitMilliseconds;
    }

    public BigDecimal getSmallBlindAmount() {
        return blindsLevel.getSmallBlindAmount();
    }

    public BigDecimal getBringInAmount() {
        return getSmallBlindAmount();
    }

    public BigDecimal getBigBlindAmount() {
        return blindsLevel.getBigBlindAmount();
    }

    public void setBlindsLevels(BlindsLevel level) {
        this.blindsLevel = level;
    }

    public BlindsLevel getBlindsLevel() {
        return blindsLevel;
    }

    public Currency getCurrency() {
        return currency;
    }

    public PokerVariant getVariant() {
        return variant;
    }
    
    public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public long getRatholingTimeOutMinutes() {
		return ratholingTimeOutMinutes;
	}
	
	public void setRatholingTimeOutMinutes(long ratholingTimeOutMinutes) {
		this.ratholingTimeOutMinutes = ratholingTimeOutMinutes;
	}
}
