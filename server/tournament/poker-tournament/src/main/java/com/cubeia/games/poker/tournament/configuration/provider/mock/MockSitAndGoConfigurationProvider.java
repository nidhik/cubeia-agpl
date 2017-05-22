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

package com.cubeia.games.poker.tournament.configuration.provider.mock;

import static com.cubeia.poker.timing.TimingFactory.getRegistry;
import static com.google.common.collect.Maps.newLinkedHashMap;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import com.cubeia.poker.PokerVariant;
import com.cubeia.poker.betting.BetStrategyType;
import org.apache.log4j.Logger;

import com.cubeia.games.poker.tournament.configuration.RebuyConfiguration;
import com.cubeia.games.poker.tournament.configuration.SitAndGoConfiguration;
import com.cubeia.games.poker.tournament.configuration.blinds.Level;
import com.cubeia.games.poker.tournament.configuration.payouts.PayoutStructure;
import com.cubeia.games.poker.tournament.configuration.payouts.PayoutStructureParser;
import com.cubeia.games.poker.tournament.configuration.provider.SitAndGoConfigurationProvider;
import com.cubeia.poker.timing.TimingProfile;


/**
 * The mock provider creates new tournament automatically without the need of a database.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class MockSitAndGoConfigurationProvider implements SitAndGoConfigurationProvider {

    private static transient Logger log = Logger.getLogger(MockSitAndGoConfigurationProvider.class);

    private Map<String, SitAndGoConfiguration> requestedTournaments = newLinkedHashMap();

    /*------------------------------------------------

       LIFECYCLE METHODS

    ------------------------------------------------*/

    public MockSitAndGoConfigurationProvider() {
        log.debug("Creating mock configuration.");
        InputStream resourceAsStream = getClass().getResourceAsStream("default_payouts.csv");
        PayoutStructure payouts = new PayoutStructureParser().parsePayouts(resourceAsStream);
        SitAndGoConfiguration headsUp = createSitAndGoConfiguration("Heads up", 2, getRegistry().getTimingProfile("DEFAULT"), payouts);

        // Temporarily making this a rebuy tournament.
        headsUp.getConfiguration().setRebuyConfiguration(new RebuyConfiguration(1000, true, 1, BigDecimal.valueOf(100), new BigDecimal(200000), BigDecimal.valueOf(100), new BigDecimal(200000)));
        headsUp.getConfiguration().getBlindsStructure().insertLevel(1, new Level(new BigDecimal(20), new BigDecimal(40), new BigDecimal(5), 2, true));
        requestedTournaments.put("Heads up", headsUp);
        requestedTournaments.put("Heads up Crazy", createSitAndGoConfiguration("Heads up Crazy", 2, getRegistry().getTimingProfile("DEFAULT"), payouts,PokerVariant.CRAZY_PINEAPPLE));
        requestedTournaments.put("Express 5 Players", createSitAndGoConfiguration("Express 5 Players", 5, getRegistry().getTimingProfile("SUPER_EXPRESS"), payouts));
        requestedTournaments.put("Super expr 10 Players", createSitAndGoConfiguration("Super expr 10 Players", 10, getRegistry().getTimingProfile("SUPER_EXPRESS"), payouts));
        requestedTournaments.put("20 Players", createSitAndGoConfiguration("20 Players", 20, getRegistry().getTimingProfile("DEFAULT"), payouts));
        requestedTournaments.put("Crazy 20 Players", createSitAndGoConfiguration("Crazy 20 Players", 20, getRegistry().getTimingProfile("DEFAULT"), payouts, PokerVariant.CRAZY_PINEAPPLE));
        requestedTournaments.put("100 Players", createSitAndGoConfiguration("100 Players", 100, getRegistry().getTimingProfile("SUPER_EXPRESS"), payouts));
        requestedTournaments.put("1000 Players", createSitAndGoConfiguration("1000 Players", 1000, getRegistry().getTimingProfile("SUPER_EXPRESS"), payouts));
        SitAndGoConfiguration stud = createSitAndGoConfiguration("2 Players 7 Stud", 2, getRegistry().getTimingProfile("DEFAULT"), payouts, PokerVariant.SEVEN_CARD_STUD);
        stud.getConfiguration().setBetStrategy(BetStrategyType.FIXED_LIMIT);
        requestedTournaments.put("2 Players 7 Stud", stud);

        SitAndGoConfiguration stud2 = createSitAndGoConfiguration("5 Players 7 Stud", 5, getRegistry().getTimingProfile("DEFAULT"), payouts, PokerVariant.SEVEN_CARD_STUD);
        stud2.getConfiguration().setBetStrategy(BetStrategyType.FIXED_LIMIT);
        requestedTournaments.put("5 Players 7 Stud", stud2);

        SitAndGoConfiguration fiveCardStud = createSitAndGoConfiguration("2 Players 5 Stud", 5, getRegistry().getTimingProfile("DEFAULT"), payouts, PokerVariant.FIVE_CARD_STUD);
        fiveCardStud.getConfiguration().setBetStrategy(BetStrategyType.FIXED_LIMIT);
        requestedTournaments.put("2 Players 5 Stud", fiveCardStud);

        SitAndGoConfiguration omaha = createSitAndGoConfiguration("2 Players Omaha", 2, getRegistry().getTimingProfile("DEFAULT"), payouts, PokerVariant.OMAHA);
        omaha.getConfiguration().setBetStrategy(BetStrategyType.NO_LIMIT);
        requestedTournaments.put("2 Players Omaha", omaha);


        SitAndGoConfiguration multiTableShortHanded = createSitAndGoConfiguration("5 Plr Mtt Holdem", 5, getRegistry().getTimingProfile("DEFAULT"), payouts, PokerVariant.TEXAS_HOLDEM);
        multiTableShortHanded.getConfiguration().setBetStrategy(BetStrategyType.NO_LIMIT);
        multiTableShortHanded.getConfiguration().setSeatsPerTable(5);
        multiTableShortHanded.getConfiguration().setMaxPlayers(20);
        multiTableShortHanded.getConfiguration().setMinPlayers(20);
        requestedTournaments.put("2 Plr Mtt Holdem", multiTableShortHanded);
    }

    private SitAndGoConfiguration createSitAndGoConfiguration(String name, int capacity, TimingProfile timings, PayoutStructure payoutStructure,PokerVariant variant) {
        SitAndGoConfiguration configuration = new SitAndGoConfiguration(name, capacity, timings);
        configuration.getConfiguration().setBuyIn(BigDecimal.valueOf(10));
        configuration.getConfiguration().setFee(BigDecimal.valueOf(1));
        configuration.getConfiguration().setPayoutStructure(payoutStructure);
        configuration.getConfiguration().setCurrency("EUR");
        configuration.getConfiguration().setStartingChips(new BigDecimal(100000));
        configuration.getConfiguration().setVariant(variant);


        return configuration;
    }

    private SitAndGoConfiguration createSitAndGoConfiguration(String name, int capacity, TimingProfile timings, PayoutStructure payoutStructure) {
        return createSitAndGoConfiguration(name, capacity, timings, payoutStructure,PokerVariant.TEXAS_HOLDEM);
    }

    public Collection<SitAndGoConfiguration> getConfigurations(boolean includeArchived) {
        return requestedTournaments.values();
    }

}
