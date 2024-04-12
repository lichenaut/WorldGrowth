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
    public void run() { //TODO: make center of border the intersection of 4 chunks
        /*if (main.getWorldMath().willTopMaxGrowthPerHour()) return;

        int points = main.getPoints();
        int borderQuota = main.getBorderQuota();
        if (points < borderQuota) return;

        int growthSize = main.getConfiguration().getInt("growth-size");
        int mainWorldGrowthMultiplier = main.getWorldMath().getMainWorld().growthMultiplier();
        main.addBlocksGrownThisHour(growthSize * mainWorldGrowthMultiplier);
        main.addBorderQuota(main.getConfiguration().getInt("increment-growth-quota-by"));

        for (WGWorld wgWorld : worlds) { //TODO: set border properties on startup, not even on reload
            String worldName = wgWorld.name();
            World world = server.getWorld(worldName);
            if (world == null) throw new IllegalArgumentException("World " + worldName + " not found!");

            WorldBorder worldBorder = world.getWorldBorder();
            //TODO
        }*/
    }
}
