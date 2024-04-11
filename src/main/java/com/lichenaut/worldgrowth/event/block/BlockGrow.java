package com.lichenaut.worldgrowth.event.block;

import com.lichenaut.worldgrowth.event.WGPointEvent;
import lombok.Data;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockGrowEvent;

@Data
public class BlockGrow implements WGPointEvent<BlockGrowEvent> {

    private final int quota;
    private final int pointValue;
    private int count;

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(BlockGrowEvent event) { count++; }
}
