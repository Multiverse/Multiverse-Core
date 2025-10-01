package org.mvplugins.multiverse.core.world.biomeprovider;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
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
        NamespacedKey biomeKey = NamespacedKey.fromString(params.toLowerCase(Locale.ENGLISH));
        return new SingleBiomeProvider(Registry.BIOME.get(biomeKey));
    }

    @Override
    public Collection<String> suggestParams(@NotNull String currentInput) {
        if (biomes == null) {
            biomes = Registry.BIOME.stream()
                    .map(biome -> biome.getKey().getKey().toLowerCase(Locale.ENGLISH))
                    .toList();
        }
        return biomes;
    }
}
