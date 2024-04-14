package com.lichenaut.worldgrowth.event;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Objects;

@RequiredArgsConstructor
public class WGMocker implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onSuffocate(PlayerDeathEvent event) {
        if (Objects.requireNonNull(event.getDeathMessage()).contains("left the confines of this world")) {
            String displayName = event.getEntity().getDisplayName();
            event.setDeathMessage(displayName + " left the confines of a world... or did the confines of a world leave " + displayName + "?");
        }
    }
}
