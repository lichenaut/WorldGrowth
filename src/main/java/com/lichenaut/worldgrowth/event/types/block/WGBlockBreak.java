package com.lichenaut.worldgrowth.event.types.block;

import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

public class WGBlockBreak extends WGPointEvent<BlockBreakEvent> {

    public WGBlockBreak(WGDBManager databaseManager, Logger logging, int quota, int points) {super(databaseManager, logging, quota, points);}

    @Override
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    protected void onEvent(BlockBreakEvent event) {incrementCount();}
}
