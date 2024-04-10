package com.lichenaut.worldgrowth.event.entity;

import com.lichenaut.worldgrowth.event.WGPointEvent;
import lombok.Data;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;

@Data
public class VillagerAcquireTrade implements WGPointEvent<VillagerAcquireTradeEvent> {

    private final int quota;
    private final int pointValue;
    private int count;

    @Override
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEvent(VillagerAcquireTradeEvent event) { count++; }
}
