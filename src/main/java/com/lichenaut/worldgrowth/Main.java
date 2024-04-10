package com.lichenaut.worldgrowth;

import com.lichenaut.worldgrowth.cmd.WGCommand;
import com.lichenaut.worldgrowth.cmd.WGTabCompleter;
import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.db.WGMySQLManager;
import com.lichenaut.worldgrowth.db.WGSQLiteManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import com.lichenaut.worldgrowth.runnable.WGBorderGrower;
import com.lichenaut.worldgrowth.runnable.WGEventCounter;
import com.lichenaut.worldgrowth.runnable.WGRunnableManager;
import com.lichenaut.worldgrowth.util.WGCopier;
import com.lichenaut.worldgrowth.util.WGMessager;
import com.lichenaut.worldgrowth.util.WGRegisterer;
import com.lichenaut.worldgrowth.util.WGVarDeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.Configuration;
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

    private final Logger logging = LogManager.getLogger("WorldGrowth");
    private final PluginManager pluginManager = getServer().getPluginManager();
    private final String separator = FileSystems.getDefault().getSeparator();
    private final WGMessager messager = new WGMessager(this);
    private final WGRunnableManager eventCounterManager = new WGRunnableManager(this);
    private WGRunnableManager growthCounterManager = new WGRunnableManager(this);
    private final WGRunnableManager borderManager = new WGRunnableManager(this);
    private final Set<WGPointEvent<?>> pointEvents = new HashSet<>();
    private int borderQuota;
    private int maxBorderQuota;
    private int points;
    private int borderSize;
    private int blocksGrownThisHour; //TODO: manager resets this value every hour
    private int boostMultiplier;
    private WGRunnableManager boostManager = new WGRunnableManager(this);
    private PluginCommand wgCommand;
    private Configuration configuration;
    private WGDBManager databaseManager;
    private WGVarDeSerializer varDeSerializer;

    @Override
    public void onEnable() {
        new MetricsLite(this, 21539);
        wgCommand = Objects.requireNonNull(getCommand("wg"));
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        reloadWG();
    }

    public void reloadWG() {
        HandlerList.unregisterAll(this);
        wgCommand.setExecutor(null);
        wgCommand.setTabCompleter(null);
        pointEvents.clear();

        reloadConfig();
        configuration = getConfig();
        if (configuration.getBoolean("disable-plugin")) {
            logging.info("Plugin is disabled in config.yml. Disabling WorldGrowth.");
            pluginManager.disablePlugin(this);
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

        wgCommand.setExecutor(new WGCommand(this, messager));
        wgCommand.setTabCompleter(new WGTabCompleter());

        String url = configuration.getString("database-url");
        String user = configuration.getString("database-user");
        String password = configuration.getString("database-password");
        int maxPoolSize = configuration.getInt("database-max-pool-size");
        String finalUrl;
        if (url != null && user != null && password != null) {
            logging.info("Database information given in config.yml. Using a remote database.");
            if (databaseManager == null || databaseManager instanceof WGSQLiteManager) databaseManager = new WGMySQLManager(this, configuration, messager);
            finalUrl = "jdbc:mysql://" + url;
        } else {
            logging.info("Database information not given in config.yml. Using a local database.");
            String outFilePath = getDataFolder().getPath() + separator + "worldgrowth.db";
            try {
                WGCopier.smallCopy(getResource("worldgrowth.db"), outFilePath);
            } catch (IOException e) {
                logging.error("Error while creating local database file!");
                throw new RuntimeException(e);
            }
            if (databaseManager == null || databaseManager instanceof WGMySQLManager) databaseManager = new WGSQLiteManager(this, configuration, messager);
            finalUrl = "jdbc:sqlite:" + outFilePath;
        }

        varDeSerializer = new WGVarDeSerializer(this, logging, pointEvents, eventCounterManager, databaseManager);
        CompletableFuture
                .runAsync(() -> databaseManager.initializeDataSource(finalUrl, user, password, maxPoolSize))
                .thenAcceptAsync(connected -> {
                    try {
                        databaseManager.createStructure();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .thenAcceptAsync(structured -> {
                    maxBorderQuota = configuration.getInt("max-growth-quota");
                    try {
                        varDeSerializer.deserializePointsQuota();
                        databaseManager.deserializeRunnableQueue(boostManager, "SELECT `multiplier`, `delay` FROM `boosts`", "boosts");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .thenAcceptAsync(deserialized -> {
                    try {
                        new WGRegisterer(this, configuration, databaseManager, pluginManager, varDeSerializer, pointEvents).registerEvents();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .thenAcceptAsync(registered -> {
                    eventCounterManager.addRunnable(new WGEventCounter(this), 200L);
                    WGBorderGrower borderGrower = new WGBorderGrower(this);
                    borderGrower.buildWorlds();
                    borderManager.addRunnable(borderGrower, 400L);
                })
                .thenAcceptAsync(queued -> logging.info("Database connection up and running."))
                .exceptionallyAsync(e -> {
                    logging.error("Error during the database connecting, structuring, deserializing, registering, and queueing process!");
                    logging.error(e);
                    return null;
                });
    }

    @Override
    public void onDisable() {
        if (databaseManager == null) return;

        try {
            varDeSerializer.serializeCountsQuotaPoints();
            databaseManager.serializeRunnableQueue(boostManager, "INSERT INTO `boosts` (`multiplier`, `delay`) VALUES (?, ?)");
        } catch (SQLException e) {
            logging.error("Error while serializing boost queue!");
            throw new RuntimeException(e);
        } finally {
            databaseManager.closeDataSource();
        }
    }

    public void addPoints(int pointsToAdd) { points += pointsToAdd; }

    public void addBlocksGrownThisHour(int blocksToAdd) { blocksGrownThisHour += blocksToAdd; }

    public void addBorderQuota(int quotaToAdd) {
        if (borderQuota + quotaToAdd > maxBorderQuota) return;

        borderQuota += quotaToAdd;
    }
}
