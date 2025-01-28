package org.mvplugins.multiverse.core.world.biomeprovider;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.jetbrains.annotations.NotNull;

final class SingleBiomeProviderParser implements BiomeProviderParser {

    @Override
    public BiomeProvider parseBiomeProvider(@NotNull String worldName, @NotNull String params) {
        return new SingleBiomeProvider(Biome.valueOf(params.toUpperCase()));
    }
}
