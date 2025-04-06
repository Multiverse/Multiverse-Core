package org.mvplugins.multiverse.core.config.migration.action;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Supplier;

public final class SetMigratorAction<T> implements MigratorAction {

    public static <T> SetMigratorAction<T> of(String path, T value) {
        return new SetMigratorAction<>(path, () -> value);
    }

    public static <T> SetMigratorAction<T> of(String path, Supplier<T> value) {
        return new SetMigratorAction<>(path, value);
    }

    private final String path;
    private final Supplier<T> value;

    SetMigratorAction(String path, Supplier<T> value) {
        this.path = path;
        this.value = value;
    }

    @Override
    public void migrate(ConfigurationSection config) {
        config.set(path, value.get());
        Logging.config("Set %s to %s", path, value.get());
    }
}
