package org.mvplugins.multiverse.core.world.biomeprovider;

import jakarta.inject.Inject;
import org.bukkit.WorldCreator;
import org.bukkit.generator.BiomeProvider;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A factory class for creating BiomeProvider objects.
 * <br/>
 * This class provides methods for parsing biome provider strings and registering custom biome provider parsers.
 * It also provides a method for generating a list of suggested biome provider strings based on the user's current input.
 */
@Service
public final class BiomeProviderFactory {

    private final Map<String, BiomeProviderParser> biomeProviderParsers;

    @Inject
    BiomeProviderFactory() {
        biomeProviderParsers = new HashMap<>();
        registerBiomeProviderParser("single", new SingleBiomeProviderParser());
    }

    /**
     * Registers a custom biome provider parser. Key will be prepended with "@".
     *
     * @param key                   the key to associate with the biome provider parser
     * @param biomeProviderParser   the biome provider parser to register
     */
    public void registerBiomeProviderParser(@NotNull String key, @NotNull BiomeProviderParser biomeProviderParser) {
        biomeProviderParsers.put("@" + key, biomeProviderParser);
    }

    /**
     * Parses a biome provider string and returns a corresponding BiomeProvider object.
     *
     * @param worldName the name of the world
     * @param biomeProviderString the string to parse, which can be a biome provider name or a custom provider string
     * @return the parsed BiomeProvider object, or null if the input string is empty
     */
    public BiomeProvider parseBiomeProvider(@NotNull String worldName, @NotNull String biomeProviderString) {
        if (biomeProviderString.isEmpty()) {
            return null;
        }
        if (biomeProviderString.startsWith("@")) {
            String[] split = biomeProviderString.split(":", 2);
            BiomeProviderParser biomeProviderParser = biomeProviderParsers.get(split[0]);
            if (biomeProviderParser != null) {
                return biomeProviderParser.parseBiomeProvider(worldName, split.length > 1 ? split[1] : "");
            }
        }
        return WorldCreator.getBiomeProviderForName(worldName, biomeProviderString, null);
    }

    /**
     * Generates a list of suggested biome provider strings based on the user's current input.
     * <br/>
     * If the input doesn't contain a colon (:), it returns a list of all available biome provider keys,
     * with a colon appended to the end of each key if the input doesn't exactly match the key.
     * <br/>
     * If the input contains a colon, it splits the input into two parts: the biome provider key and the parameters.
     * It then uses the biome provider parser associated with the key to suggest possible parameters
     * and returns a list of complete biome provider strings by combining the key and suggested parameters.
     * <br/>
     * If no matching biome provider parser is found, it returns an empty list.
     *
     * @param currentInput the user's current input
     * @return a collection of suggested biome provider strings
     */
    public Collection<String> suggestBiomeString(@NotNull String currentInput) {
        String[] split = currentInput.split(":", 2);
        if (split.length < 2) {
            return biomeProviderParsers.keySet().stream()
                    .map(key -> currentInput.equals(key) ? key + ":" : key)
                    .toList();
        }
        BiomeProviderParser biomeProviderParser = biomeProviderParsers.get(split[0]);
        if (biomeProviderParser != null) {
            return biomeProviderParser.suggestParams(split[1]).stream().map(key -> split[0] + ":" + key).toList();
        }
        return Collections.emptyList();
    }
}
