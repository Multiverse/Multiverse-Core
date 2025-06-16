package org.mvplugins.multiverse.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.anchor.AnchorManager;
import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.inject.PluginServiceLocator;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;
import org.mvplugins.multiverse.core.teleportation.BlockSafety;
import org.mvplugins.multiverse.core.teleportation.LocationManipulation;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.biomeprovider.BiomeProviderFactory;
import org.mvplugins.multiverse.core.world.generators.GeneratorProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Provides access to the MultiverseCore API.
 */
public class MultiverseCoreApi {

    private static MultiverseCoreApi instance;
    private static final List<Consumer<MultiverseCoreApi>> whenLoadedCallbacks = new ArrayList<>();

    static void init(@NotNull MultiverseCore multiverseCore) {
        if (instance != null) {
            throw new IllegalStateException("MultiverseCoreApi has already been initialized!");
        }
        instance = new MultiverseCoreApi(multiverseCore.getServiceLocator());
        Bukkit.getServicesManager().register(MultiverseCoreApi.class, instance, multiverseCore, ServicePriority.Normal);

        whenLoadedCallbacks.forEach(c -> c.accept(instance));
        whenLoadedCallbacks.clear();
    }

    static void shutdown() {
        Bukkit.getServicesManager().unregister(instance);
        instance = null;
    }

    /**
     * Hook your plugin into the MultiverseCoreApi here to ensure you only start using the API after it has been initialized.
     * Use this if you know your plugin may load before Multiverse-Core is fully initialized.
     * <br/>
     * This handy method removes the need for you to check with plugin manager or listen to plugin enable event.
     * <br/>
     * Callback will be called immediately if the MultiverseCoreApi has already been initialized.
     *
     * @param consumer The callback to execute when the MultiverseCoreApi has been initialized.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public static void whenLoaded(@NotNull Consumer<MultiverseCoreApi> consumer) {
        if (instance != null) {
            consumer.accept(instance);
        } else {
            whenLoadedCallbacks.add(consumer);
        }
    }

    /**
     * Checks if the MultiverseCoreApi has been initialized.
     *
     * @return True if the MultiverseCoreApi has been initialized, false otherwise
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public static boolean isLoaded() {
        return instance != null;
    }

    /**
     * Gets the MultiverseCoreApi. This will throw an exception if the Multiverse-Core has not been initialized.
     * <br/>
     * You can check if the MultiverseCoreApi has been initialized with {@link #isLoaded()} before using this method.
     * <br/>
     * Alternatively, you can use {@link #whenLoaded(Consumer)} to hook into the MultiverseCoreApi if your plugin may
     * load before Multiverse-Core is fully initialized.
     *
     * @return The MultiverseCoreApi
     * @throws IllegalStateException if the MultiverseCoreApi has not been initialized
     */
    public static @NotNull MultiverseCoreApi get() {
        if (instance == null) {
            throw new IllegalStateException("MultiverseCoreApi has not been initialized!");
        }
        return instance;
    }

    private final PluginServiceLocator serviceLocator;

    private MultiverseCoreApi(@NotNull PluginServiceLocator serviceProvider) {
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
     * Gets the instance of BiomeProviderFactory.
     *
     * @return The BiomeProviderFactory instance
     */
    public @NotNull BiomeProviderFactory getBiomeProviderFactory() {
        return Objects.requireNonNull(serviceLocator.getActiveService(BiomeProviderFactory.class));
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
     * Gets the instance of CoreConfig.
     *
     * @return The CoreConfig instance
     */
    public @NotNull CoreConfig getCoreConfig() {
        return Objects.requireNonNull(serviceLocator.getActiveService(CoreConfig.class));
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
    public @NotNull PluginServiceLocator getServiceLocator() {
        return serviceLocator;
    }
}
