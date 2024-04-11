package com.lichenaut.worldgrowth.event.block;

import com.lichenaut.worldgrowth.event.WGPointEvent;
import lombok.Data;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFromToEvent;

@Data
public class BlockFromTo implements WGPointEvent<BlockFromToEvent> {

    private final int quota;
    private final int pointValue;
    private int count;

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(BlockFromToEvent event) { count++; }
}
