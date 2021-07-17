package com.onarandombox.MultiverseCore.generators;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.GeneratorPlugin;
import com.onarandombox.MultiverseCore.api.MVGeneratorManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Default implementation of {@link MVGeneratorManager} to facilitate the detection and management of generator
 * plugins installed on user's server.
 */
public class GeneratorManager implements MVGeneratorManager {

    private final MultiverseCore plugin;
    private final Map<String, String> defaultGens;
    private Map<String, GeneratorPlugin> generatorPluginMap;

    public GeneratorManager(MultiverseCore plugin) {
        this.plugin = plugin;
        this.defaultGens = new HashMap<>();
        loadDefaultWorldGenerators();
        findAndRegisterAllGeneratorPlugins();
        Bukkit.getPluginManager().registerEvents(new GeneratorListener(), this.plugin);
    }

    /**
     * Loads default generator strings defined in 'bukkit.yml'.
     */
    private void loadDefaultWorldGenerators() {
        this.generatorPluginMap = new HashMap<>();

        File[] files = this.plugin.getServerFolder().listFiles((file, s) -> s.equalsIgnoreCase("bukkit.yml"));
        if (files == null || files.length != 1) {
            Logging.fine("Could not read 'bukkit.yml'. Any Default worldgenerators will not be loaded!");
            return;
        }

        FileConfiguration bukkitConfig = YamlConfiguration.loadConfiguration(files[0]);
        if (!bukkitConfig.isConfigurationSection("worlds")) {
            Logging.fine("'bukkit.yml' missing worlds config section. No default generators found!");
            return;
        }

        Set<String> keys = bukkitConfig.getConfigurationSection("worlds").getKeys(false);
        for (String key : keys) {
            this.defaultGens.put(key, bukkitConfig.getString("worlds." + key + ".generator", ""));
        }

        this.generatorPluginMap = Collections.unmodifiableMap(this.generatorPluginMap);
    }

    /**
     * Register all generator plugins detected.
     */
    private void findAndRegisterAllGeneratorPlugins() {
        Arrays.stream(Bukkit.getPluginManager().getPlugins()).forEach(this::register);
    }

    /**
     * Attempts to register a plugin as {@link SimpleGeneratorPlugin}.
     *
     * @param genPlugin The plugin to register.
     * @return True if registered successfully, else false.
     */
    private boolean register(@NotNull Plugin genPlugin) {
        return this.register(genPlugin, SimpleGeneratorPlugin.DEFAULT_TEST_ID);
    }

    /**
     * Attempts to register a plugin as {@link SimpleGeneratorPlugin}.
     *
     * @param genPlugin The plugin to register.
     * @param id        Generator Id to test for valid chunk generator.
     * @return True if registered successfully, else false.
     */
    private boolean register(@NotNull Plugin genPlugin, String id) {
        Logging.finer("Attempting to register %s as a generator...", genPlugin.getName());
        if (this.isGeneratorPlugin(genPlugin)) {
            return false;
        }
        if (!simpleTestGen(genPlugin, id)) {
            Logging.finer("%s is probably not a generator plugin!", genPlugin.getName());
            return false;
        }
        this.generatorPluginMap.put(genPlugin.getName(), new SimpleGeneratorPlugin(genPlugin));
        Logging.finer("Registered %s as a simple generator plugin.", genPlugin.getName());
        return true;
    }

    /**
     * Basic test to see if plugin is a valid generator.
     *
     * @param generator The potential generator plugin.
     * @param id        Generator Id to test for valid chunk generator.
     * @return True if plugin is a valid generator, else false.
     */
    private boolean simpleTestGen(@Nullable Plugin generator, String id) {
        if (generator == null) {
            return false;
        }

        // Since we are unsure if the plugin is even suppose to be a generator, we assume any error means its
        // not a generator plugin.
        try {
            return generator.getDefaultWorldGenerator(SimpleGeneratorPlugin.TEST_WORLDNAME, id) != null;
        } catch (Exception ignore) {
            return false;
        }
    }

