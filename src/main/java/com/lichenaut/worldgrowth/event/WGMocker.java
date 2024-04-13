package com.lichenaut.worldgrowth.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Objects;

public class WGMocker implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onSuffocate(PlayerDeathEvent e) {
        if (Objects.requireNonNull(e.getDeathMessage()).contains("left the confines of this world")) {
            e.setDeathMessage(e.getEntity().getDisplayName() + " left the confines of a world... or did the confines of a world leave?");
        }
    }
}
