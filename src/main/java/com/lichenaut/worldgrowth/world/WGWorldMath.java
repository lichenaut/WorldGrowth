package com.lichenaut.worldgrowth.world;

import com.lichenaut.worldgrowth.Main;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

record WGWorld(boolean isMain, String name, int startSize, int maxSize, int growthMultiplier) {}

@Getter
public class WGWorldMath {

    private final Main main;
    private final Configuration configuration;
    private final Server server;
    private final Set<WGWorld> worlds = new HashSet<>();
    private final WGWorld mainWorld;

    public WGWorldMath(Main main, Configuration configuration) {
        this.main = main;
        this.configuration = configuration;
        server = main.getServer();
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

        mainWorld = worlds.stream().filter(WGWorld::isMain).findFirst().orElseThrow();
    }

    public boolean willTopMaxGrowthPerHour() {
        return
                main.getBlocksGrownThisHour() +
                (configuration.getInt("growth-size") * mainWorld.growthMultiplier()) >
                main.getConfiguration().getInt("max-block-growth-per-hour");
    }

    public String getMainWorldName() {
        return mainWorld.name();
    }

    public int getMainWorldBorderStartSize() {
        return mainWorld.startSize();
    }

    public Location getSpawn(String worldName) {
        return Objects.requireNonNull(server.getWorld(worldName)).getSpawnLocation();
    }
}
