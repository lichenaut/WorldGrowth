package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

record WGWorld(boolean isMain, String name, int startSize, int maxSize, int growthMultiplier) {}

@RequiredArgsConstructor
public class WGBorderGrower extends BukkitRunnable {

    private final Main main;
    private final Set<WGWorld> worlds = new HashSet<>();

    public void buildWorlds() {
        ConfigurationSection worldsSection = main.getConfiguration().getConfigurationSection("worlds");
        if (worldsSection == null) throw new IllegalArgumentException("No worlds found in configuration!"); //TODO: test this functionality

        for (String worldKey : worldsSection.getKeys(false)) {
            ConfigurationSection worldSection = worldsSection.getConfigurationSection(worldKey);
            if (worldSection == null) throw new IllegalArgumentException("No configuration found for world " + worldKey + "!");

            worlds.add(new WGWorld(
                    worldSection.getBoolean("main", false),
                    worldSection.getString("name"),
                    worldSection.getInt("start-size"),
                    worldSection.getInt("max-size"),
                    worldSection.getInt("growth-multiplier")));
        }
    }

    @Override
    public void run() { //TODO: make all async
        int points = main.getPoints();
        int borderQuota = main.getBorderQuota();
        if (points < borderQuota) return;

        WGWorld mainWorld = worlds.stream().filter(WGWorld::isMain).findFirst().orElseThrow();
        Configuration configuration = main.getConfiguration();
        int blocksGrownThisHour = main.getBlocksGrownThisHour();
        int growthSize = configuration.getInt("growth-size");
        if (blocksGrownThisHour + (growthSize * mainWorld.growthMultiplier()) > configuration.getInt("max-block-growth-per-hour")) {
            //TODO: turn off event counting by setting a variable that gets reset every hour
            return;
        }

        main.addBlocksGrownThisHour(growthSize);
        main.addBorderQuota(configuration.getInt("increment-growth-quota-by"));

        Server server = main.getServer();
        for (WGWorld wgWorld : worlds) { //TODO: set border properties on startup, not even on reload
            String worldName = wgWorld.name();
            World world = server.getWorld(worldName);
            if (world == null) throw new IllegalArgumentException("World " + worldName + " not found!");

            WorldBorder worldBorder = world.getWorldBorder();
            //TODO
        }
    }
}
