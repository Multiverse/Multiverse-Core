package com.mvplugin.integration.bukkit;

import com.dumptruckman.minecraft.pluginbase.plugin.AbstractBukkitPlugin;
import com.mvplugin.impl.CoreConfig;
import com.mvplugin.impl.MVCore;

import java.io.IOException;

/**
 * The primary Bukkit plugin implementation of Multiverse-Core.
 */
public class MultiverseCore extends AbstractBukkitPlugin<CoreConfig> implements MVCore {

    private static final String COMMAND_PREFIX = "mv";

    private BukkitWorldManager worldManager;

    public MultiverseCore() {
        this.setPermissionName("multiverse.core");
    }

    @Override
    protected void onPluginLoad() {
        worldManager = new BukkitWorldManager(this);
    }

    @Override
    public String getCommandPrefix() {
        return COMMAND_PREFIX;
    }

    @Override
    protected CoreConfig newConfigInstance() throws IOException {
        return new YamlCoreConfig(this);
    }

    @Override
    protected boolean useDatabase() {
        return false;
    }

    @Override
    public BukkitWorldManager getMVWorldManager() {
        return this.worldManager;
    }
}
