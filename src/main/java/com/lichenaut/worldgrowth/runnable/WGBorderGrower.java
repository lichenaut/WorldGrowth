package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@RequiredArgsConstructor
public class WGBorderGrower extends BukkitRunnable {

    private final Main main;

    @Override
    public void run() { //TODO: async
        /*if (main.getWorldMath().willTopMaxGrowthPerHour()) return;

        int points = main.getPoints();
        int borderQuota = main.getBorderQuota();
        if (points < borderQuota) return;

        int growthSize = main.getConfiguration().getInt("growth-size");
        main.addBlocksGrownThisHour(growthSize);
        main.addBorderQuota(configuration.getInt("increment-growth-quota-by"));

        for (WGWorld wgWorld : worlds) { //TODO: set border properties on startup, not even on reload
            String worldName = wgWorld.name();
            World world = server.getWorld(worldName);
            if (world == null) throw new IllegalArgumentException("World " + worldName + " not found!");

            WorldBorder worldBorder = world.getWorldBorder();
            //TODO
        }*/
    }
}
