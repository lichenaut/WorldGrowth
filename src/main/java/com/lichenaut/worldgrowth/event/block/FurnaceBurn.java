package com.lichenaut.worldgrowth.event.block;

import com.lichenaut.worldgrowth.event.WGPointEvent;
import lombok.Data;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.FurnaceBurnEvent;

@Data
public class FurnaceBurn implements WGPointEvent<FurnaceBurnEvent> {

    private final int quota;
    private final int pointValue;
    private int count;

    @Override
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEvent(FurnaceBurnEvent event) { count++; }
}
