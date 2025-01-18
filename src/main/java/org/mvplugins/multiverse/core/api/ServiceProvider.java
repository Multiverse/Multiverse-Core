package org.mvplugins.multiverse.core.api;

import io.vavr.control.Try;
import org.glassfish.hk2.api.MultiException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.inject.PluginServiceLocator;

import java.lang.annotation.Annotation;
import java.util.List;

public interface ServiceProvider {
    @NotNull Try<PluginServiceLocator> enable();

    void disable();

    /**
     * Gets the best service from this plugin that implements the given contract or has the given implementation.
     * Service will be instantiated if it is not already instantiated.
     *
     * @param contractOrImpl The contract or concrete implementation to get the best instance of
     * @param qualifiers     The set of qualifiers that must match this service definition
     * @param <T>            The type of the contract to get
     * @return An instance of the contract or impl if it is a service, null otherwise
     * @throws MultiException if there was an error during service lookup
     * @since 5.0
     */
    @Nullable <T> T getService(@NotNull Class<T> contractOrImpl, Annotation... qualifiers) throws MultiException;

    /**
     * Gets the best active service from this plugin that implements the given contract or has the given implementation.
     *
     * @param contractOrImpl The contract or concrete implementation to get the best instance of
     * @param qualifiers     The set of qualifiers that must match this service definition
     * @param <T>            The type of the contract to get
     * @return An instance of the contract or impl if it is a service and is already instantiated, null otherwise
     * @throws MultiException if there was an error during service lookup
     * @since 5.0
     */
    @Nullable <T> T getActiveService(@NotNull Class<T> contractOrImpl, Annotation... qualifiers) throws MultiException;

    /**
     * Gets all services from this plugin that implement the given contract or have the given implementation and have
     * the provided qualifiers. Services will be instantiated if it is not already instantiated.
     *
     * @param contractOrImpl The contract or concrete implementation to get the best instance of
     * @param qualifiers     The set of qualifiers that must match this service definition
     * @param <T>            The type of the contract to get
     * @return A list of services implementing this contract or concrete implementation. May not return null, but may
     * return an empty list.
     * @throws MultiException if there was an error during service lookup
     * @since 5.0
     */
    @NotNull <T> List<T> getAllServices(
            @NotNull Class<T> contractOrImpl,
            Annotation... qualifiers) throws MultiException;

    /**
     * Gets all services from this plugin that implement the given contract or have the given implementation and have
     * the provided qualifiers.
     *
     * @param contractOrImpl The contract or concrete implementation to get the best instance of
     * @param qualifiers     The set of qualifiers that must match this service definition
     * @param <T>            The type of the contract to get
     * @return A list of services already instantiated implementing this contract or concrete implementation.
     * May not return null, but may return an empty list.
     * @throws MultiException if there was an error during service lookup
     * @since 5.0
     */
    @NotNull <T> List<T> getAllActiveServices(
            @NotNull Class<T> contractOrImpl,
            Annotation... qualifiers) throws MultiException;
}
