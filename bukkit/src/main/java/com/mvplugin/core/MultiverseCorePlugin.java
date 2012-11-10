package com.mvplugin.core;

import com.dumptruckman.minecraft.pluginbase.plugin.AbstractBukkitPlugin;
import com.mvplugin.core.api.CoreConfig;
import com.mvplugin.core.api.MultiverseCore;
import com.mvplugin.core.command.ImportCommand;

import java.io.IOException;

/**
 * The primary Bukkit plugin implementation of Multiverse-Core.
 */
public class MultiverseCorePlugin extends AbstractBukkitPlugin<CoreConfig> implements MultiverseCore {

    private static final String COMMAND_PREFIX = "mv";

    private BukkitWorldManager worldManager;

    public MultiverseCorePlugin() {
        this.setPermissionName("multiverse.core");
    }

    @Override
    protected void onPluginLoad() {
        worldManager = new BukkitWorldManager(this);
        getCommandHandler().registerCommand(ImportCommand.class);
    }

    @Override
    public void onPluginEnable() {

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
