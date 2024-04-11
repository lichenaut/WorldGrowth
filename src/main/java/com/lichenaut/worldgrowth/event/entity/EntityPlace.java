package com.lichenaut.worldgrowth.event.entity;

import com.lichenaut.worldgrowth.event.WGPointEvent;
import lombok.Data;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPlaceEvent;

@Data
public class EntityPlace implements WGPointEvent<EntityPlaceEvent> {

    private final int quota;
    private final int pointValue;
    private int count;

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(EntityPlaceEvent event) { count++; }
}
