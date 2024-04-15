package com.lichenaut.worldgrowth;

import com.lichenaut.worldgrowth.cmd.WGCommand;
import com.lichenaut.worldgrowth.cmd.WGTabCompleter;
import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.db.WGMySQLManager;
import com.lichenaut.worldgrowth.db.WGSQLiteManager;
import com.lichenaut.worldgrowth.event.WGKicker;
import com.lichenaut.worldgrowth.event.WGMocker;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import com.lichenaut.worldgrowth.runnable.*;
import com.lichenaut.worldgrowth.util.*;
import com.lichenaut.worldgrowth.vote.WGVoteMath;
import com.lichenaut.worldgrowth.world.WGWorldMath;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

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

    private final PluginManager pluginManager = getServer().getPluginManager();
    private final Logger logging = LogManager.getLogger("WorldGrowth");
    private final String separator = FileSystems.getDefault().getSeparator();
    private final WGMessager messager = new WGMessager(this);
    private final BukkitScheduler scheduler = Bukkit.getScheduler();
    private final WGRunnableManager eventCounterManager = new WGRunnableManager(this);
    private final WGRunnableManager hourMaxManager = new WGRunnableManager(this);
    private final WGRunnableManager borderManager = new WGRunnableManager(this);
    private final WGRunnableManager unificationManager = new WGRunnableManager(this);
    private final WGRunnableManager autosaveManager = new WGRunnableManager(this);
    private final WGBossBar bossBar = new WGBossBar(this);
    private final Set<WGPointEvent<?>> pointEvents = new HashSet<>();
    private int borderQuota;
    private int maxBorderQuota;
    private int blocksGrownThisHour;
    private double points;
    private double boostMultiplier = 1.0;
    private WGRunnableManager boostManager = new WGRunnableManager(this);
    private PluginCommand wgCommand;
    private Configuration configuration;
    private CompletableFuture<Void> mainFuture = CompletableFuture.completedFuture(null);
    private WGDBManager databaseManager;
    private WGVarDeSerializer varDeSerializer;
    private WGWorldMath worldMath;
    private WGVoteMath voteMath;

    @Override
    public void onEnable() {
        WGKicker kicker = new WGKicker();
        pluginManager.registerEvents(kicker, this);

        new MetricsLite(this, 21539);

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        reloadWG();

        mainFuture = mainFuture
                .thenAcceptAsync(commandsSet -> {
                    try {
                        varDeSerializer.deserializeVariablesExceptCounts();
                        databaseManager.deserializeRunnableQueue(hourMaxManager, "SELECT `delay` FROM `hour`");
                        databaseManager.deserializeRunnableQueue(unificationManager, "SELECT `delay` FROM `unifications`");
                        databaseManager.deserializeRunnableQueue(boostManager, "SELECT `multiplier`, `delay` FROM `boosts`");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .exceptionallyAsync(e -> {
                    logging.error("Error while deserializing variables and runnable queues!");
                    disablePlugin(e);
                    return null;
                });

        mainFuture = mainFuture
                .thenAcceptAsync(deserialized -> {
                    if (hourMaxManager.getRunnableQueue().isEmpty()) hourMaxManager.addRunnable(new WGHourCounter(this), 0L);
                    eventCounterManager.addRunnable(new WGEventConverter(this), 200L);
                    borderManager.addRunnable(new WGBorderGrower(this), 600L);
                    autosaveManager.addRunnable(new WGAutosaver(this), 35000L);
                })
                .exceptionallyAsync(e -> {
                    logging.error("Error while deserializing!");
                    disablePlugin(e);
                    return null;
                });

        CompletableFuture<Void> worldFuture = mainFuture
                .thenAcceptAsync(queued -> scheduler.runTask(this, () -> worldMath.setBorders()))
                .exceptionallyAsync(e -> {
                    logging.error("Error while setting world borders!");
                    disablePlugin(e);
                    return null;
                });

        HandlerList.unregisterAll(kicker);

        WGMocker mocker = new WGMocker();
        pluginManager.registerEvents(mocker, this);

        voteMath = new WGVoteMath(this);
        wgCommand = Objects.requireNonNull(getCommand("wg"));

        mainFuture = worldFuture
                .thenAcceptAsync(bordered -> logging.info("WorldGrowth loaded."));
    }

    public void reloadWG() {
        reloadConfig();
        configuration = getConfig();

        HandlerList.unregisterAll(this);
        pointEvents.clear();

        CompletableFuture<Void> disabledFuture = mainFuture
                .thenAcceptAsync(setup -> scheduler.runTask(this, () -> {
                    if (configuration.getBoolean("disable-plugin")) {
                        logging.info("Plugin disabled in config.yml."); //TODO test this functionality
                        disablePlugin();
                    }
                }));

        mainFuture = disabledFuture
                .thenAcceptAsync(disabledChecked -> {
                    String localesFolderString = getDataFolder().getPath() + separator + "locales";
                    try {
                        Path localesFolderPath = Path.of(localesFolderString);
                        if (!Files.exists(localesFolderPath)) Files.createDirectory(localesFolderPath);
                        String[] localeFiles = {separator + "de.properties", separator + "en.properties", separator + "es.properties", separator + "fr.properties"};
                        for (String locale : localeFiles) WGCopier.smallCopy(getResource("locales" + locale), localesFolderString + locale);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        messager.loadLocaleMessages(localesFolderString);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .exceptionallyAsync(e -> {
                    logging.error("Error while creating locale files and loading locale messages!");
                    disablePlugin(e);
                    return null;
                });

        String url = configuration.getString("database-url");
        String user = configuration.getString("database-user");
        String password = configuration.getString("database-password");
        int maxPoolSize = configuration.getInt("database-max-pool-size");
        String finalUrl;
        if (url != null && user != null && password != null) {
            logging.info("Database information given in config.yml. Using a remote database.");
            if (databaseManager == null || databaseManager instanceof WGSQLiteManager) databaseManager = new WGMySQLManager(this, configuration);
            finalUrl = "jdbc:mysql://" + url;
        } else {
            logging.info("Database information not given in config.yml. Using a local database.");
            String outFilePath = getDataFolder().getPath() + separator + "worldgrowth.db";

            mainFuture = mainFuture
                    .thenAcceptAsync(localesLoaded -> {
                        try {
                            WGCopier.smallCopy(getResource("worldgrowth.db"), outFilePath);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .exceptionallyAsync(e -> {
                        logging.error("Error while creating local database file!");
                        disablePlugin(e);
                        return null;
                    });

            if (databaseManager == null || databaseManager instanceof WGMySQLManager) databaseManager = new WGSQLiteManager(this, configuration);
            finalUrl = "jdbc:sqlite:" + outFilePath;
        }

        mainFuture = mainFuture
                .thenAcceptAsync(dbInit -> databaseManager.initializeDataSource(finalUrl, user, password, maxPoolSize))
                .exceptionallyAsync(e -> {
                    logging.error("Error while setting up database!");
                    disablePlugin(e);
                    return null;
                });

        mainFuture = mainFuture
                .thenAcceptAsync(connected -> {
                    try {
                        databaseManager.createStructure();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .exceptionallyAsync(e -> {
                    logging.error("Error while creating database structure!");
                    disablePlugin(e);
                    return null;
                });

        varDeSerializer = new WGVarDeSerializer(this, pointEvents, eventCounterManager, databaseManager);

        mainFuture = mainFuture
                .thenAcceptAsync(structured -> {
                    try {
                        new WGRegisterer(this, configuration, databaseManager, pluginManager, varDeSerializer, pointEvents).registerEvents();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .exceptionallyAsync(e -> {
                    logging.error("Error while registering events!");
                    disablePlugin(e);
                    return null;
                });

        mainFuture = mainFuture
                .thenAcceptAsync(registered -> {
                    wgCommand.setExecutor(new WGCommand(this, messager));
                    wgCommand.setTabCompleter(new WGTabCompleter());
                })
                .exceptionallyAsync(e -> {
                    logging.error("Error while setting up commands!");
                    disablePlugin(e);
                    return null;
                });

        maxBorderQuota = configuration.getInt("max-growth-quota");
        worldMath = new WGWorldMath(this, configuration);
    }

    @Override
    public void onDisable() {
        if (databaseManager == null) return;

        mainFuture = mainFuture
                .thenAcceptAsync(disabled -> {
                    try {
                        autosaveManager.getRunnableQueue().get(0).cancel();
                    } catch (Exception ignore) {}
                    finally {
                        autosaveManager.getRunnableQueue().clear();
                        new WGAutosaver(this).run();

                        databaseManager.closeDataSource();
                    }
                })
                .exceptionallyAsync(e -> {
                    logging.error("Error during the database serializing process!");
                    logging.error(e);
                    return null;
                });
    }

    private void disablePlugin() { pluginManager.disablePlugin(this); }

    private void disablePlugin(Object e) {
        logging.error(e);
        pluginManager.disablePlugin(this);
    }

    public void addPoints(double pointsToAdd) { points += pointsToAdd; }

    public void subtractPoints(double pointsToSubtract) { points -= pointsToSubtract; }

    public void addBlocksGrownThisHour(int blocksToAdd) { blocksGrownThisHour += blocksToAdd; }

    public void addBorderQuota(int quotaToAdd) {
        if (borderQuota + quotaToAdd > maxBorderQuota) return;

        borderQuota += quotaToAdd;
    }
}
