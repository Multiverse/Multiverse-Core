package com.mvplugin.core;

import com.dumptruckman.minecraft.pluginbase.properties.YamlProperties;
import com.mvplugin.core.api.WorldProperties;

import java.io.File;
import java.io.IOException;

/**
 * YAML implementation of {@link WorldProperties} which will store all the world properties in a
 * YAML file named after the world.
 */
class YamlWorldProperties extends YamlProperties implements WorldProperties {

    YamlWorldProperties(File configFile) throws IOException {
        super(false, true, configFile, WorldProperties.class);
    }
}
