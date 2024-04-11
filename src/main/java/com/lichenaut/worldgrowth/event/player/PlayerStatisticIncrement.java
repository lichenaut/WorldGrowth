package com.lichenaut.worldgrowth.event.player;

import com.lichenaut.worldgrowth.event.WGPointEvent;
import lombok.Data;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.util.HashSet;
import java.util.Set;

@Data
public class PlayerStatisticIncrement implements WGPointEvent<PlayerStatisticIncrementEvent> {

    private final Set<Statistic> statistics = new HashSet<>();
    private final int quota;
    private final int pointValue;
    private int count;

    public PlayerStatisticIncrement(int quota, int pointValue) {
        this.quota = quota;
        this.pointValue = pointValue;
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
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(PlayerStatisticIncrementEvent event) { if (statistics.contains(event.getStatistic())) count++; }
}
