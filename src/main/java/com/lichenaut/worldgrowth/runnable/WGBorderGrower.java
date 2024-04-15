package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.util.WGMessager;
import com.lichenaut.worldgrowth.world.WGWorld;
import com.lichenaut.worldgrowth.world.WGWorldMath;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Server;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.Configuration;
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
        main.getBorderManager().addRunnable(this, 6000L);
        WGWorldMath worldMath = main.getWorldMath();
        if (worldMath.willTopMaxGrowthPerHour()) return;

        double points = main.getPoints();
        int borderQuota = main.getBorderQuota();
        if (points < borderQuota) return;

        Configuration configuration = main.getConfiguration();
        int growthSize = configuration.getInt("growth-size");
        main.subtractPoints(borderQuota);

        WGMessager messager = main.getMessager();
        if (main.getVoteMath().unificationThresholdMet()) { //Unification event chosen.
            long delay = borderQuota * configuration.getLong("ticks-per-point");
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
            BaseComponent[] minutes;
            if (delay == 1200) {
                minutes = messager.getMinute();
            } else {
                minutes = messager.getMinutes();
            }
            messager.spreadMsg(
                    true,
                    messager.concatArrays(
                            messager.combineMessage(messager.getUnificationOccurred1(),
                                    String.valueOf(
                                            main.getServer().getWorlds().stream().mapToInt(world ->
                                                    (int) world.getWorldBorder().getSize()).max().orElseThrow())),
                            messager.combineMessage(messager.getUnificationOccurred2(), String.format("%.2f", (double) delay / 1200)),
                            messager.combineMessage(minutes, "!")),
                    true);
        } else { //Usual border growth chosen.
            int mainWorldGrowthSize = growthSize * worldMath.getMainWorld().growthMultiplier();
            main.addBlocksGrownThisHour(mainWorldGrowthSize);
            main.addBorderQuota(configuration.getInt("increment-growth-quota-by"));

            if (main.getUnificationManager().getRunnableQueue().isEmpty()) {
                for (WGWorld wgWorld : worldMath.getWorlds()) {
                    WorldBorder worldBorder = Objects.requireNonNull(server.getWorld(wgWorld.name())).getWorldBorder();
                    int newSize = worldMath.getNaturalSize(wgWorld);
                    main.getScheduler()
                            .runTask(main, () ->
                                    worldBorder.setSize(newSize,
                                            newSize - (int) worldBorder.getSize() / 2));
                }
                main.getBossBar().growthIndicator();
            }

            messager.spreadMsg(
                    true,
                    messager.concatArrays(
                            messager.combineMessage(messager.getGrowthOccurred1(), String.valueOf(worldMath.getNaturalSize(worldMath.getMainWorld()))),
                            messager.getGrowthOccurred2()),
                    true);
            if (worldMath.willTopMaxGrowthPerHour()) {
                messager.spreadMsg(
                        true,
                        main.getMessager().getPointsOff(),
                        true);
            }
        }
    }
}
