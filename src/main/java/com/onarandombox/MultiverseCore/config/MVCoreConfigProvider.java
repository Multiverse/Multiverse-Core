package com.onarandombox.MultiverseCore.config;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.MultiverseCoreConfiguration;
import com.onarandombox.MultiverseCore.api.MVConfig;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.event.MVDebugModeEvent;
import io.vavr.control.Option;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public final class MVCoreConfigProvider {

    private static final String CONFIG_FILE = "config.yml";
    private static final String DEFAULTS_CONFIG_FILE = "/defaults/config.yml";
    private static final String WORLDS_CONFIG_FILE = "worlds.yml";
    private static final String CHARACTER_ENCODING = "UTF-8";
    private static final String CONFIG_KEY = "multiverse-configuration";

    private FileConfiguration multiverseConfig;
    private volatile MultiverseCoreConfiguration config;

    private final Plugin plugin;
    private final MVWorldManager worldManager;
    private final PluginManager pluginManager;

    @Inject
    public MVCoreConfigProvider(MultiverseCore plugin, MVWorldManager worldManager, PluginManager pluginManager) {
        this.plugin = plugin;
        this.worldManager = worldManager;
        this.pluginManager = pluginManager;
    }

    @NotNull
    public Option<MVConfig> getConfig() {
        return Option.of(config);
    }

    public void loadConfigs() {
        multiverseConfig = loadConfigWithDefaults();
        setConfigOptions(multiverseConfig);
        config = getOrCreateConfigObject(multiverseConfig);

        loadWorldConfigs();

        setDebugLevelFromConfig(config);
    }

    @NotNull
    private FileConfiguration loadConfigWithDefaults() {
        var config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), CONFIG_FILE));

        Option.of(plugin.getClass().getResourceAsStream(DEFAULTS_CONFIG_FILE))
                .toTry()
                .mapTry(defaultsStream -> YamlConfiguration.loadConfiguration(
                        new BufferedReader(new InputStreamReader(defaultsStream, CHARACTER_ENCODING))))
                .andThen(config::setDefaults)
                .onFailure(e -> {
                    Logging.severe("Couldn't load default config with UTF-8 encoding. Details follow:");
                    e.printStackTrace();
                    Logging.severe("Default configs NOT loaded.");
                });

        return config;
    }

    private void setConfigOptions(@NotNull FileConfiguration config) {
        config.options().copyDefaults(false);
        config.options().copyHeader(true);
    }

    @NotNull
    private MultiverseCoreConfiguration getOrCreateConfigObject(@NotNull FileConfiguration config) {
        return Try.of(() -> (MultiverseCoreConfiguration) config.get(CONFIG_KEY))
                .toOption() // Ignore exceptions
                .map(c -> c == null ? new MultiverseCoreConfiguration() : c)
                .getOrElse(new MultiverseCoreConfiguration());
    }

    private void loadWorldConfigs() {
        worldManager.loadWorldConfig(new File(plugin.getDataFolder(), WORLDS_CONFIG_FILE));
    }

    private void setDebugLevelFromConfig(@NotNull MVConfig config) {
        int level = Logging.getDebugLevel();
        Logging.setDebugLevel(config.getGlobalDebug());
        if (level != Logging.getDebugLevel()) {
            pluginManager.callEvent(new MVDebugModeEvent(level));
        }
    }

    @NotNull
    public Try<Void> saveConfig() {
        try {
            this.multiverseConfig.set(CONFIG_KEY, config);
            this.multiverseConfig.save(new File(plugin.getDataFolder(), CONFIG_FILE));
            return Try.success(null);
        } catch (IOException e) {
            return Try.failure(new MultiverseConfigurationException(
                    "Could not save Multiverse config.yml config. Please check your file permissions.", e));
        }
    }
}
