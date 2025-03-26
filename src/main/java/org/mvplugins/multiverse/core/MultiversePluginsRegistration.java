package org.mvplugins.multiverse.core;

import com.dumptruckman.minecraft.util.Logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handle loading sub-modules of the Multiverse-Core and checking for compatibility with api versions.
 */
public class MultiversePluginsRegistration {

    private static MultiversePluginsRegistration instance;

    public static MultiversePluginsRegistration get() {
        if (instance == null) {
            instance = new MultiversePluginsRegistration();
        }
        return instance;
    }

    private MultiverseCore core;
    private final List<String> registeredPlugins;
    private int pluginCount = 0;

    public MultiversePluginsRegistration() {
        registeredPlugins = new ArrayList<>();
    }

    void setCore(MultiverseCore core) {
        this.core = core;
    }

    void deferenceCore() {
        core = null;
    }

    void registerMultiversePlugin(MultiversePlugin plugin) {
        if (core == null) {
            throw new IllegalStateException("MultiverseCore has not been initialized!");
        }
        Logging.fine("Registering %s version api %s", plugin.getDescription().getName(), plugin.getVersionAsNumber());
        if (core.getVersionAsNumber() == -1) {
            // Probably a development build, so we dont check for version compatibility
            return;
        }
        if (core.getVersionAsNumber() < plugin.getTargetCoreVersion()) {
            Logging.severe("Your Multiverse-Core is OUT OF DATE!");
            Logging.severe("This version of %s requires at least Multiverse-Core version %s", plugin.getDescription().getName(), plugin.getTargetCoreVersion());
            Logging.severe("Your current Multiverse-Core version is: %s", core.getVersionAsNumber());
            Logging.severe("Grab an updated copy at: ");
            Logging.severe(core.getDescription().getWebsite());
            Logging.severe("Disabling!");
            core.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }
        if (core.getMinBackwardsCompatibleVersion() > plugin.getTargetCoreVersion()) {
            Logging.severe("Your %s is OUT OF DATE!", plugin.getDescription().getName());
            Logging.severe("This version of %s only supports up to Multiverse-Core version %s", plugin.getDescription().getName(), plugin.getTargetCoreVersion());
            Logging.severe("But your current Multiverse-Core version is: %s", core.getVersionAsNumber());
            Logging.severe("Grab an updated copy at: ");
            Logging.severe(plugin.getDescription().getWebsite());
            Logging.severe("Disabling!");
            core.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }
        registeredPlugins.add(plugin.getDescription().getName());
        pluginCount++;
    }

    void unregisterMultiversePlugin(MultiversePlugin plugin) {
        registeredPlugins.remove(plugin.getDescription().getName());
        pluginCount--;
    }

    /**
     * Gets the list of multiverse plugins modules running on this server.
     *
     * @return The list of multiverse plugins.
     */
    public List<String> getRegisteredPlugins() {
        return Collections.unmodifiableList(registeredPlugins);
    }

    /**
     * Get the number of multiverse plugins running on this server.
     *
     * @return The number of multiverse plugins.
     */
    public int getPluginCount() {
        return pluginCount;
    }
}
