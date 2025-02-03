package org.mvplugins.multiverse.core.world.biomeprovider;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A parser for {@link SingleBiomeProvider}
 */
final class SingleBiomeProviderParser implements BiomeProviderParser {

    private final List<String> biomeStrings = Arrays.stream(Biome.values())
            .map(biome -> biome.toString().toLowerCase())
            .toList();

    @Override
    public BiomeProvider parseBiomeProvider(@NotNull String worldName, @NotNull String params) {
        return new SingleBiomeProvider(Biome.valueOf(params.toUpperCase()));
    }

    @Override
    public Collection<String> suggestParams(@NotNull String currentInput) {
        return biomeStrings;
    }
}
