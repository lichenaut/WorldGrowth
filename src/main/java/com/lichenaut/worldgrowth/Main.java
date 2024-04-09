package com.lichenaut.worldgrowth;

import com.lichenaut.worldgrowth.cmd.WGCommand;
import com.lichenaut.worldgrowth.cmd.WGTabCompleter;
import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.db.WGMySQLManager;
import com.lichenaut.worldgrowth.db.WGSQLiteManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import com.lichenaut.worldgrowth.event.block.BellResonate;
import com.lichenaut.worldgrowth.runnable.WGEventChecker;
import com.lichenaut.worldgrowth.runnable.WGRunnableManager;
import com.lichenaut.worldgrowth.util.WGCopier;
import com.lichenaut.worldgrowth.util.WGMessager;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
@SuppressWarnings("unused")
public final class Main extends JavaPlugin {

    private final Main plugin = this;
    private final Logger logging = LogManager.getLogger("WorldGrowth");
    private final WGMessager messager = new WGMessager(this);
    private final String separator = FileSystems.getDefault().getSeparator();
    private final PluginManager pluginManager = getServer().getPluginManager();
    private final Set<WGPointEvent<?>> pointEvents = new HashSet<>();
    private int points = 0;
    WGRunnableManager pointManager = new WGRunnableManager(this);
    private int boostMultiplier = 1;
    private WGRunnableManager boostManager = new WGRunnableManager(this);
    private Configuration configuration;
    private WGDBManager databaseManager;

    @Override
    public void onEnable() {
        new MetricsLite(plugin, 21539);
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        reloadWG();
        Objects.requireNonNull(getCommand("wg")).setExecutor(new WGCommand(this, messager));
        Objects.requireNonNull(getCommand("wg")).setTabCompleter(new WGTabCompleter());
    }

    public void reloadWG() {
        HandlerList.unregisterAll(this);
        pointEvents.clear();

        reloadConfig();
        configuration = getConfig();
        if (configuration.getBoolean("disable-plugin")) {
            logging.info("Plugin is disabled in config.yml. Disabling WorldGrowth.");
            getServer().getPluginManager().disablePlugin(this);
        }

        String localesFolderString = getDataFolder().getPath() + separator + "locales";
        try {
            Path localesFolderPath = Path.of(localesFolderString);
            if (!Files.exists(localesFolderPath)) Files.createDirectory(localesFolderPath);
            String[] localeFiles = {separator + "de.properties", separator + "en.properties", separator + "es.properties", separator + "fr.properties"};
            for (String locale : localeFiles) WGCopier.smallCopy(getResource("locales" + locale), localesFolderString + locale);
        } catch (IOException e) {
            logging.error("Error while creating locale files.");
            throw new RuntimeException(e);
        }
        try {
            messager.loadLocaleMessages(localesFolderString);
        } catch (IOException e) {
            logging.error("Error while loading locale messages.");
            throw new RuntimeException(e);
        }

        String url = configuration.getString("database-url");
        String username = configuration.getString("database-username");
        String password = configuration.getString("database-password");
        int maxPoolSize = configuration.getInt("database-max-pool-size");
        String finalUrl;
        if (url != null && username != null && password != null) {
            logging.info("Database information given in config.yml. Using a remote database.");
            if (databaseManager == null || databaseManager instanceof WGSQLiteManager) databaseManager = new WGMySQLManager(this, messager);
            finalUrl = "jdbc:mysql://" + url;
        } else {
            logging.info("Database information not given in config.yml. Using a local database.");
            try {
                WGCopier.smallCopy(getResource("worldgrowth.db"), getDataFolder().getPath() + separator + "worldgrowth.db");
            } catch (IOException e) {
                logging.error("Error while creating local database file!");
                throw new RuntimeException(e);
            }
            if (databaseManager == null || databaseManager instanceof WGMySQLManager) databaseManager = new WGSQLiteManager(this, messager);
            finalUrl = "jdbc:sqlite:" + getDataFolder().getPath() + separator + "worldgrowth.db";
        }
        CompletableFuture
                .runAsync(() -> databaseManager.initializeDataSource(finalUrl, username, password, maxPoolSize))
                .thenComposeAsync(connected -> CompletableFuture.runAsync(() -> {
                    try {
                        databaseManager.createStructure();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .thenComposeAsync(structured -> CompletableFuture.runAsync(() ->
                        pointManager.addRunnable(new WGEventChecker(this, logging, pointEvents, pointManager), 1000L)))
                .thenComposeAsync(queued -> CompletableFuture.runAsync(() -> {
                    try {
                        databaseManager.deserializeRunnableQueue(boostManager, "SELECT `multiplier`, `delay` FROM `boosts`", "boosts");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .thenAcceptAsync(deserialized -> logging.info("Database connection up and running."))
                .exceptionallyAsync(e -> {
                    logging.error("Error during the database connecting, structuring, queueing, and deserializing process!");
                    logging.error(e);
                    return null;
                });

        ConfigurationSection events = configuration.getConfigurationSection("events");
        if (databaseManager != null && events != null) {
            for (String event : events.getKeys(false)) {
                ConfigurationSection eventSection = events.getConfigurationSection(event);
                if (eventSection == null) continue;

                int quota = eventSection.getInt("quota");
                if (quota < 1) continue;

                int pointValue = eventSection.getInt("points");
                switch (event) {
                    case "bell-resonate":
                        BellResonate bellResonate = new BellResonate(quota, pointValue);
                        pluginManager.registerEvents(bellResonate, this);
                        pointEvents.add(bellResonate);
                        break;
                    /*case "bell-ring":
                        BellRing bellRing = new BellRing(quota, pointValue);
                        pluginManager.registerEvents(bellRing, this);
                        pointEvents.add(bellRing);
                        break;
                    case "block-break":
                        BlockBreak blockBreak = new BlockBreak(quota, pointValue);
                        pluginManager.registerEvents(blockBreak, this);
                        pointEvents.add(blockBreak);
                        break;*/
                    //make event listener registerer class
                }
            }
        }
    }

    @Override
    public void onDisable() {
        if (databaseManager == null) return;

        try {
            databaseManager.serializeRunnableQueue(boostManager, "INSERT INTO `boosts` (`multiplier`, `delay`) VALUES (?, ?)");
        } catch (SQLException e) {
            logging.error("Error while serializing boost queue!");
            throw new RuntimeException(e);
        } finally {
            databaseManager.closeDataSource();
        }
    }

    public void addPoints(int pointsToAdd) { points += pointsToAdd; }
}
