package com.lichenaut.worldgrowth.event.entity;

import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityToggleGlideEvent;

public class EntityToggleGlide extends WGPointEvent<EntityToggleGlideEvent> {

    public EntityToggleGlide(WGDBManager databaseManager, Logger logging, int quota, int points) { super(databaseManager, logging, quota, points); }

    @Override
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    protected void onEvent(EntityToggleGlideEvent event) { incrementCount(); }
}