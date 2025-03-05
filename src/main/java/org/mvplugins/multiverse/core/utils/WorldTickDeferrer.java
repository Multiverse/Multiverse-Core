package org.mvplugins.multiverse.core.utils;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.MultiverseCore;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

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

    public boolean isIteratingOverLevels() {
        if (console == null || isIteratingOverLevelsMethod == null) {
            return false;
        }
        return Objects.requireNonNullElse(
                ReflectHelper.getFieldValue(console, isIteratingOverLevelsMethod, Boolean.class),
                false);
    }
}