    /**
     * Unregisters a plugin.
     *
     * @param genPlugin The plugin to unregister.
     * @return True if the plugin was present and now unregistered, else false.
     */
    private boolean unregister(@NotNull Plugin genPlugin) {
        return this.generatorPluginMap.remove(genPlugin.getName()) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean register(@NotNull GeneratorPlugin generatorPlugin) {
        Plugin genPlugin = generatorPlugin.getPlugin();

        // Make sure not registered before.
        GeneratorPlugin registeredGeneratorPlugin = this.getGeneratorPlugin(genPlugin);
        if (registeredGeneratorPlugin != null) {
            if (!(registeredGeneratorPlugin instanceof SimpleGeneratorPlugin)) {
                throw new IllegalStateException("You cannot register plugin for '" + genPlugin.getName() + "' twice!");
            }
            // If it's auto registration by MV, we can remove in favour of the custom one.
            this.unregister(genPlugin);
        }

        if (this.safelyTestGenerator(genPlugin, generatorPlugin::getDefaultChunkGenerator) != TestResult.VALID) {
            return false;
        }

        this.generatorPluginMap.put(genPlugin.getName(), generatorPlugin);
        Logging.fine("Registered %s as a simple generator plugin.", genPlugin.getName());
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidGenerator(@NotNull String genString, @NotNull String worldName) {
        return validateGenerator(genString, worldName) == TestResult.VALID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public TestResult validateGenerator(@NotNull String genString, @NotNull String worldName) {
        String[] genArray = genString.split(":", 2);
        String pluginName = genArray[0];
        String id = (genArray.length == 1) ? "" : genArray[1];

        Plugin genPlugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (genPlugin == null) {
            return TestResult.PLUGIN_DOES_NOT_EXIST;
        }

        if (!this.isGeneratorPlugin(genPlugin)) {
            // Tries to register again with specific Id defined.
            if (!this.register(genPlugin, id)) {
                return TestResult.INVALID_GENERATOR;
            }
        }

        GeneratorPlugin generatorPlugin = this.getGeneratorPlugin(genPlugin);
        if (generatorPlugin == null) {
            return TestResult.INVALID_GENERATOR;
        }

        TestResult testResult = safelyTestGenerator(generatorPlugin.getPlugin(), () -> generatorPlugin.getChunkGenerator(id, worldName));
        if (testResult == TestResult.VALID && generatorPlugin instanceof SimpleGeneratorPlugin) {
            ((SimpleGeneratorPlugin) generatorPlugin).addKnownWorkingId(id);
        }
        return testResult;
    }

    /**
     * Tries to get chunk generator with wrapper to catch any exceptions that may occur.
     *
     * @param genPlugin The generator plugin.
     * @param genGetter Logic to get the chunk generator.
     * @return VALID if successfully got a not-null chunk generator, INVALID_GENERATOR if null,
     * ERRORS if exception is thrown.
     */
    @NotNull
    private TestResult safelyTestGenerator(Plugin genPlugin, Callable<ChunkGenerator> genGetter) {
        TestResult result = this.plugin.getUnsafeCallWrapper().wrap(
                () -> genGetter.call() == null ? TestResult.INVALID_GENERATOR : TestResult.VALID,
                genPlugin.getName(),
                "Failed to get the chunk generator: %s"
        );
        return (result == null) ? TestResult.ERRORS : result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGeneratorPlugin(@NotNull Plugin genPlugin) {
        return this.isGeneratorPlugin(genPlugin.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGeneratorPlugin(@NotNull String pluginName) {
        return this.generatorPluginMap.containsKey(pluginName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public GeneratorPlugin getGeneratorPlugin(@NotNull Plugin genPlugin) {
        return this.getGeneratorPlugin(genPlugin.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public GeneratorPlugin getGeneratorPlugin(@NotNull String pluginName) {
        return this.generatorPluginMap.get(pluginName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public Collection<GeneratorPlugin> getGeneratorPlugins() {
        return this.generatorPluginMap.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public Collection<String> getGeneratorPluginNames() {
        return this.generatorPluginMap.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public String getDefaultWorldGen(String worldName) {
        return this.defaultGens.get(worldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public Map<String, String> getDefaultWorldGens() {
        return this.defaultGens;
    }

    /**
     * Listen to when a plugin enables/disable to register and unregister accordingly.
     */
    private class GeneratorListener implements Listener {
        @EventHandler
        public void onEnable(PluginEnableEvent event) {
            register(event.getPlugin());
        }

        @EventHandler
        public void onDisable(PluginDisableEvent event) {
            unregister(event.getPlugin());
        }
    }
}
