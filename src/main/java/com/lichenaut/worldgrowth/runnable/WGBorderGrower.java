package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.world.WGWorld;
import com.lichenaut.worldgrowth.world.WGWorldMath;
import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class WGBorderGrower extends BukkitRunnable {

    private final Main main;
    private final Server server;

    public WGBorderGrower(Main main) {
        this.main = main;
        server = main.getServer();
    }

    @Override
    public void run() {
        WGWorldMath worldMath = main.getWorldMath();
        if (worldMath.willTopMaxGrowthPerHour()) return;

        double points = main.getPoints();
        int borderQuota = main.getBorderQuota();
        if (points < borderQuota) return;

        int growthSize = main.getConfiguration().getInt("growth-size");
        main.subtractPoints(borderQuota);
        main.addBlocksGrownThisHour(growthSize * worldMath.getMainWorld().growthMultiplier());
        main.addBorderQuota(main.getConfiguration().getInt("increment-growth-quota-by"));

        for (WGWorld wgWorld : worldMath.getWorlds()) {
            String worldName = wgWorld.name();
            World world = server.getWorld(worldName);
            if (world == null) throw new IllegalArgumentException("World " + worldName + " not found!");

            WorldBorder worldBorder = world.getWorldBorder();
            main.getScheduler()
                    .runTask(main, () ->
                            worldBorder.setSize(worldBorder.getSize() + (growthSize * wgWorld.growthMultiplier()),
                                    16L));
        }

        main.getEventCounterManager().addRunnable(this, 6000L);
    }
}
