/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools;

import org.bukkit.WorldType;

import java.util.Set;

public class WorldFlags {
    private final Set<String> flagsUsed;
    private final String seed;
    private final String generator;
    private final WorldType worldType;
    private final boolean spawnAdjust;
    private final boolean generateStructures;

    public WorldFlags(Set<String> flagsUsed,
                      String seed, String generator,
                      WorldType worldType,
                      boolean spawnAdjust,
                      boolean generateStructures) {

        this.flagsUsed = flagsUsed;
        this.seed = seed;
        this.generator = generator;
        this.worldType = worldType;
        this.spawnAdjust = spawnAdjust;
        this.generateStructures = generateStructures;
    }

    public boolean hasFlag(String flag) {
        return flagsUsed.contains(flag);
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
