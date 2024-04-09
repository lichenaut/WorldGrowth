package com.lichenaut.worldgrowth.event.block;

import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.FluidLevelChangeEvent;

public class FluidLevelChange extends WGPointEvent<FluidLevelChangeEvent> {

    public FluidLevelChange(WGDBManager databaseManager, Logger logging, int quota, int points) { super(databaseManager, logging, quota, points); }

    @Override
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    protected void onEvent(FluidLevelChangeEvent event) { incrementCount(); }
}
