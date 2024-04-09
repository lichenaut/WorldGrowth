package com.lichenaut.worldgrowth.event.block;

import com.lichenaut.worldgrowth.event.WGPointEvent;
import lombok.Data;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BellRingEvent;

@Data
public class BellRing implements WGPointEvent<BellRingEvent> {

    private final int quota;
    private final int pointValue;
    private int count = 0;

    @Override
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEvent(BellRingEvent event) { count++; }
}
