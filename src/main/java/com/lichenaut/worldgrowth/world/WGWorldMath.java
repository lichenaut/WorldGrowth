package com.lichenaut.worldgrowth.world;

import com.lichenaut.worldgrowth.Main;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

            Location borderCenter;
            String coords = worldSection.getString("border-center", null);
            if (coords == null) {
                borderCenter = null;
            } else {
                String[] splitCoords = coords.split(" ");
                borderCenter = new Location(
                        server.getWorld(worldSection.getString("name")),
                        Double.parseDouble(splitCoords[0]),
                        Double.parseDouble(splitCoords[1]),
                        Double.parseDouble(splitCoords[2]));
            }

            worlds.add(new WGWorld(
                    worldSection.getBoolean("main", false),
                    worldSection.getString("name"),
                    borderCenter,
                    worldSection.getInt("start-size"),
                    worldSection.getInt("max-size"),
                    worldSection.getInt("growth-multiplier")));
        }

        mainWorld = worlds.stream().filter(WGWorld::isMain).findFirst().orElseThrow();
    }

    public void setBorders() {
        for (WGWorld wgWorld : worlds) {
            String worldName = wgWorld.name();
            WorldBorder worldBorder = Objects.requireNonNull(server.getWorld(worldName)).getWorldBorder();

            worldBorder.setWarningDistance(0);
            worldBorder.setWarningTime(-1);

            worldBorder.setCenter( //Set a world's border from either config, or the world's spawn location.
                    Objects.requireNonNullElseGet(wgWorld.borderCenter(),
                            () -> Objects.requireNonNull(server.getWorld(worldName)).getSpawnLocation()));

            worldBorder.setSize( //Set a world's border to its start size plus the number of growths times the world's growth multiplier.
                    wgWorld.startSize() +
                            (((main.getBorderQuota() - (double) configuration.getInt("starting-growth-quota")) /
                                    configuration.getInt("increment-growth-quota-by")) *
                                    wgWorld.growthMultiplier()));
        }
    }

    public boolean willTopMaxGrowthPerHour() {
        return
                main.getBlocksGrownThisHour() +
                (configuration.getInt("growth-size") * mainWorld.growthMultiplier()) >
                main.getConfiguration().getInt("max-block-growth-per-hour");
    }
}

