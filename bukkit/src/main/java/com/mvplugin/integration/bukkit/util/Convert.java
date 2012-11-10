package com.mvplugin.integration.bukkit.util;

import org.bukkit.Difficulty;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;

import com.mvplugin.minecraft.WorldEnvironment;

public final class Convert {
    private Convert() {}

    public static Difficulty toBukkit(com.mvplugin.minecraft.Difficulty d) {
        return Difficulty.valueOf(d.name());
    }
    
    public static WorldType toBukkit(com.mvplugin.minecraft.WorldType t) {
        return WorldType.valueOf(t.name());
    }

    public static Environment toBukkit(WorldEnvironment e) {
        return Environment.valueOf(e.name());
    }
}
