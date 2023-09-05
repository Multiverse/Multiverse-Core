package com.onarandombox.MultiverseCore.worldnew.generators;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.onarandombox.MultiverseCore.utils.file.FileUtils.getBukkitConfig;

@Service
public class GeneratorProvider implements Listener {
    private final Map<String, String> defaultGenerators;
    private final Map<String, GeneratorPlugin> generatorPlugins;

    @Inject
    GeneratorProvider(@NotNull MultiverseCore multiverseCore) {
        defaultGenerators = new HashMap<>();
        generatorPlugins = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, multiverseCore);
        loadDefaultWorldGenerators();
        loadPluginGenerators();
    }

    private void loadDefaultWorldGenerators() {
        File bukkitConfigFile = getBukkitConfig();
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
    private void loadPluginGenerators() {
        Arrays.stream(Bukkit.getPluginManager().getPlugins()).forEach(plugin -> {
            if (testIsGeneratorPlugin(plugin)) {
                registerGeneratorPlugin(new SimpleGeneratorPlugin(plugin.getName()));
            }
        });
    }

    private boolean testIsGeneratorPlugin(Plugin plugin) {
        String worldName = Bukkit.getWorlds().stream().findFirst().map(World::getName).orElse("world");
        try {
            return plugin.getDefaultWorldGenerator(worldName, "") != null;
        } catch (IllegalArgumentException e) {
            Logging.fine("Testing id is wrong, but it is probably a generator plugin: %s", plugin.getName());
            return true;
        } catch (Throwable t) {
            Logging.warning("Plugin %s threw an exception when testing if it is a generator plugin!", plugin.getName());
            return false;
        }
    }

    public String getDefaultGeneratorForWorld(String worldName) {
        return defaultGenerators.getOrDefault(worldName, "");
    }

    public boolean registerGeneratorPlugin(@NotNull GeneratorPlugin generatorPlugin) {
        var registeredGenerator = generatorPlugins.get(generatorPlugin.getPluginName());
        if (registeredGenerator == null || registeredGenerator instanceof SimpleGeneratorPlugin) {
            generatorPlugins.put(generatorPlugin.getPluginName(), generatorPlugin);
            return true;
        }
        Logging.severe("Generator plugin with name %s is already registered!", generatorPlugin.getPluginName());
        return false;
    }


    public boolean unregisterGeneratorPlugin(@NotNull String pluginName) {
        if (generatorPlugins.containsKey(pluginName)) {
            generatorPlugins.remove(pluginName);
            return true;
        }
        Logging.severe("Generator plugin with name %s is not registered!", pluginName);
        return false;
    }

    public boolean isGeneratorPluginRegistered(@NotNull String pluginName) {
        return generatorPlugins.containsKey(pluginName);
    }

    public GeneratorPlugin getGeneratorPlugin(@NotNull String pluginName) {
        return generatorPlugins.get(pluginName);
    }

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

    @EventHandler
    private void onPluginEnable(PluginEnableEvent event) {
        if (testIsGeneratorPlugin(event.getPlugin())) {
            registerGeneratorPlugin(new SimpleGeneratorPlugin(event.getPlugin().getName()));
        }
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent event) {
        if (isGeneratorPluginRegistered(event.getPlugin().getName())) {
            unregisterGeneratorPlugin(event.getPlugin().getName());
        }
    }
}
