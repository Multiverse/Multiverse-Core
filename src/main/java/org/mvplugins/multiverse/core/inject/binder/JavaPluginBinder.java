package org.mvplugins.multiverse.core.inject.binder;

import org.bukkit.plugin.java.JavaPlugin;
import org.glassfish.hk2.utilities.binding.ScopedBindingBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * A base class for java plugin binders. Binds the plugin to the {@link JavaPlugin} interface.
 *
 * @param <T> The type of the plugin.
 */
public abstract class JavaPluginBinder<T extends JavaPlugin> extends PluginBinder<T> {

    protected JavaPluginBinder(@NotNull T plugin) {
        super(plugin);
    }

    @Override
    protected ScopedBindingBuilder<T> bindPluginClass(ScopedBindingBuilder<T> bindingBuilder) {
        return bindingBuilder.to(JavaPlugin.class);
    }
}
