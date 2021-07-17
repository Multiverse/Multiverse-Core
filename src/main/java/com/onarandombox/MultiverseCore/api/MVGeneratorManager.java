package com.onarandombox.MultiverseCore.api;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * API for handling generator plugins installed on the user's server.
 */
public interface MVGeneratorManager {

    /**
     * Register a generator plugin.
     *
     * @param generatorPlugin   The {@link GeneratorPlugin} to register.
     * @return True if successfully registered, else false.
     */
    boolean register(@NotNull GeneratorPlugin generatorPlugin);

    /**
     * Check if a given generator string is valid.
     *
     * @param genString The generator string to check on.
     * @param worldName The target world name to generate on.
     * @return True if valid, else false.
     */
    boolean isValidGenerator(@NotNull String genString, @NotNull String worldName);

    /**
     * Check if a given generator string is valid with reason of failure with {@link TestResult}.
     *
     * @param genString The generator string to check on.
     * @param worldName The target world name to generate on.
     * @return Result of the validation.
     */
    @NotNull
    TestResult validateGenerator(@NotNull String genString, @NotNull String worldName);

    /**
     * Checks if a plugin is a registered generator plugin.
     *
     * @param genPlugin The plugin to check on.
     * @return True if is valid, else false.
     */
    boolean isGeneratorPlugin(@NotNull Plugin genPlugin);

    /**
     * Checks if a plugin is a registered generator plugin.
     *
     * @param pluginName The plugin name to check on.
     * @return True if is valid, else false.
     */
    boolean isGeneratorPlugin(@NotNull String pluginName);

    /**
     * Gets the {@link GeneratorPlugin} for a given plugin if present.
     *
     * @param genPlugin The plugin that is associated to a {@link GeneratorPlugin}.
     * @return The {@link GeneratorPlugin} if present, else null.
     */
    @Nullable
    GeneratorPlugin getGeneratorPlugin(@NotNull Plugin genPlugin);

    /**
     * Gets the {@link GeneratorPlugin} for a given plugin if present.
     *
     * @param pluginName The plugin name that is associated to a {@link GeneratorPlugin}.
     * @return The {@link GeneratorPlugin} if present, else null.
     */
    @Nullable
    GeneratorPlugin getGeneratorPlugin(@NotNull String pluginName);

    /**
     * Gets all registered {@link GeneratorPlugin}.
     *
     * @return A collection of registered generator plugins.
     */
    @NotNull
    Collection<GeneratorPlugin> getGeneratorPlugins();

    /**
     * Gets all registered plugin names associate with {@link GeneratorPlugin}.
     *
     * @return A collection of registered generator plugin names.
     */
    @NotNull
    Collection<String> getGeneratorPluginNames();

    /**
     * Gets the default world generator string defined in 'bukkit.yml' for a given world.
     *
     * @param worldName The world to get default generator of.
     * @return The generator string if present, else null.
     */
    @Nullable
    String getDefaultWorldGen(String worldName);

    /**
     * Gets all the world generator strings defined in 'bukkit.yml'.
     *
     * @return Map if world names and its respective default world generator strings.
     */
    @NotNull
    Map<String, String> getDefaultWorldGens();

    /**
     * Results from validating a generator.
     */
     enum TestResult {
        /**
         * Successfully got {@link ChunkGenerator} from the world generator plugin.
         */
        VALID,

        /**
         * Exceptions thrown while trying to get {@link ChunkGenerator} from the world generator plugin.
         */
        ERRORS,

        /**
         * Plugin is present on the server, but doesnt look like a world generator.
         */
        INVALID_GENERATOR,

        /**
         * Plugin not installed on the server.
         */
        PLUGIN_DOES_NOT_EXIST
    }
}
