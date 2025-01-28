package org.mvplugins.multiverse.core.world.biomeprovider;

import jakarta.inject.Inject;
import org.bukkit.WorldCreator;
import org.bukkit.generator.BiomeProvider;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public final class BiomeProviderFactory {

    private final Map<String, BiomeProviderParser> biomeProviderParsers;

    @Inject
    public BiomeProviderFactory() {
        biomeProviderParsers = new HashMap<>();
        registerBiomeProviderParser("single", new SingleBiomeProviderParser());
    }

    public void registerBiomeProviderParser(@NotNull String key, @NotNull BiomeProviderParser biomeProviderParser) {
        biomeProviderParsers.put("@" + key, biomeProviderParser);
    }

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
}
