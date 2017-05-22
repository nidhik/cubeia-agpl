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

package com.cubeia.games.poker.tournament.configuration.payouts;

import com.cubeia.games.poker.common.money.Currency;
import com.google.code.morphia.annotations.Transient;
import org.apache.log4j.Logger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

/**
 * Defines the payouts for a given range of entrants.
 */
@Entity
public class Payouts implements Serializable {

    private static final Logger log = Logger.getLogger(Payouts.class);

    @Id
    @GeneratedValue
    private int id;

    /**
     * The range of entrants these payouts are applicable for.
     */
    @ManyToOne(fetch = EAGER, cascade = ALL)
    private IntRange entrantsRange;

    /** The size of the prize pool. */
    private BigDecimal prizePool;

    private Currency currency;

    /** A list of payouts, each payout defines a percentage for a range of positions in the tournament. */
    @OneToMany(fetch = EAGER, cascade = ALL)
    @OrderColumn
    private List<Payout> payoutList = new ArrayList<Payout>();

    /**
     * Maps positions to payouts. Does not need to be stored in the database as this is only calculated during runtime.
     */
    @Transient
    private transient Map<Integer, BigDecimal> positionsToPayouts = new HashMap<Integer, BigDecimal>();

    Payouts() {
    }

    public Payouts(IntRange entrants, List<Payout> payouts, BigDecimal prizePool, Currency currency) {
        this.entrantsRange = entrants;
        this.payoutList = payouts;
        this.prizePool = prizePool;
        this.currency = currency;
    }

    public Payouts(IntRange entrants, List<Payout> payouts) {
        this(entrants, payouts, BigDecimal.ZERO,null);
    }

    public boolean inRange(int numberOfEntrants) {
        return entrantsRange.contains(numberOfEntrants);
    }

    public Payouts withPrizePool(BigDecimal prizePool, Currency currency, BigDecimal buyIn) {
        Payouts payouts = new Payouts(entrantsRange, payoutList, prizePool, currency);
        payouts.calculatePayouts(buyIn);
        return payouts;
    }

    private void calculatePayouts(BigDecimal buyIn) {
        BigDecimal totalPayouts = null;
        // First try rounding to closest buy-in
        if(buyIn.compareTo(BigDecimal.ZERO) > 0) {
            totalPayouts = calculatePayoutsRoundingToClosestBuyIn(buyIn);
        }
        // If that doesn't add up, fall back to normal rounding.
        if (totalPayouts==null || totalPayouts.compareTo(prizePool) != 0) {
            log.debug("Total payouts " + totalPayouts + " didn't equal the prize pool " + prizePool + ", falling back to normal rounding.");
            positionsToPayouts.clear();
            calculatePayoutsWithNormalRounding(buyIn);
        }
    }

    private void calculatePayoutsWithNormalRounding(BigDecimal buyIn) {
        BigDecimal totalPayouts = BigDecimal.ZERO;
        int inTheMoney = 0;
        for (int i = 1; i < entrantsRange.getStop(); i++) {
            BigDecimal payout = calculatePayoutForPosition(i);
            if (payout.equals(BigDecimal.ZERO)) {
                // Reached the end of in-the-money.
                break;
            }
            inTheMoney++;
            positionsToPayouts.put(i, payout);
            totalPayouts = totalPayouts.add(payout);
        }
        // Check remaining cents.
        if (totalPayouts.compareTo(prizePool) < 0) {
            distributeRemainingCents(totalPayouts, inTheMoney);
        } else if (totalPayouts.compareTo(prizePool) < 0) {
            log.fatal("Total payouts " + totalPayouts + " > prize pool " + prizePool + ", something is very wrong! buyIn " + buyIn + " entrantsRange " + entrantsRange);
        }
    }

    private void distributeRemainingCents(BigDecimal totalPayouts, int inTheMoney) {
        log.debug("We have some remaining cents, distributing them round robin style.");
        BigDecimal cent = BigDecimal.ONE.movePointLeft(currency.getFractionalDigits());
        if (inTheMoney < 2) {
            // Only one (or zero, which is weird) winner, he gets all the remaining cents.
            log.debug("Only " + inTheMoney + " players in the money, giving all remaining cents to the winner.");
            BigDecimal remainingCents = prizePool.subtract(totalPayouts);
            log.debug("Increasing payout for winner by " + remainingCents);
            increasePositionBy(1, remainingCents);
        } else {
            int position = 0;
            while (totalPayouts.compareTo(prizePool) < 0) {
                log.debug("Increasing payout for position " + (position + 1) + " by " + cent);
                increasePositionBy(position + 1, cent);
                position = (position + 1) % inTheMoney;
                totalPayouts = totalPayouts.add(cent);
            }
        }
    }

