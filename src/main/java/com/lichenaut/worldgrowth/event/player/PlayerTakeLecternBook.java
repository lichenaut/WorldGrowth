package com.lichenaut.worldgrowth.event.player;

import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;

public class PlayerTakeLecternBook extends WGPointEvent<PlayerTakeLecternBookEvent> {

    public PlayerTakeLecternBook(WGDBManager databaseManager, Logger logging, int quota, int points) { super(databaseManager, logging, quota, points); }

    @Override
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    protected void onEvent(PlayerTakeLecternBookEvent event) { incrementCount(); }
}