package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.util.WGVarDeSerializer;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

@RequiredArgsConstructor
public class WGAutosaver extends BukkitRunnable {

    private final Main main;

    @Override
    public void run() {
        main.getBorderManager().addRunnable(this, 36000L);
        WGVarDeSerializer varDeSerializer = main.getVarDeSerializer();
        try {
            varDeSerializer.serializeVariables();
            varDeSerializer.serializeRunnableQueues();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
