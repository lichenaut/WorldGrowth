package com.lichenaut.worldgrowth.event.player;

import com.lichenaut.worldgrowth.event.WGPointEvent;
import lombok.Data;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

@Data
public class PlayerQuit implements WGPointEvent<PlayerQuitEvent> {

    private final int quota;
    private final int pointValue;
    private int count;

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(PlayerQuitEvent event) { count++; }
}
