package org.mvplugins.multiverse.core.inject.binder;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Binds the Bukkit {@link Server} and it's associated services we make use of in Multiverse.
 */
public class ServerBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(Bukkit.getServer()).to(Server.class);
        bind(Bukkit.getPluginManager()).to(PluginManager.class);
    }
}
