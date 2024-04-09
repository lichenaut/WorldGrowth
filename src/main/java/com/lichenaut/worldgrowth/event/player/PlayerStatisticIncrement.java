package com.lichenaut.worldgrowth.event.player;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import org.apache.logging.log4j.Logger;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.util.HashSet;
import java.util.Set;

public class PlayerStatisticIncrement extends WGPointEvent<PlayerStatisticIncrementEvent> {

    private final Set<Statistic> statistics = new HashSet<>();

    public PlayerStatisticIncrement(Main main, WGDBManager databaseManager, Logger logging, int quota, int points) {
        super(main, databaseManager, logging, quota, points);
        statistics.add(Statistic.ARMOR_CLEANED);
        statistics.add(Statistic.BANNER_CLEANED);
        statistics.add(Statistic.CAKE_SLICES_EATEN);
        statistics.add(Statistic.CAULDRON_USED);
        statistics.add(Statistic.CLEAN_SHULKER_BOX);
        statistics.add(Statistic.RECORD_PLAYED);
        statistics.add(Statistic.SLEEP_IN_BED);
        statistics.add(Statistic.TRADED_WITH_VILLAGER);
    }

    @Override
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    protected void onEvent(PlayerStatisticIncrementEvent event) {
        if (statistics.contains(event.getStatistic())) incrementCount();
    }
}
