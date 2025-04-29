package org.mvplugins.multiverse.core.module;

import org.glassfish.hk2.utilities.binding.ScopedBindingBuilder;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.inject.binder.JavaPluginBinder;

/**
 * Binder for a {@link MultiverseModule}
 * @param <T>   The type of the module
 */
public abstract class MultiverseModuleBinder<T extends MultiverseModule> extends JavaPluginBinder<T> {

    protected MultiverseModuleBinder(@NotNull T module) {
        super(module);
    }

    @Override
    protected ScopedBindingBuilder<T> bindPluginClass(ScopedBindingBuilder<T> bindingBuilder) {
        return super.bindPluginClass(bindingBuilder).to(MultiverseModule.class);
    }
}
