package com.onarandombox.MultiverseCore.inject.registration;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InstanceLifecycleEvent;
import org.glassfish.hk2.api.InstanceLifecycleEventType;
import org.glassfish.hk2.api.InstanceLifecycleListener;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

/**
 * Provided as a base class for {@link InstanceLifecycleListener} implementations that automatically register instances
 * of a given type.
 * <br/>
 * This will cause any instance of the given type to be registered as a service unless it is annotated with
 * {@link DoNotRegister}. What registration means is up to the implementation.
 * <br/>
 * Note: Implementations should be annotated with {@link Service} and utilize constructor injection with {@link Inject}
 * to ensure that the filter provider is properly injected.
 *
 * @param <T> The type of instances to automatically register.
 */
public abstract class AbstractAutoRegistration<T> implements InstanceLifecycleListener {

    private final Provider<RegistrationFilter> filterProvider;
    private final Class<T> autoRegisteredType;

    protected AbstractAutoRegistration(
            @NotNull Provider<RegistrationFilter> filterProvider,
            @NotNull Class<T> autoRegisteredType
    ) {
        this.filterProvider = filterProvider;
        this.autoRegisteredType = autoRegisteredType;
    }

    private RegistrationFilter getRegistrationFilter() {
        return filterProvider.get();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @Override
    public void lifecycleEvent(InstanceLifecycleEvent lifecycleEvent) {
        if (lifecycleEvent.getEventType() != InstanceLifecycleEventType.POST_PRODUCTION) {
            return;
        }
        var potentialInstance = lifecycleEvent.getLifecycleObject();
        if (shouldRegister(potentialInstance)) {
            register(autoRegisteredType.cast(potentialInstance));
        }
    }

    private boolean shouldRegister(Object instance) {
        return autoRegisteredType.isInstance(instance) && getRegistrationFilter().shouldRegister(instance);
    }

    /**
     * Called when the given instance should be registered. What registration means is up to the implementation.
     * If the instance's class is annotated with {@link DoNotRegister}, this method will not be called for instances of
     * that type.
     *
     * @param instance The instance to register.
     */
    protected abstract void register(T instance);
}
