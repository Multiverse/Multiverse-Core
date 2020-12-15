package com.onarandombox.MultiverseCore.commands_helper;

import co.aikar.commands.BukkitCommandExecutionContext;
import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.WorldType;

import java.util.Map;

public class CreateWorldFlags {
    private final String seed;
    private final String generator;
    private final WorldType worldType;
    private final boolean spawnAdjust;
    private final boolean generateStructures;

    public CreateWorldFlags(String seed, String generator, WorldType worldType, boolean spawnAdjust, boolean generateStructures) {
        this.seed = seed;
        this.generator = generator;
        this.worldType = worldType;
        this.spawnAdjust = spawnAdjust;
        this.generateStructures = generateStructures;
    }

//    public static CreateWorldFlags Parse(Map<String, String> flags) {
//        return new CreateWorldFlags(
//
//        )
//    }

    private WorldType getWorldType(String type) {
        if (type == null) {
            return WorldType.NORMAL;
        }

        if (type.equalsIgnoreCase("normal")) {
            type = "NORMAL";
        }
        else if (type.equalsIgnoreCase("flat")) {
            type = "FLAT";
        }
        else if (type.equalsIgnoreCase("largebiomes")) {
            type = "LARGE_BIOMES";
        }
        else if (type.equalsIgnoreCase("amplified")) {
            type = "AMPLIFIED";
        }

        try {
            return WorldType.valueOf(type);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
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
}
