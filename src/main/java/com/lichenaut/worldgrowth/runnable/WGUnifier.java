package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.util.WGMessager;
import com.lichenaut.worldgrowth.world.WGWorld;
import com.lichenaut.worldgrowth.world.WGWorldMath;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public abstract class WGUnifier extends BukkitRunnable {

    private final Main main;
    private final long timeStarted = System.currentTimeMillis();

    public void runUnification(long delay, boolean instant) {
        WGWorldMath worldMath = main.getWorldMath();
        Set<WGWorld> wgWorlds = worldMath.getWorlds();
        int biggestWorldSize = main.getServer().getWorlds().stream().mapToInt(world -> (int) world.getWorldBorder().getSize()).max().orElseThrow();
        long unificationPacing = instant ? 0L : 60L;
        for (WGWorld wgWorld : wgWorlds) {
            int unificationSize = Math.min(biggestWorldSize, wgWorld.maxSize());
            WorldBorder worldBorder = Objects.requireNonNull(main.getServer().getWorld(wgWorld.name())).getWorldBorder();
            main.getScheduler()
                    .runTask(main, () ->
                            worldBorder.setSize(unificationSize, unificationPacing));
        }

        main.getBossBar().unificationIndicator();
        WGMessager messager = main.getMessager();
        messager.spreadMsg(
                true,
                messager.concatArrays(
                        messager.combineMessage(messager.getUnificationOccurred1(), String.valueOf(biggestWorldSize)),
                        messager.combineMessage(messager.getUnificationOccurred2(), String.format("%.2f", (double) delay / 1200)),
                        messager.getUnificationOccurred3()),
                true);

        warnDeunification(messager, delay, 5);
        warnDeunification(messager, delay, 1);
    }

    public void runReset() {
        WGWorldMath worldMath = main.getWorldMath();
        Set<WGWorld> wgWorlds = worldMath.getWorlds();
        for (WGWorld wgWorld : wgWorlds) {
            int naturalSize = worldMath.getNaturalSize(wgWorld);
            WorldBorder worldBorder = Objects.requireNonNull(main.getServer().getWorld(wgWorld.name())).getWorldBorder();
            main.getScheduler()
                    .runTask(main, () ->
                            worldBorder.setSize(naturalSize, 60L));
        }

        main.getBossBar().deunificationIndicator();
        WGMessager messager = main.getMessager();
        messager.spreadMsg(
                true,
                messager.getDeunificationOccurred(),
                true);
    }

    private void warnDeunification(WGMessager messager, long delay, int minutes) {
        if (delay < minutes*1200L) return;

        Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> {
            BaseComponent[] concatMessage = messager.combineMessage(messager.getDeunificationWarning1(), String.valueOf(minutes));

            if (minutes == 1) {
                concatMessage = messager.concatArrays(
                        concatMessage,
                        messager.getDeunificationWarning3());
            } else {
                concatMessage = messager.concatArrays(
                        concatMessage,
                        messager.getDeunificationWarning2());
            }

            if (main.getUnificationManager().getRunnableQueue().size() > 1) {
                concatMessage = messager.concatArrays(
                        concatMessage,
                        messager.getUnificationQueued());
            }

            messager.spreadMsg(
                    true,
                    concatMessage,
                    true);
        }, delay - (minutes*1200L));
    }

    private boolean isOutsideNewBorder(Location playerLocation, Location worldBorderCenter, double worldBorderSize) {
        double dx = playerLocation.getX() - worldBorderCenter.getX();
        double dz = playerLocation.getZ() - worldBorderCenter.getZ();
        return dx * dx + dz * dz > worldBorderSize * worldBorderSize / 4.0;
    }
}
