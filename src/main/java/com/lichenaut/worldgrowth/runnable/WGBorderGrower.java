package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.world.WGWorld;
import com.lichenaut.worldgrowth.world.WGWorldMath;
import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.WorldBorder;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

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

        if (main.getVoteMath().unificationThresholdMet()) { //Unification event chosen.
            long delay = borderQuota * main.getConfiguration().getLong("ticks-per-point");
            WGRunnableManager unificationManager = main.getUnificationManager();
            unificationManager.addRunnable(new WGUnifier(main) {
                @Override
                public void run () {
                    runUnification(delay, false);
                }
            }, 0L);
            unificationManager.addRunnable(new WGUnifier(main) {
                @Override
                public void run () {
                    runReset();
                }
            }, delay);
        } else { //Usual border growth chosen.
            main.addBlocksGrownThisHour(growthSize * worldMath.getMainWorld().growthMultiplier());
            main.addBorderQuota(main.getConfiguration().getInt("increment-growth-quota-by"));

            for (WGWorld wgWorld : worldMath.getWorlds()) {
                WorldBorder worldBorder = Objects.requireNonNull(server.getWorld(wgWorld.name())).getWorldBorder();
                int newSize = worldMath.getNaturalSize(wgWorld);
                main.getScheduler()
                        .runTask(main, () ->
                                worldBorder.setSize(newSize, //Scale rate of change to size change.
                                        newSize - (int) worldBorder.getSize() / 2));
            }
        }

        main.getEventCounterManager().addRunnable(this, 6000L);
    }
}
