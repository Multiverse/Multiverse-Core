package com.onarandombox.MultiverseCore.commands_helper;

import org.bukkit.WorldType;

public class WorldFlags {
    private final String seed;
    private final String generator;
    private final WorldType worldType;
    private final boolean spawnAdjust;
    private final boolean generateStructures;

    public WorldFlags(String seed, String generator, WorldType worldType, boolean spawnAdjust, boolean generateStructures) {
        this.seed = seed;
        this.generator = generator;
        this.worldType = worldType;
        this.spawnAdjust = spawnAdjust;
        this.generateStructures = generateStructures;
    }

    public String getSeed() {
        return seed;
    }

    public String getGenerator() {
        return generator;
    }

    public WorldType getWorldType() {
        return worldType;
    }

    public boolean isSpawnAdjust() {
        return spawnAdjust;
    }

    public boolean isGenerateStructures() {
        return generateStructures;
    }

    @Override
    public String toString() {
        return "CreateWorldFlags{" +
                "seed='" + seed + '\'' +
                ", generator='" + generator + '\'' +
                ", worldType=" + worldType +
                ", spawnAdjust=" + spawnAdjust +
                ", generateStructures=" + generateStructures +
                '}';
    }
}
