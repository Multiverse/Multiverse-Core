package com.onarandombox.multiverse.core;

import com.dumptruckman.minecraft.pluginbase.plugin.AbstractBukkitPlugin;
import com.onarandombox.multiverse.core.api.Core;
import com.onarandombox.multiverse.core.api.CoreConfig;

import java.io.IOException;

/**
 * The primary Bukkit plugin implementation of Multiverse-Core.
 */
public class MultiverseCore extends AbstractBukkitPlugin<CoreConfig> implements Core {

    public MultiverseCore() {
        this.setPermissionName("multiverse.core");
    }

    @Override
    public String getCommandPrefix() {
        return "mv";
    }

    @Override
    protected CoreConfig newConfigInstance() throws IOException {
        return new YamlCoreConfig(this);
    }

    @Override
    protected boolean useDatabase() {
        return false;
    }
}
