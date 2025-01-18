package org.mvplugins.multiverse.core.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.api.config.MVCoreConfig;
import org.mvplugins.multiverse.core.api.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.api.teleportation.BlockSafety;
import org.mvplugins.multiverse.core.api.teleportation.LocationManipulation;
import org.mvplugins.multiverse.core.api.teleportation.SafetyTeleporter;
import org.mvplugins.multiverse.core.api.world.WorldManager;
import org.mvplugins.multiverse.core.api.world.generators.GeneratorProvider;

import java.util.Objects;

/**
 * Provides access to the MultiverseCore API.
 *
 * @since 5.0
 */
public class MultiverseCoreApi {

    private static MultiverseCoreApi instance;

    @ApiStatus.Internal
    static void init(ServiceProvider serviceProvider) {
        instance = new MultiverseCoreApi(serviceProvider);
    }

    @ApiStatus.Internal
    static void shutdown() {
        instance = null;
    }

    /**
     * Gets the MultiverseCoreApi. This will throw an exception if the Multiverse-Core has not been initialized.
     *
     * @return The MultiverseCoreApi
     * @since 5.0
     */
    public static MultiverseCoreApi get() {
        if (instance == null) {
            throw new IllegalStateException("MultiverseCoreApi has not been initialized");
        }
        return instance;
    }

    private final ServiceProvider serviceProvider;

    private MultiverseCoreApi(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    /**
     * Gets the instance of BlockSafety.
     *
     * @return The BlockSafety instance
     * @since 5.0
     */
    public @NotNull BlockSafety getBlockSafety() {
        return Objects.requireNonNull(serviceProvider.getActiveService(BlockSafety.class));
    }

    /**
     * Gets the instance of DestinationsProvider.
     *
     * @return The DestinationsProvider instance
     * @since 5.0
     */
    public @NotNull DestinationsProvider getDestinationsProvider() {
        return Objects.requireNonNull(serviceProvider.getActiveService(DestinationsProvider.class));
    }

    /**
     * Gets the instance of GeneratorProvider.
     *
     * @return The GeneratorProvider instance
     * @since 5.0
     */
    public @NotNull GeneratorProvider getGeneratorProvider() {
        return Objects.requireNonNull(serviceProvider.getActiveService(GeneratorProvider.class));
    }

    /**
     * Gets the instance of LocationManipulation.
     *
     * @return The LocationManipulation instance
     * @since 5.0
     */
    public @NotNull LocationManipulation getLocationManipulation() {
        return Objects.requireNonNull(serviceProvider.getActiveService(LocationManipulation.class));
    }

    /**
     * Gets the instance of MVCoreConfig.
     *
     * @return The MVCoreConfig instance
     * @since 5.0
     */
    public @NotNull MVCoreConfig getMVCoreConfig() {
        return Objects.requireNonNull(serviceProvider.getActiveService(MVCoreConfig.class));
    }

    /**
     * Gets the instance of SafetyTeleporter.
     *
     * @return The SafetyTeleporter instance
     * @since 5.0
     */
    public @NotNull SafetyTeleporter getSafetyTeleporter() {
        return Objects.requireNonNull(serviceProvider.getActiveService(SafetyTeleporter.class));
    }

    /**
     * Gets the instance of WorldManager.
     *
     * @return The WorldManager instance
     * @since 5.0
     */
    public @NotNull WorldManager getWorldManager() {
        return Objects.requireNonNull(serviceProvider.getActiveService(WorldManager.class));
    }
}
