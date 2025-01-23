package org.mvplugins.multiverse.core.world.config;

import org.bukkit.block.Biome;
import org.mvplugins.multiverse.core.configuration.functions.NodeSerializer;

final class BiomeSerializer implements NodeSerializer<Biome> {

    static final String VANILLA_BIOME_BEHAVIOUR = "@vanilla";

    @Override
    public Biome deserialize(Object object, Class<Biome> type) {
        if (object instanceof Biome) {
            return (Biome) object;
        }
        try {
            String biomeStr = String.valueOf(object);
            if (biomeStr.equalsIgnoreCase(VANILLA_BIOME_BEHAVIOUR)) {
                return null;
            }
            return Biome.valueOf(biomeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public Object serialize(Biome biome, Class<Biome> type) {
        if (biome == null || biome == Biome.CUSTOM) {
            return VANILLA_BIOME_BEHAVIOUR;
        }
        return biome.name().toLowerCase();
    }
}
