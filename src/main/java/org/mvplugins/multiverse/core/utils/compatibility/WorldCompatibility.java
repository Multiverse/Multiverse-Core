package org.mvplugins.multiverse.core.utils.compatibility;

import io.vavr.control.Try;
import org.bukkit.World;
import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.utils.ReflectHelper;

import java.lang.reflect.Method;

/**
 * Compatibility class used to handle API changes in {@link World} class.
 *
 * @since 5.7
 */
@ApiStatus.AvailableSince("5.7")
public final class WorldCompatibility {

    private static final Try<Method> SAVE_WITH_FLUSH_METHOD;

    static {
        SAVE_WITH_FLUSH_METHOD = ReflectHelper.tryGetMethod(World.class, "save", boolean.class);
    }

    /**
     * Saves the world, with an option to wait for chunk writers to finish if the method is available. Saving with
     * flush is only supported on PaperMC 1.21+.
     * <br />
     * If the method is not available, it will fall back to the normal save method, which may not wait for chunk writers
     * to finish, but is the best we can do for older versions of Minecraft.
     *
     * @param world The world to save
     * @param flush Waits for chunk writers to finish
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static void saveWithFlush(World world, boolean flush) {
        SAVE_WITH_FLUSH_METHOD
                .flatMap(method -> ReflectHelper.tryInvokeMethod(world, method, flush))
                .orElseRun(ignore -> world.save());
    }

    private WorldCompatibility() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
