package com.lichenaut.worldgrowth;

import com.lichenaut.worldgrowth.cmd.WGCommand;
import com.lichenaut.worldgrowth.cmd.WGTabCompleter;
import com.lichenaut.worldgrowth.db.WGDatabaseManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import com.lichenaut.worldgrowth.event.types.block.WGBlockBreak;
import com.lichenaut.worldgrowth.runnable.WGRunnableManager;
import com.lichenaut.worldgrowth.util.WGCopier;
import com.lichenaut.worldgrowth.util.WGMessager;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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
    private Configuration configuration;
    private final PluginManager pluginManager = getServer().getPluginManager();
    private final Set<WGPointEvent<?>> pointEvents = new HashSet<>();
    private WGDatabaseManager databaseManager;
    private WGRunnableManager boosterManager = new WGRunnableManager(this);
    private int boostMultiplier = 1;

    WGRunnableManager pointManager = new WGRunnableManager(this);

    @Override
    public void onEnable() {
        //Metrics metrics = new Metrics(plugin, pluginId);
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        reloadWG();
        Objects.requireNonNull(getCommand("wg")).setExecutor(new WGCommand(this, messager));
        Objects.requireNonNull(getCommand("wg")).setTabCompleter(new WGTabCompleter());
    }

    public void reloadWG() {
        HandlerList.unregisterAll(this);

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
        if (url != null && username != null && password != null) {
            databaseManager = new WGDatabaseManager();
            String finalUrl = "jdbc:mysql://" + url + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            CompletableFuture
                    .runAsync(() -> {
                        try {
                            databaseManager.updateConnection(finalUrl, username, password);
                        } catch (SQLException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .thenComposeAsync(connected -> CompletableFuture.runAsync(() -> {
                        try {
                            databaseManager.createStructure();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }))
                    .thenComposeAsync(structured -> CompletableFuture.runAsync(() -> pointManager.addRunnable(new BukkitRunnable() {
                        @Override
                        public void run() {
                            CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
                            for (WGPointEvent<?> pointEvent : pointEvents)
                                future = future.thenComposeAsync(checked -> CompletableFuture.runAsync(pointEvent::checkCount));
                            future.whenComplete((result, e) -> {
                                if (e != null) {
                                    logging.error("Error while trying to use database!");
                                    logging.error(e);
                                }
                                pointManager.addRunnable(this, 1000L);
                            });
                        }
                    }, 1000L)))
                    .thenAcceptAsync(checking -> logging.info("Database connection up and running."))
                    .exceptionallyAsync(e -> {
                        logging.error("Error during database connecting and structuring!");
                        logging.error(e);
                        databaseManager = null;
                        return null;
                    });
        } else {
            databaseManager = null;
            logging.info("Database information not given. Storing information locally.");
        }

        ConfigurationSection events = configuration.getConfigurationSection("events");
        if (events != null) {
            for (String event : events.getKeys(false)) {
                ConfigurationSection eventSection = events.getConfigurationSection(event);
                if (eventSection == null) continue;
                switch (event) {
                    case "block-break":
                        WGBlockBreak blockBreak = new WGBlockBreak(databaseManager, logging, eventSection.getInt("quota"), eventSection.getInt("points"));
                        pluginManager.registerEvents(blockBreak, this);
                        pointEvents.add(blockBreak);
                        break;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        /*databaseManager.closeConnection();
        boosterManager.serializeQueue();*/
    }
}
