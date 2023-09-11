package com.onarandombox.MultiverseCore;

import com.onarandombox.MultiverseCore.api.MVCore;
import com.onarandombox.MultiverseCore.inject.binder.JavaPluginBinder;
import org.glassfish.hk2.utilities.binding.ScopedBindingBuilder;
import org.jetbrains.annotations.NotNull;

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
