package com.mvplugin.core;

import com.dumptruckman.minecraft.pluginbase.plugin.AbstractBukkitPlugin;
import com.mvplugin.core.api.CoreConfig;
import com.mvplugin.core.api.MultiverseCore;
import com.mvplugin.core.command.ImportCommand;
import com.mvplugin.core.util.PropertyDescriptions;

import java.io.IOException;

/**
 * The primary Bukkit plugin implementation of Multiverse-Core.
 */
public class MultiverseCorePlugin extends AbstractBukkitPlugin<CoreConfig> implements MultiverseCore {

    private static final int PROTOCOL = 19;
    private static final String COMMAND_PREFIX = "mv";

    private BukkitWorldManager worldManager;

    public MultiverseCorePlugin() {
        this.setPermissionName("multiverse.core");
    }

    @Override
    protected void onPluginLoad() {
        PropertyDescriptions.init();
    }

    @Override
    public void onPluginEnable() {
        worldManager = new BukkitWorldManager(this);
    }

    protected void onReloadConfig() {
        worldManager = new BukkitWorldManager(this);
    }

    @Override
    protected void registerCommands() {
        registerCommand(ImportCommand.class);
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
    public BukkitWorldManager getWorldManager() {
        return this.worldManager;
    }

    @Override
    public MultiverseCore getCore() {
        return this;
    }

    @Override
    public void setCore(MultiverseCore core) { }

    @Override
    public int getProtocolVersion() {
        return PROTOCOL;
    }
}
