package com.lichenaut.worldgrowth;

import com.lichenaut.worldgrowth.cmd.WGCommand;
import com.lichenaut.worldgrowth.cmd.WGTabCompleter;
import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.db.WGMySQLManager;
import com.lichenaut.worldgrowth.db.WGSQLiteManager;
import com.lichenaut.worldgrowth.event.WGKicker;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import com.lichenaut.worldgrowth.runnable.WGBorderGrower;
import com.lichenaut.worldgrowth.runnable.WGEventConverter;
import com.lichenaut.worldgrowth.runnable.WGHourCounter;
import com.lichenaut.worldgrowth.runnable.WGRunnableManager;
import com.lichenaut.worldgrowth.util.WGCopier;
import com.lichenaut.worldgrowth.util.WGMessager;
import com.lichenaut.worldgrowth.util.WGRegisterer;
import com.lichenaut.worldgrowth.util.WGVarDeSerializer;
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
    private final CompletableFuture<Void> mainFuture = CompletableFuture.completedFuture(null);
    private final BukkitScheduler scheduler = Bukkit.getScheduler();
    private final WGRunnableManager eventCounterManager = new WGRunnableManager(this);
    private final WGRunnableManager hourMaxManager = new WGRunnableManager(this);
    private final WGRunnableManager borderManager = new WGRunnableManager(this);
    private final Set<WGPointEvent<?>> pointEvents = new HashSet<>();
    private int borderQuota;
    private int maxBorderQuota;
    private int blocksGrownThisHour;
    private double points;
    private double boostMultiplier = 1.0;
    private WGRunnableManager boostManager = new WGRunnableManager(this);
    private PluginCommand wgCommand;
    private Configuration configuration;
    private WGDBManager databaseManager;
    private WGVarDeSerializer varDeSerializer;
    private WGWorldMath worldMath;

    @Override
    public void onEnable() {
        WGKicker kicker = new WGKicker();
        pluginManager.registerEvents(kicker, this);

        new MetricsLite(this, 21539);

        wgCommand = Objects.requireNonNull(getCommand("wg"));

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        reloadWG();

        mainFuture
                .thenAcceptAsync(commandsSet -> {
                    try {
                        varDeSerializer.deserializeVariablesExceptCount();
                        databaseManager.deserializeRunnableQueue(hourMaxManager, "SELECT `delay` FROM `hour`");
                        databaseManager.deserializeRunnableQueue(boostManager, "SELECT `multiplier`, `delay` FROM `boosts`");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .exceptionallyAsync(e -> {
                    logging.error("Error while deserializing information!");
                    disablePlugin(e);
                    return null;
                });

        mainFuture
                .thenAcceptAsync(deserialized -> {
                    if (hourMaxManager.getRunnableQueue().isEmpty()) hourMaxManager.addRunnable(new WGHourCounter(this), 0L);
                    eventCounterManager.addRunnable(new WGEventConverter(this), 100L);
                    borderManager.addRunnable(new WGBorderGrower(this), 200L);
                })
                .exceptionallyAsync(e -> {
                    logging.error("Error while queueing runnables!");
                    disablePlugin(e);
                    return null;
                });

        mainFuture
                .thenAcceptAsync(queued -> scheduler.runTask(this, () -> worldMath.setBorders()))
                .thenAcceptAsync(done -> HandlerList.unregisterAll(kicker))
                .exceptionallyAsync(e -> {
                    logging.error("Error while setting world borders!");
                    disablePlugin(e);
                    return null;
                });
    }

    public void reloadWG() {
        HandlerList.unregisterAll(this);
        pointEvents.clear();

        reloadConfig();
        configuration = getConfig();
        if (configuration.getBoolean("disable-plugin")) {
            logging.info("Plugin is disabled in config.yml. Disabling WorldGrowth.");
            disablePlugin();
        }

        String localesFolderString = getDataFolder().getPath() + separator + "locales"; //TODO wip async
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

        String url = configuration.getString("database-url"); //TODO wip async
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
            try {
                WGCopier.smallCopy(getResource("worldgrowth.db"), outFilePath);
            } catch (IOException e) {
                logging.error("Error while creating local database file!");
                throw new RuntimeException(e);
            }
            if (databaseManager == null || databaseManager instanceof WGMySQLManager) databaseManager = new WGSQLiteManager(this, configuration);
            finalUrl = "jdbc:sqlite:" + outFilePath;
        }

        varDeSerializer = new WGVarDeSerializer(this, pointEvents, eventCounterManager, databaseManager);

        mainFuture
                .thenAcceptAsync(dbInit -> databaseManager.initializeDataSource(finalUrl, user, password, maxPoolSize))
                .exceptionallyAsync(e -> {
                    logging.error("Error while initializing database datasource!");
                    disablePlugin(e);
                    return null;
                });

        mainFuture
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

        mainFuture
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

        mainFuture
                .thenAcceptAsync(registered -> {
                    maxBorderQuota = configuration.getInt("max-growth-quota");
                    worldMath = new WGWorldMath(this, configuration);
                })
                .exceptionallyAsync(e -> {
                    logging.error("Error while setting up world math!");
                    disablePlugin(e);
                    return null;
                });

        mainFuture
                .thenAcceptAsync(mathReadied -> {
                    wgCommand.setExecutor(new WGCommand(this, messager));
                    wgCommand.setTabCompleter(new WGTabCompleter());
                })
                .exceptionallyAsync(e -> {
                    logging.error("Error while setting up plugin command!");
                    disablePlugin(e);
                    return null;
                });
    }

    @Override
    public void onDisable() {
        if (databaseManager == null) return;

        mainFuture
                .thenAcceptAsync(queued -> {
                    try {
                        varDeSerializer.serializeVariables();
                        databaseManager.serializeRunnableQueue(hourMaxManager, "INSERT INTO `hour` (`delay`) VALUES (?)");
                        databaseManager.serializeRunnableQueue(boostManager, "INSERT INTO `boosts` (`multiplier`, `delay`) VALUES (?, ?)");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } finally {
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
