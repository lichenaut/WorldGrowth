package com.lichenaut.worldgrowth;

import com.lichenaut.worldgrowth.cmd.WGCommand;
import com.lichenaut.worldgrowth.cmd.WGTabCompleter;
import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.db.WGMySQLManager;
import com.lichenaut.worldgrowth.db.WGSQLiteManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
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

    private final Main plugin = this;
    private final Logger logging = LogManager.getLogger("WorldGrowth");
    private final WGMessager messager = new WGMessager(this);
    private final PluginManager pluginManager = getServer().getPluginManager();
    private final String separator = FileSystems.getDefault().getSeparator();
    private final Set<WGPointEvent<?>> pointEvents = new HashSet<>();
    private int borderQuota;
    private int points;
    private final WGRunnableManager counterManager = new WGRunnableManager(this);
    private int boostMultiplier = 1;
    private WGRunnableManager boostManager = new WGRunnableManager(this);
    private Configuration configuration;
    private WGDBManager databaseManager;
    private WGVarDeSerializer varDeSerializer;

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
            try {
                WGCopier.smallCopy(getResource("worldgrowth.db"), getDataFolder().getPath() + separator + "worldgrowth.db");
            } catch (IOException e) {
                logging.error("Error while creating local database file!");
                throw new RuntimeException(e);
            }
            if (databaseManager == null || databaseManager instanceof WGMySQLManager) databaseManager = new WGSQLiteManager(this, configuration, messager);
            finalUrl = "jdbc:sqlite:" + getDataFolder().getPath() + separator + "worldgrowth.db";
        }
        varDeSerializer = new WGVarDeSerializer(this, logging, pointEvents, counterManager, databaseManager);

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
                .thenAcceptAsync(registered ->
                        counterManager.addRunnable(new WGEventCounter(this, logging, pointEvents, counterManager), 1000L))
                .thenAcceptAsync(queued -> logging.info("Database connection up and running."))
                .exceptionallyAsync(e -> {
                    logging.error("Error during the database connecting, structuring, deserializing, queueing, and registering process!");
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
}
