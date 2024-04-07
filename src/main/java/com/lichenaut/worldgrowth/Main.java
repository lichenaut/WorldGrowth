package com.lichenaut.worldgrowth;

import com.lichenaut.worldgrowth.cmd.WGCommand;
import com.lichenaut.worldgrowth.cmd.WGTabCompleter;
import com.lichenaut.worldgrowth.util.WGCopier;
import com.lichenaut.worldgrowth.util.WGDatabaseManager;
import com.lichenaut.worldgrowth.util.WGMessager;
import com.lichenaut.worldgrowth.util.WGRunnableManager;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
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
    private int boostMultiplier = 1;
    WGRunnableManager boosterManager = new WGRunnableManager(this);
    WGDatabaseManager databaseManager;

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

        String url = configuration.getString("database-url");
        String username = configuration.getString("database-username");
        String password = configuration.getString("database-password");
        if (url != null && username != null && password != null) {
            String finalUrl = "jdbc:mysql://" + url + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            databaseManager = new WGDatabaseManager();
            CompletableFuture
                    .runAsync(() -> {
                        try {
                            databaseManager.updateConnection(finalUrl, username, password);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .thenComposeAsync(connection -> CompletableFuture.runAsync(() -> {
                        try {
                            databaseManager.createStructure();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }))
                    .thenAcceptAsync(result -> logging.info("Database up and running."))
                    .exceptionallyAsync(e -> {
                        logging.error("Error while trying to connect to database!");
                        logging.error(e);
                        databaseManager = null;
                        return null;
                    });
        } else {
            databaseManager = null;
            logging.info("Database information not given. Storing information locally.");
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
    }

    @Override
    public void onDisable() {
        //databaseManager.closeConnection();
        //boosterManager.serializeQueue();
    }
}
