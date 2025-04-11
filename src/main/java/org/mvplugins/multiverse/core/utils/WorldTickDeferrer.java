package org.mvplugins.multiverse.core.utils;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.MultiverseCore;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Defers action that cannot be done during world tick.
 */
@Service
public final class WorldTickDeferrer {

    private final MultiverseCore plugin;

    private Object console = null;
    private Field isIteratingOverLevelsMethod = null;

    @Inject
    WorldTickDeferrer(@NotNull MultiverseCore plugin, @NotNull Server server) {
        this.plugin = plugin;
        Method getServerMethod = ReflectHelper.getMethod(server.getClass(), "getServer");
        if (getServerMethod == null) {
            Logging.fine("Unable to find getServer method.");
            return;
        }
        this.console = ReflectHelper.invokeMethod(server, getServerMethod);
        if (console == null) {
            Logging.fine("Unable to find console.");
            return;
        }
        this.isIteratingOverLevelsMethod = Try.of(() -> console.getClass().getField("isIteratingOverLevels")).getOrNull();
        if (isIteratingOverLevelsMethod == null) {
            Logging.fine("Unable to find isIteratingOverLevels field.");
        }
    }

    /**
     * Defer action that cannot be done during world tick if needed.
     * 
     * @param action The action to defer
     */
    public void deferWorldTick(Runnable action) {
        if (!isIteratingOverLevels()) {
            action.run();
            return;
        }
        Logging.fine("Deferring world tick...");
        new BukkitRunnable() {
            @Override
            public void run() {
                action.run();
            }
        }.runTaskLater(this.plugin, 1L);
    }

    /**
     * Check if the the server is currently doing a world tick.
     *
     * @return True if the server is currently doing a world tick
     */
    private boolean isIteratingOverLevels() {
        if (console == null || isIteratingOverLevelsMethod == null) {
            return false;
        }
        return Objects.requireNonNullElse(
                ReflectHelper.getFieldValue(console, isIteratingOverLevelsMethod, Boolean.class),
                false);
    }
}
