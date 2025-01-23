package org.mvplugins.multiverse.core.world.generators;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.utils.FileUtils;

/**
 * Parse the default world generators from the bukkit config and load any generator plugins.
 * Helps in suggesting and validating generator strings.
 */
@Service
public final class GeneratorProvider implements Listener {
    private final Map<String, String> defaultGenerators;
    private final Map<String, GeneratorPlugin> generatorPlugins;
    private final FileUtils fileUtils;

    @Inject
    GeneratorProvider(@NotNull MultiverseCore multiverseCore, @NotNull FileUtils fileUtils) {
        this.fileUtils = fileUtils;
        defaultGenerators = new HashMap<>();
        generatorPlugins = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, multiverseCore);
        loadDefaultWorldGenerators();
        loadPluginGenerators();
    }

    /**
     * Load the default world generators string from the bukkit config.
     */
    private void loadDefaultWorldGenerators() {
        File bukkitConfigFile = fileUtils.getBukkitConfig();
        if (bukkitConfigFile == null) {
            Logging.warning("Any default world generators will not be loaded!");
            return;
        }

        FileConfiguration bukkitConfig = YamlConfiguration.loadConfiguration(bukkitConfigFile);
        ConfigurationSection worldSection = bukkitConfig.getConfigurationSection("worlds");
        if (worldSection != null) {
            Set<String> keys = worldSection.getKeys(false);
            keys.forEach(key -> defaultGenerators.put(key, bukkitConfig.getString("worlds." + key + ".generator", "")));
        }
    }

    /**
     * Find generator plugins from plugins loaded and register them.
     */
    private void loadPluginGenerators() {
        Arrays.stream(Bukkit.getPluginManager().getPlugins()).forEach(plugin -> {
            if (testIsGeneratorPlugin(plugin)) {
                registerGeneratorPlugin(new SimpleGeneratorPlugin(plugin.getName()));
            }
        });
    }

    /**
     * Basic test if a plugin is a generator plugin.
     *
     * @param plugin    The plugin to test.
     * @return True if the plugin is a generator plugin, else false.
     */
    private boolean testIsGeneratorPlugin(Plugin plugin) {
        String worldName = Bukkit.getWorlds().stream().findFirst().map(World::getName).orElse("world");
        return Try.of(() -> plugin.getDefaultWorldGenerator(worldName, "") != null)
                .recover(IllegalArgumentException.class, true)
                .recover(throwable -> {
                    Logging.warning("Plugin %s threw an exception when testing if it is a generator plugin!",
                            plugin.getName());
                    throwable.printStackTrace();
                    return false;
                }).getOrElse(false);
    }

    /**
     * Gets the default generator for a world from the bukkit.yml config.
     *
     * @param worldName The name of the world.
     * @return The default generator string for the world, or null if none.
     */
    public @Nullable String getDefaultGeneratorForWorld(String worldName) {
        return defaultGenerators.getOrDefault(worldName, null);
    }

    /**
     * Attempts to register a plugin as {@link SimpleGeneratorPlugin}.
     *
     * @param generatorPlugin The plugin to register.
     * @return True if registered successfully, else false.
     */
    public boolean registerGeneratorPlugin(@NotNull GeneratorPlugin generatorPlugin) {
        var registeredGenerator = generatorPlugins.get(generatorPlugin.getPluginName());
        if (registeredGenerator == null || registeredGenerator instanceof SimpleGeneratorPlugin) {
            generatorPlugins.put(generatorPlugin.getPluginName(), generatorPlugin);
            return true;
        }
        Logging.severe("Generator plugin with name %s is already registered!", generatorPlugin.getPluginName());
        return false;
    }

    /**
     * Unregisters a plugin.
     *
     * @param pluginName The plugin to unregister.
     * @return True if the plugin was present and now unregistered, else false.
     */
    public boolean unregisterGeneratorPlugin(@NotNull String pluginName) {
        if (generatorPlugins.containsKey(pluginName)) {
            generatorPlugins.remove(pluginName);
            return true;
        }
        Logging.severe("Generator plugin with name %s is not registered!", pluginName);
        return false;
    }

    /**
     * Whether a plugin is registered as a generator plugin.
     *
     * @param pluginName The name of the plugin.
     * @return True if the plugin is registered, else false.
     */
    public boolean isGeneratorPluginRegistered(@NotNull String pluginName) {
        return generatorPlugins.containsKey(pluginName);
    }

    /**
     * Gets a generator plugin by name.
     *
     * @param pluginName The name of the plugin.
     * @return The generator plugin, or null if not registered.
     */
    public @Nullable GeneratorPlugin getGeneratorPlugin(@NotNull String pluginName) {
        return generatorPlugins.get(pluginName);
    }

    /**
     * Auto complete generator strings, used in command tab completion.
     *
     * @param currentInput  The current input from the user.
     * @return A collection of suggestions.
     */
    public Collection<String> suggestGeneratorString(@Nullable String currentInput) {
        String[] genSpilt = currentInput == null ? new String[0] : currentInput.split(":", 2);
        List<String> suggestions = new ArrayList<>(generatorPlugins.keySet());
        if (genSpilt.length < 2) {
            return suggestions;
        }
        GeneratorPlugin generatorPlugin = generatorPlugins.get(genSpilt[0]);
        if (generatorPlugin == null) {
            return suggestions;
        }
        suggestions.addAll(generatorPlugin.suggestIds(genSpilt[1]).stream().map(id -> genSpilt[0] + ":" + id).toList());
        return suggestions;
    }

    /**
     * Listen to plugins enabled to see if they are generator plugins.
     *
     * @param event The plugin enable event.
     */
    @EventHandler
    private void onPluginEnable(PluginEnableEvent event) {
        if (!testIsGeneratorPlugin(event.getPlugin())) {
            Logging.finest("Plugin %s is not a generator plugin.", event.getPlugin().getName());
            return;
        }
        if (!registerGeneratorPlugin(new SimpleGeneratorPlugin(event.getPlugin().getName()))) {
            Logging.severe("Failed to register generator plugin %s!", event.getPlugin().getName());
        }
    }

    /**
     * Listen to plugins disabled to see if they are generator plugins. If so, unregister them.
     *
     * @param event The plugin disable event.
     */
    @EventHandler
    private void onPluginDisable(PluginDisableEvent event) {
        if (!isGeneratorPluginRegistered(event.getPlugin().getName())) {
            Logging.finest("Plugin %s is not a generator plugin.", event.getPlugin().getName());
            return;
        }
        if (!unregisterGeneratorPlugin(event.getPlugin().getName())) {
            Logging.severe("Failed to unregister generator plugin %s!", event.getPlugin().getName());
        }
    }
}
