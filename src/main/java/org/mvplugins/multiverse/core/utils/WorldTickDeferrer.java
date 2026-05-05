package org.mvplugins.multiverse.core.utils;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.MultiverseCore;

import java.lang.reflect.Field;

/**
 * Defers action that cannot be done during world tick.
 */
@Service
public final class WorldTickDeferrer {

    private final MultiverseCore plugin;

    private final Option<Object> console;
    private final Option<Field> isIteratingOverLevelsMethod;

    @Inject
    WorldTickDeferrer(@NotNull MultiverseCore plugin, @NotNull Server server) {
        this.plugin = plugin;
        this.console = ReflectHelper.tryGetMethod(server.getClass(), "getServer")
                .onFailure(throwable -> Logging.fine("Unable to find getServer method."))
                .flatMap(getServerMethod -> ReflectHelper.tryInvokeMethod(server, getServerMethod))
                .onFailure(throwable -> Logging.fine("Unable to find console."))
                .toOption();
        this.isIteratingOverLevelsMethod = console.toTry()
                .map(Object::getClass)
                .flatMap(consoleClazz -> ReflectHelper.tryGetField(consoleClazz, "isIteratingOverLevels"))
                .onFailure(throwable -> Logging.fine("Unable to find isIteratingOverLevels field."))
                .toOption();
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
     * Check if the server is currently doing a world tick.
     *
     * @return True if the server is currently doing a world tick
     */
    private boolean isIteratingOverLevels() {
        return isIteratingOverLevelsMethod
                .flatMap(field -> console
                        .flatMap(c -> ReflectHelper.tryGetFieldValue(c, field, Boolean.class).toOption()))
                .getOrElse(false);
    }
}
