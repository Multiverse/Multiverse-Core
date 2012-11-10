package com.mvplugin.core.util;

import com.mvplugin.core.minecraft.WorldEnvironment;
import org.bukkit.Difficulty;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;

public final class Convert {
    private Convert() {}

    public static Difficulty toBukkit(com.mvplugin.core.minecraft.Difficulty d) {
        return Difficulty.valueOf(d.name());
    }
    
    public static WorldType toBukkit(com.mvplugin.core.minecraft.WorldType t) {
        return WorldType.valueOf(t.name());
    }

    public static com.mvplugin.core.minecraft.WorldType fromBukkit(WorldType t) {
        return com.mvplugin.core.minecraft.WorldType.valueOf(t.name());
    }

    public static Environment toBukkit(WorldEnvironment e) {
        return Environment.valueOf(e.name());
    }
}
