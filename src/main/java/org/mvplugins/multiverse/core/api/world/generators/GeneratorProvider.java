package org.mvplugins.multiverse.core.api.world.generators;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Contract;
import org.mvplugins.multiverse.core.world.generators.SimpleGeneratorPlugin;

import java.util.Collection;

/**
 * Parse the default world generators from the bukkit config and load any generator plugins.
 * Helps in suggesting and validating generator strings.
 *
 * @since 5.0
 */
@Contract
public interface GeneratorProvider {
    /**
     * Gets the default generator for a world from the bukkit.yml config.
     *
     * @param worldName The name of the world.
     * @return The default generator string for the world, or null if none.
     * @since 5.0
     */
    @Nullable String getDefaultGeneratorForWorld(String worldName);

    /**
     * Attempts to register a plugin as {@link SimpleGeneratorPlugin}.
     *
     * @param generatorPlugin The plugin to register.
     * @return True if registered successfully, else false.
     * @since 5.0
     */
    boolean registerGeneratorPlugin(@NotNull GeneratorPlugin generatorPlugin);

    /**
     * Unregisters a plugin.
     *
     * @param pluginName The plugin to unregister.
     * @return True if the plugin was present and now unregistered, else false.
     * @since 5.0
     */
    boolean unregisterGeneratorPlugin(@NotNull String pluginName);

    /**
     * Whether a plugin is registered as a generator plugin.
     *
     * @param pluginName The name of the plugin.
     * @return True if the plugin is registered, else false.
     * @since 5.0
     */
    boolean isGeneratorPluginRegistered(@NotNull String pluginName);

    /**
     * Gets a generator plugin by name.
     *
     * @param pluginName The name of the plugin.
     * @return The generator plugin, or null if not registered.
     * @since 5.0
     */
    @Nullable GeneratorPlugin getGeneratorPlugin(@NotNull String pluginName);

    /**
     * Auto complete generator strings, used in command tab completion.
     *
     * @param currentInput  The current input from the user.
     * @return A collection of suggestions.
     */
    Collection<String> suggestGeneratorString(@Nullable String currentInput);
}
