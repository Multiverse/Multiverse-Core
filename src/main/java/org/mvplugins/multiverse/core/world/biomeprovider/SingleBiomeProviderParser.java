package org.mvplugins.multiverse.core.world.biomeprovider;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * A parser for {@link SingleBiomeProvider}
 */
final class SingleBiomeProviderParser implements BiomeProviderParser {

    private List<String> biomes;

    @Override
    public BiomeProvider parseBiomeProvider(@NotNull String worldName, @NotNull String params) {
        return new SingleBiomeProvider(Biome.valueOf(params.toUpperCase(Locale.ENGLISH)));
    }

    @Override
    public Collection<String> suggestParams(@NotNull String currentInput) {
        if (biomes == null) {
            biomes = Arrays.stream(Biome.values()).map(biome -> biome.toString().toLowerCase(Locale.ENGLISH)).toList();
        }
        return biomes;
    }
}
