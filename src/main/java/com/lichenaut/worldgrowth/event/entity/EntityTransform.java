package com.lichenaut.worldgrowth.event.entity;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTransformEvent;

public class EntityTransform extends WGPointEvent<EntityTransformEvent> {

    public EntityTransform(Main plugin, WGDBManager databaseManager, Logger logging, int quota, int points) { super(plugin, databaseManager, logging, quota, points); }

    @Override
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    protected void onEvent(EntityTransformEvent event) { count++; }
}
