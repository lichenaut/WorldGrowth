package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.util.WGMessager;
import com.lichenaut.worldgrowth.world.WGWorld;
import com.lichenaut.worldgrowth.world.WGWorldMath;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
        int biggestGrowthMultiplier = wgWorlds.stream().mapToInt(WGWorld::growthMultiplier).max().orElseThrow();
        long unificationPace = 0L; //Scale rate of change to size change if not from a deserialized runnable.
        for (WGWorld wgWorld : wgWorlds) {
            int unificationSize = Math.min(biggestWorldSize, wgWorld.maxSize());
            WorldBorder worldBorder = Objects.requireNonNull(main.getServer().getWorld(wgWorld.name())).getWorldBorder();
            if (!instant) unificationPace = (long) (unificationSize - worldBorder.getSize() * wgWorld.growthMultiplier()) / biggestGrowthMultiplier;
            long finalUnificationPace = unificationPace;
            main.getScheduler()
                    .runTask(main, () ->
                            worldBorder.setSize(unificationSize, finalUnificationPace));
        }

        WGMessager messager = main.getMessager();
        //TODO messaging
    }

    public void runReset() {
        WGWorldMath worldMath = main.getWorldMath();
        Set<WGWorld> wgWorlds = worldMath.getWorlds();
        int biggestGrowthMultiplier = wgWorlds.stream().mapToInt(WGWorld::growthMultiplier).max().orElseThrow();
        for (WGWorld wgWorld : wgWorlds) {
            int naturalSize = worldMath.getNaturalSize(wgWorld);
            WorldBorder worldBorder = Objects.requireNonNull(main.getServer().getWorld(wgWorld.name())).getWorldBorder();
            main.getScheduler()
                    .runTask(main, () ->
                            worldBorder.setSize(naturalSize, //Scale rate of change to size change.
                                    (long) (worldBorder.getSize() - (long) naturalSize * wgWorld.growthMultiplier()) /
                                            biggestGrowthMultiplier));
        }

        WGMessager messager = main.getMessager();
        //TODO messaging
    }
}
