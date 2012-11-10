package com.mvplugin.integration.bukkit.util;

import com.dumptruckman.minecraft.pluginbase.locale.Message;

public class BukkitLanguage {
    public static final Message CREATE_WORLD_ERROR = new Message("worlds.create_world_error",
            "&aBukkit&f experienced a problem while attempting to create '&b%s&f'!");
    public static final Message WGEN_UNKNOWN_GENERATOR = new Message("worlds.create.unknown_generator",
            "I can't create a &bBukkit&f world that uses the generator &c%s&f!");
}
