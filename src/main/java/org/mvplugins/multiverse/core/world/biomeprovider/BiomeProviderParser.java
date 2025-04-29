package org.mvplugins.multiverse.core.world.biomeprovider;

import org.bukkit.generator.BiomeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A parser for BiomeProvider objects.
 * <br/>
 * This interface provides methods for parsing biome provider strings and generating suggested biome provider strings.
 */
public interface BiomeProviderParser {

    /**
     * Parses a biome provider string and returns a corresponding BiomeProvider object.
     *
     * @param worldName the name of the world
     * @param params the parameters to parse
     * @return the parsed BiomeProvider object
     */
    BiomeProvider parseBiomeProvider(@NotNull String worldName, @NotNull String params);

    /**
     * Generates a list of suggested biome provider strings based on the user's current input.
     *
     * @param currentInput the user's current input
     * @return a collection of suggested biome provider strings
     */
    Collection<String> suggestParams(@NotNull String currentInput);
}
