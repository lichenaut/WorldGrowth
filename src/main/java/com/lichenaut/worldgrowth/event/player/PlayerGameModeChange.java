package com.lichenaut.worldgrowth.event.player;

import com.lichenaut.worldgrowth.event.WGPointEvent;
import lombok.Data;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

@Data
public class PlayerGameModeChange implements WGPointEvent<PlayerGameModeChangeEvent> {

    private final int quota;
    private final int pointValue;
    private int count;

    @Override
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEvent(PlayerGameModeChangeEvent event) { count++; }
}
