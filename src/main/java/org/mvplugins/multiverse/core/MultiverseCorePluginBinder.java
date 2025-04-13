package org.mvplugins.multiverse.core;

import org.glassfish.hk2.utilities.binding.ScopedBindingBuilder;
import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.module.MultiverseModuleBinder;

final class MultiverseCorePluginBinder extends MultiverseModuleBinder<MultiverseCore> {

    MultiverseCorePluginBinder(@NotNull MultiverseCore plugin) {
        super(plugin);
    }

    @Override
    protected ScopedBindingBuilder<MultiverseCore> bindPluginClass(
            ScopedBindingBuilder<MultiverseCore> bindingBuilder) {
        return super.bindPluginClass(bindingBuilder).to(MultiverseCore.class);
    }
}
