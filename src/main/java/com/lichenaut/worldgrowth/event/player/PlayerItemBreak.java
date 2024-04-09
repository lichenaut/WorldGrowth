package com.lichenaut.worldgrowth.event.player;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemBreakEvent;

public class PlayerItemBreak extends WGPointEvent<PlayerItemBreakEvent> {

    public PlayerItemBreak(Main main, WGDBManager databaseManager, Logger logging, int quota, int points) { super(main, databaseManager, logging, quota, points); }

    @Override
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    protected void onEvent(PlayerItemBreakEvent event) { count++; }
}
