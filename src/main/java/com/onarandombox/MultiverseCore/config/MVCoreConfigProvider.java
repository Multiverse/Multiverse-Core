package com.onarandombox.MultiverseCore.config;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVConfig;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.event.MVDebugModeEvent;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import java.io.File;

@Service
public final class MVCoreConfigProvider {

    private static final String WORLDS_CONFIG_FILE = "worlds.yml";

    private volatile MVCoreConfig config;

    private final MultiverseCore plugin;
    private final PluginManager pluginManager;

    private final Provider<MVWorldManager> worldManagerProvider; // TODO remove this dependency

    @Inject
    MVCoreConfigProvider(
            MultiverseCore plugin,
            PluginManager pluginManager,
            Provider<MVWorldManager> worldManagerProvider
    ) {
        this.plugin = plugin;
        this.pluginManager = pluginManager;
        this.worldManagerProvider = worldManagerProvider;
    }

    /**
     * Checks if the config is loaded.
     *
     * @return True if the config is loaded, false otherwise
     */
    public boolean isConfigLoaded() {
        return config != null;
    }

    /**
     * Gets the Core configuration instance. If the config has not been loaded yet, it will first be loaded.
     *
     * @return The config
     */
    @NotNull
    public MVConfig getConfig() {
        if (config == null) {
            loadConfigs();
        }
        return config;
    }

    public void loadConfigs() {
        config = MVCoreConfig.init(plugin);

        loadWorldConfigs();

        setDebugLevelFromConfig(config);
    }

    private void loadWorldConfigs() {
        worldManagerProvider.get().loadWorldConfig(new File(plugin.getDataFolder(), WORLDS_CONFIG_FILE));
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
        if (config != null) {
            return Try.run(() -> config.save());
        }
        return Try.failure(new IllegalStateException("Config not loaded yet"));
    }
}
