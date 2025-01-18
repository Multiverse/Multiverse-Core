package org.mvplugins.multiverse.core;

import org.glassfish.hk2.utilities.binding.ScopedBindingBuilder;
import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.submodules.MVCore;
import org.mvplugins.multiverse.core.inject.binder.JavaPluginBinder;

class MultiverseCorePluginBinder extends JavaPluginBinder<MultiverseCore> {

    protected MultiverseCorePluginBinder(@NotNull MultiverseCore plugin) {
        super(plugin);
    }

    @Override
    protected ScopedBindingBuilder<MultiverseCore> bindPluginClass(
            ScopedBindingBuilder<MultiverseCore> bindingBuilder) {
        return super.bindPluginClass(bindingBuilder).to(MVCore.class).to(MultiverseCore.class);
    }
}
