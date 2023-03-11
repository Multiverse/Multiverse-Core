package com.onarandombox.MultiverseCore.inject;

import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.annotations.Contract;

/**
 * Marker interface for injection features.
 * <br/>
 * Injection features are used to extend the functionality of the {@link PluginInjection} class. They are only used
 * internally and should not be implemented by plugins.
 */
@Contract
public interface InjectionFeature {

    /**
     * Called prior to the eager loading of {@link AutoLoadedService}s.
     * <br/>
     * It's possible that performing injection in feature related services will cause {@link AutoLoadedService} instances to
     * be created.
     *
     * @param pluginServiceLocator The service locator for the plugin.
     */
    default void preServicesCreation(ServiceLocator pluginServiceLocator) {}

    /**
     * Called after the eager loading of {@link AutoLoadedService}s.
     * <br/>
     * All {@link AutoLoadedService} instances should be created by this point.
     *
     * @param pluginServiceLocator The service locator for the plugin.
     */
    default void postServicesCreation(ServiceLocator pluginServiceLocator) {}
}
