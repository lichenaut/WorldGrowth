package com.lichenaut.worldgrowth;

import com.lichenaut.worldgrowth.cmd.WGCommand;
import com.lichenaut.worldgrowth.cmd.WGTabCompleter;
import com.lichenaut.worldgrowth.util.WGCopier;
import com.lichenaut.worldgrowth.util.WGMsgBank;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Getter
@SuppressWarnings("unused")
public final class Main extends JavaPlugin {

    private final Main plugin = this;
    private final Logger logging = LogManager.getLogger("WorldGrowth");
    private final WGMsgBank msgBank = new WGMsgBank(this);
    private final String separator = FileSystems.getDefault().getSeparator();
    private Configuration configuration;

    @Override
    public void onEnable() {
        //Metrics metrics = new Metrics(plugin, pluginId);
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        reloadWG();
        Objects.requireNonNull(getCommand("wg")).setExecutor(new WGCommand(this, msgBank));
        Objects.requireNonNull(getCommand("wg")).setTabCompleter(new WGTabCompleter());
    }

    public void reloadWG() {
        reloadConfig();
        configuration = getConfig();
        if (configuration.getBoolean("disable-plugin")) {
            logging.info("Plugin is disabled in config.yml. Disabling WorldGrowth.");
            getServer().getPluginManager().disablePlugin(this);
        }

        String localesFolderString = getDataFolder().getPath() + separator + "locales";
        Path localesFolderPath = Path.of(localesFolderString);
        try {
            if (!Files.exists(localesFolderPath)) Files.createDirectory(localesFolderPath);
            String[] localeFiles = {separator + "de.properties", separator + "en.properties", separator + "es.properties", separator + "fr.properties"};
            for (String locale : localeFiles) WGCopier.smallCopy(getResource("locales" + locale), localesFolderString + locale);
        } catch (IOException e) {
            logging.error("Error while creating locale files.");
            throw new RuntimeException(e);
        }

        try {
            msgBank.loadLocaleMessages();
        } catch (IOException e) {
            logging.error("Error while loading locale messages.");
            throw new RuntimeException(e);
        }
    }

    /*@Override
    public void onDisable() {

    }*/
}
