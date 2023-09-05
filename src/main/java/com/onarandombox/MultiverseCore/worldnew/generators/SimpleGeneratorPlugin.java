package com.onarandombox.MultiverseCore.worldnew.generators;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public final class SimpleGeneratorPlugin implements GeneratorPlugin {
    private final String pluginName;

    public SimpleGeneratorPlugin(@NotNull String pluginName) {
        this.pluginName = pluginName;
    }

    @Override
    public @NotNull Collection<String> suggestIds(@Nullable String currentIdInput) {
        return Collections.emptyList();
    }

    @Override
    public @Nullable Collection<String> getExampleUsages() {
        return null;
    }

    @Override
    public @Nullable String getInfoLink() {
        return null;
    }

    @Override
    public @NotNull String getPluginName() {
        return pluginName;
    }
}
