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

    static void init(PluginServiceLocator serviceLocator) {
        if (instance != null) {
            throw new IllegalStateException("MultiverseCoreApi has already been initialized!");
        }
        instance = new MultiverseCoreApi(serviceLocator);
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

    private final PluginServiceLocator serviceLocator;

    private MultiverseCoreApi(PluginServiceLocator serviceProvider) {
        this.serviceLocator = serviceProvider;
    }

    /**
     * Gets the instance of AnchorManager.
     *
     * @return The AnchorManager instance
     */
    public @NotNull AnchorManager getAnchorManager() {
        return Objects.requireNonNull(serviceLocator.getActiveService(AnchorManager.class));
    }

    /**
     * Gets the instance of BlockSafety.
     *
     * @return The BlockSafety instance
     */
    public @NotNull BlockSafety getBlockSafety() {
        return Objects.requireNonNull(serviceLocator.getActiveService(BlockSafety.class));
    }

    /**
     * Gets the instance of DestinationsProvider.
     *
     * @return The DestinationsProvider instance
     */
    public @NotNull DestinationsProvider getDestinationsProvider() {
        return Objects.requireNonNull(serviceLocator.getActiveService(DestinationsProvider.class));
    }

    /**
     * Gets the instance of GeneratorProvider.
     *
     * @return The GeneratorProvider instance
     */
    public @NotNull GeneratorProvider getGeneratorProvider() {
        return Objects.requireNonNull(serviceLocator.getActiveService(GeneratorProvider.class));
    }

    /**
     * Gets the instance of LocationManipulation.
     *
     * @return The LocationManipulation instance
     */
    public @NotNull LocationManipulation getLocationManipulation() {
        return Objects.requireNonNull(serviceLocator.getActiveService(LocationManipulation.class));
    }

    /**
     * Gets the instance of MVCoreConfig.
     *
     * @return The MVCoreConfig instance
     */
    public @NotNull MVCoreConfig getMVCoreConfig() {
        return Objects.requireNonNull(serviceLocator.getActiveService(MVCoreConfig.class));
    }

    /**
     * Gets the instance of MVEconomist.
     *
     * @return The MVEconomist instance
     */
    public @NotNull MVEconomist getMVEconomist() {
        return Objects.requireNonNull(serviceLocator.getActiveService(MVEconomist.class));
    }

    /**
     * Gets the instance of SafetyTeleporter.
     *
     * @return The SafetyTeleporter instance
     */
    public @NotNull AsyncSafetyTeleporter getSafetyTeleporter() {
        return Objects.requireNonNull(serviceLocator.getActiveService(AsyncSafetyTeleporter.class));
    }

    /**
     * Gets the instance of WorldManager.
     *
     * @return The WorldManager instance
     */
    public @NotNull WorldManager getWorldManager() {
        return Objects.requireNonNull(serviceLocator.getActiveService(WorldManager.class));
    }

    /**
     * Gets the instance of Multiverse-Core's PluginServiceLocator.
     * <br/>
     * You can use this to hook into the hk2 dependency injection system used by Multiverse-Core.
     *
     * @return The Multiverse-Core's PluginServiceLocator
     */
    public PluginServiceLocator getServiceLocator() {
        return serviceLocator;
    }
}
