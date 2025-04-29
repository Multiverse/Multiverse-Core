package org.mvplugins.multiverse.core.module;

import com.dumptruckman.minecraft.util.Logging;
import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.MultiverseCoreApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handle loading sub-modules of the Multiverse-Core and checking for compatibility with api versions.
 */
public final class MultiverseModulesRegistry {

    private static MultiverseModulesRegistry instance;

    public static MultiverseModulesRegistry get() {
        if (instance == null) {
            instance = new MultiverseModulesRegistry();
        }
        return instance;
    }

    private MultiverseCore core;
    private final List<String> registeredPlugins;
    private int pluginCount = 0;

    public MultiverseModulesRegistry() {
        registeredPlugins = new ArrayList<>();
    }

    void registerMultiverseModule(MultiverseModule module) {
        if (module instanceof MultiverseCore) {
            core = (MultiverseCore) module;
            return;
        }
        if (core == null) {
            throw new IllegalStateException("MultiverseCore has not been initialized!");
        }
        Logging.fine("Registering %s version api %s", module.getDescription().getName(), module.getVersionAsNumber());
        if (core.getVersionAsNumber() == -1) {
            // Probably a development build, so we dont check for version compatibility
            return;
        }
        if (core.getVersionAsNumber() < module.getTargetCoreVersion()) {
            Logging.severe("Your Multiverse-Core is OUT OF DATE!");
            Logging.severe("This version of %s requires at least Multiverse-Core version %s", module.getDescription().getName(), module.getTargetCoreVersion());
            Logging.severe("Your current Multiverse-Core version is: %s", core.getVersionAsNumber());
            Logging.severe("Grab an updated copy at: ");
            Logging.severe(core.getDescription().getWebsite());
            Logging.severe("Disabling!");
            core.getServer().getPluginManager().disablePlugin(module);
            return;
        }
        registeredPlugins.add(module.getDescription().getName());
        pluginCount++;
    }

    void unregisterMultiverseModule(MultiverseModule module) {
        if (module instanceof MultiverseCore) {
            core = null;
        }
        registeredPlugins.remove(module.getDescription().getName());
        pluginCount--;
    }

    MultiverseCore getCore() {
        return core;
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
