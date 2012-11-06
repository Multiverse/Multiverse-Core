package com.onarandombox.multiverse.core;

import com.dumptruckman.minecraft.pluginbase.config.AbstractYamlConfig;
import com.onarandombox.multiverse.core.api.CoreConfig;

import java.io.File;
import java.io.IOException;

/**
 * A yaml implementation of Multiverse-Core's primary configuration file.
 */
class YamlCoreConfig extends AbstractYamlConfig implements CoreConfig {

    public YamlCoreConfig(MultiverseCore plugin) throws IOException {
        super(plugin, true, true, new File(plugin.getDataFolder(), "config.yml"), CoreConfig.class);
    }
}
