package com.onarandombox.MultiverseCore.inject.registration;

import jakarta.inject.Singleton;
import org.glassfish.hk2.api.InstanceLifecycleListener;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * A binder that binds an implementation of {@link InstanceLifecycleListener} to the {@link InstanceLifecycleListener}
 * contract. Additionally, it performs the necessary bindings to support the {@link DoNotRegister} annotation.
 */
public class AutoRegistrationBinder extends AbstractBinder {

    /**
     * Creates a new {@link AutoRegistrationBinder}.
     *
     * @param lifecycleListenerClass The implementation of {@link InstanceLifecycleListener} to bind
     * @return The new {@link AutoRegistrationBinder}
     */
    public static AutoRegistrationBinder with(Class<? extends InstanceLifecycleListener> lifecycleListenerClass) {
        return new AutoRegistrationBinder(lifecycleListenerClass);
    }

    private final Class<? extends InstanceLifecycleListener> lifecycleListenerClass;

    private AutoRegistrationBinder(Class<? extends InstanceLifecycleListener> lifecycleListenerClass) {
        this.lifecycleListenerClass = lifecycleListenerClass;
    }

    @Override
    protected void configure() {
        bind(DoNotRegisterRegistrationFilter.class).in(Singleton.class).to(RegistrationFilter.class).ranked(-1);
        bind(lifecycleListenerClass).in(Singleton.class).to(InstanceLifecycleListener.class);
    }
}
