package com.lichenaut.worldgrowth.event.block;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SpongeAbsorbEvent;

public class SpongeAbsorb extends WGPointEvent<SpongeAbsorbEvent> {

    public SpongeAbsorb(Main plugin, WGDBManager databaseManager, Logger logging, int quota, int points) { super(plugin, databaseManager, logging, quota, points); }

    @Override
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    protected void onEvent(SpongeAbsorbEvent event) { count++; }
}
