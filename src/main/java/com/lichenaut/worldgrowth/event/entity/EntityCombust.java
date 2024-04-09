package com.lichenaut.worldgrowth.event.entity;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustEvent;

public class EntityCombust extends WGPointEvent<EntityCombustEvent> {

    public EntityCombust(Main plugin, WGDBManager databaseManager, Logger logging, int quota, int points) { super(plugin, databaseManager, logging, quota, points); }

    @Override
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    protected void onEvent(EntityCombustEvent event) { count++; }
}
