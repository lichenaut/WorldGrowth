package com.lichenaut.worldgrowth.event.player;

import com.lichenaut.worldgrowth.event.WGPointEvent;
import lombok.Data;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRiptideEvent;

@Data
public class PlayerRiptide implements WGPointEvent<PlayerRiptideEvent> {

    private final int quota;
    private final int pointValue;
    private int count;

    @Override
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(PlayerRiptideEvent event) { count++; }
}
