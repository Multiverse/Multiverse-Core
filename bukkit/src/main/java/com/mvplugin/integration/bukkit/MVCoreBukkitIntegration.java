package com.mvplugin.integration.bukkit;

import com.dumptruckman.minecraft.pluginbase.plugin.AbstractBukkitPlugin;
import com.mvplugin.MultiverseCore;
import com.mvplugin.integration.APICollection;
import com.mvplugin.integration.Bootstrap;

import java.io.IOException;

/**
 * The primary Bukkit plugin implementation of Multiverse-Core.
 */
// TODO: Some magic that automatically hooks up abstract base libs because we can't know what libs the base uses
public class MVCoreBukkitIntegration extends AbstractBukkitPlugin<CoreConfig> {
    private static final String COMMAND_PREFIX = "mv";

    private MultiverseCore plugin;

    public MVCoreBukkitIntegration() {
        this.setPermissionName("multiverse.core");
    }

    @Override
    protected void onPluginLoad() {
        // TODO dynamically choose MVCore-implementation
        Bootstrap bootstrap = Class.forName("com.mvplugin.impl.Bootstrap").asSubclass(Bootstrap.class).newInstance();
        plugin = bootstrap.getPlugin(new APICollection(new BukkitWorldAPI(plugin, this.getServer().getWorldContainer())));
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

    public MultiverseCore getImplementation() {
        return this.plugin;
    }
}
