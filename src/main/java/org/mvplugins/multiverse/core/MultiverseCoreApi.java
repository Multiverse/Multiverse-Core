package org.mvplugins.multiverse.core;

import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.anchor.AnchorManager;
import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.inject.PluginServiceLocator;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;
import org.mvplugins.multiverse.core.teleportation.BlockSafety;
import org.mvplugins.multiverse.core.teleportation.LocationManipulation;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.generators.GeneratorProvider;

import java.util.Objects;

/**
 * Provides access to the MultiverseCore API.
 */
public class MultiverseCoreApi {

    private static MultiverseCoreApi instance;

    static void init(PluginServiceLocator serviceProvider) {
        if (instance != null) {
            throw new IllegalStateException("MultiverseCoreApi has already been initialized!");
        }
        instance = new MultiverseCoreApi(serviceProvider);
    }

    static void shutdown() {
        instance = null;
    }

    /**
     * Gets the MultiverseCoreApi. This will throw an exception if the Multiverse-Core has not been initialized.
     *
     * @return The MultiverseCoreApi
     */
    public static MultiverseCoreApi get() {
        if (instance == null) {
            throw new IllegalStateException("MultiverseCoreApi has not been initialized!");
        }
        return instance;
    }

    private final PluginServiceLocator serviceProvider;

    private MultiverseCoreApi(PluginServiceLocator serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    /**
     * Gets the instance of AnchorManager.
     *
     * @return The AnchorManager instance
     */
    public @NotNull AnchorManager getAnchorManager() {
        return Objects.requireNonNull(serviceProvider.getActiveService(AnchorManager.class));
    }

    /**
     * Gets the instance of BlockSafety.
     *
     * @return The BlockSafety instance
     */
    public @NotNull BlockSafety getBlockSafety() {
        return Objects.requireNonNull(serviceProvider.getActiveService(BlockSafety.class));
    }

    /**
     * Gets the instance of DestinationsProvider.
     *
     * @return The DestinationsProvider instance
     */
    public @NotNull DestinationsProvider getDestinationsProvider() {
        return Objects.requireNonNull(serviceProvider.getActiveService(DestinationsProvider.class));
    }

    /**
     * Gets the instance of GeneratorProvider.
     *
     * @return The GeneratorProvider instance
     */
    public @NotNull GeneratorProvider getGeneratorProvider() {
        return Objects.requireNonNull(serviceProvider.getActiveService(GeneratorProvider.class));
    }

    /**
     * Gets the instance of LocationManipulation.
     *
     * @return The LocationManipulation instance
     */
    public @NotNull LocationManipulation getLocationManipulation() {
        return Objects.requireNonNull(serviceProvider.getActiveService(LocationManipulation.class));
    }

    /**
     * Gets the instance of MVCoreConfig.
     *
     * @return The MVCoreConfig instance
     */
    public @NotNull MVCoreConfig getMVCoreConfig() {
        return Objects.requireNonNull(serviceProvider.getActiveService(MVCoreConfig.class));
    }

    /**
     * Gets the instance of MVEconomist.
     *
     * @return The MVEconomist instance
     */
    public @NotNull MVEconomist getMVEconomist() {
        return Objects.requireNonNull(serviceProvider.getActiveService(MVEconomist.class));
    }

    /**
     * Gets the instance of SafetyTeleporter.
     *
     * @return The SafetyTeleporter instance
     */
    public @NotNull AsyncSafetyTeleporter getSafetyTeleporter() {
        return Objects.requireNonNull(serviceProvider.getActiveService(AsyncSafetyTeleporter.class));
    }

    /**
     * Gets the instance of WorldManager.
     *
     * @return The WorldManager instance
     */
    public @NotNull WorldManager getWorldManager() {
        return Objects.requireNonNull(serviceProvider.getActiveService(WorldManager.class));
    }

    /**
     * Gets the instance of Multiverse-Core's PluginServiceLocator.
     * <br/>
     * You can use this to hook into the hk2 dependency injection system used by Multiverse-Core.
     *
     * @return The Multiverse-Core's PluginServiceLocator
     */
    public PluginServiceLocator getServiceProvider() {
        return serviceProvider;
    }
}
