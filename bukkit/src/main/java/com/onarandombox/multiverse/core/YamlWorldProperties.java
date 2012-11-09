package com.onarandombox.multiverse.core;

import com.dumptruckman.minecraft.pluginbase.plugin.BukkitPlugin;
import com.dumptruckman.minecraft.pluginbase.properties.YamlProperties;
import com.onarandombox.multiverse.core.api.WorldProperties;

import java.io.File;
import java.io.IOException;

/**
 * YAML implementation of {@link WorldProperties} which will store all the world properties in a
 * YAML file named after the world.
 */
class YamlWorldProperties extends YamlProperties implements WorldProperties {

    YamlWorldProperties(BukkitPlugin plugin, File configFile) throws IOException {
        super(plugin, false, true, configFile, WorldProperties.class);
    }
}
