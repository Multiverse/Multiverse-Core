package org.mvplugins.multiverse.core.world.generators;

import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A default implementation of {@link GeneratorPlugin} for those generator plugins that do not provide their own
 * custom {@link GeneratorPlugin} implementation to Multiverse.
 */
final class SimpleGeneratorPlugin implements GeneratorPlugin {
    private final String pluginName;

    SimpleGeneratorPlugin(@NotNull String pluginName) {
        this.pluginName = pluginName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Collection<String> suggestIds(@Nullable String currentIdInput) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Collection<String> getExampleUsages() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable String getInfoLink() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getPluginName() {
        return pluginName;
    }
}