    private void increasePositionBy(int position, BigDecimal increment) {
        BigDecimal payout = positionsToPayouts.get(position);
        payout = payout.add(increment);
        positionsToPayouts.put(position, payout);
    }

    private BigDecimal calculatePayoutsRoundingToClosestBuyIn(BigDecimal buyIn) {
        //WORKAROUND to allow freerolls
        //if (buyIn.compareTo(BigDecimal.ZERO) == 0 ) {
        //    buyIn = BigDecimal.valueOf(1);
        //}
        BigDecimal totalPayouts = BigDecimal.ZERO;
        for (int i = 1; i < entrantsRange.getStop(); i++) {
            BigDecimal payout = calculatePayoutRoundedToClosestBuyIn(i, buyIn);
            if (payout.compareTo(BigDecimal.ZERO) == 0) {
                // We've reached the end of the money.
                break;
            }
            positionsToPayouts.put(i, payout);
            totalPayouts = totalPayouts.add(payout);
        }
        return totalPayouts;
    }

    private BigDecimal calculatePayoutRoundedToClosestBuyIn(int position, BigDecimal buyIn) {
        BigDecimal percentageForPosition = getPercentageForPosition(position);
        BigDecimal unRounded = percentageForPosition.multiply(prizePool).divide(new BigDecimal(100));
        BigDecimal numberOfBuyIns = unRounded.divide(buyIn, 0, RoundingMode.HALF_UP);
        return numberOfBuyIns.multiply(buyIn);
    }

    public BigDecimal calculatePayoutForPosition(int position) {
        return getPercentageForPosition(position).multiply(prizePool).divide(new BigDecimal(100),currency.getFractionalDigits(), RoundingMode.DOWN);
    }

    private BigDecimal getPercentageForPosition(int position) {
        for (Payout payout : payoutList) {
            if (payout.getPositionRange().contains(position)) {
                return payout.getPercentage();
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getPayoutForPosition(int position) {
        BigDecimal payout = positionsToPayouts.get(position);
        if (payout != null) {
            return payout;
        }
        return BigDecimal.ZERO;
    }

    public IntRange getEntrantsRange() {
        return entrantsRange;
    }

    void setEntrantsRange(IntRange entrantsRange) {
        this.entrantsRange = entrantsRange;
    }

    BigDecimal getPrizePool() {
        return prizePool;
    }

    public void setPrizePool(BigDecimal prizePool) {
        this.prizePool = prizePool;
    }

    public List<Payout> getPayoutList() {
        return payoutList;
    }

    public int getNumberOfPlacesInTheMoney() {
        int max = 0;
        for (Payout payout : payoutList) {
            int upperBound = payout.getPositionRange().getStop();
            if (upperBound > max) {
                max = upperBound;
            }
        }
        return max;
    }

    public void setPayoutList(List<Payout> payoutList) {
        this.payoutList = payoutList;
    }

    @Override
    public String toString() {
        return "Payouts{" +
                "entrantsRange=" + entrantsRange +
                ", prizePool=" + prizePool +
                ", payouts=" + payoutList +
                '}';
    }

    public void verify() {
        BigDecimal sum = BigDecimal.valueOf(0);
        for (Payout payout : payoutList) {
            sum = sum.add(payout.getTotalPayoutPercentage());
        }
        if (!between(99.9, 100.0, sum)) {
            throw new IllegalStateException("Sum was not 100, but: " + sum + " for range: " + entrantsRange + ".");
        }
        log.debug("Verified payouts for range: " + entrantsRange + ". Sum = "+ sum);
    }

    private boolean between(double lower, double upper, BigDecimal sum) {
        return sum.compareTo(BigDecimal.valueOf(lower)) >= 0 && sum.compareTo(BigDecimal.valueOf(upper)) <= 0;
    }

    public Currency getCurrency() {
        return this.currency;
    }

}
