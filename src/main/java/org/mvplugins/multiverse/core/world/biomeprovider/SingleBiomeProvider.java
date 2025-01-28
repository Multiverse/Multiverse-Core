package org.mvplugins.multiverse.core.world.biomeprovider;

import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Helps create a world with only 1 type of Biome specified. Used in {@link WorldCreator#biomeProvider(BiomeProvider)}
 */
final class SingleBiomeProvider extends BiomeProvider {

    private final Biome biome;
    private final List<Biome> biomes;

    public SingleBiomeProvider(Biome biome) {
        this.biome = biome;
        this.biomes = List.of(biome);
    }

    @Override
    public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
        return this.biome;
    }

    @Override
    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
        return this.biomes;
    }

    public Biome getBiome() {
        return biome;
    }
}
