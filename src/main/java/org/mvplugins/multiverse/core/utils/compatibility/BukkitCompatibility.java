package org.mvplugins.multiverse.core.utils.compatibility;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.utils.ReflectHelper;

import java.lang.reflect.Method;
import java.nio.file.Path;

/**
 * Compatibility class used to handle API changes in {@link Bukkit} class.
 *
 * @since 5.6
 */
@ApiStatus.AvailableSince("5.6")
public final class BukkitCompatibility {

    private static final Try<Method> GET_LEVEL_DIRECTORY_METHOD;
    private static final Try<Method> GET_WORLD_NAMESPACED_KEY_METHOD;

    static {
        GET_LEVEL_DIRECTORY_METHOD = ReflectHelper.tryGetMethod(Server.class, "getLevelDirectory");
        GET_WORLD_NAMESPACED_KEY_METHOD = ReflectHelper.tryGetMethod(Bukkit.class, "getWorld", NamespacedKey.class);
    }

    /**
     * Check whether the server is using the new dimension storage system introduced in PaperMC 26.1.
     * <br />
     * This is based of whether getLevelDirectory method exists in the server class,which is the main API change for
     * the new dimension storage system.
     *
     * @return True if the server is using the new dimension storage system, else false.
     *
     * @since 5.6
     */
    @ApiStatus.AvailableSince("5.6")
    public static boolean isUsingNewDimensionStorage() {
        return GET_LEVEL_DIRECTORY_METHOD
                .flatMap(method -> ReflectHelper.tryInvokeMethod(Bukkit.getServer(), method))
                .isSuccess();
    }

    /**
     * Gets the folder where all the worlds will be store. Before 26.1, all worlds are stored in the root directory
     * of the server, which can be obtained by {@link Server#getWorldContainer()}.
     * <br />
     * After 26.1, PaperMC changed all worlds are stored in the "[level]/dimensions" folder under the world
     * level directory, which needs to be manually parsed.
     *
     * @return The location where all the worlds folders should be, depending on server's mc version.
     *
     * @since 5.6
     */
    @ApiStatus.AvailableSince("5.6")
    @NotNull
    public static Path getWorldFoldersDirectory() {
        Server server = Bukkit.getServer();
        return GET_LEVEL_DIRECTORY_METHOD.flatMap(method -> ReflectHelper.tryInvokeMethod(server, method))
                .filter(Path.class::isInstance)
                .map(Path.class::cast)
                .map(path -> path.resolve("dimensions"))
                .getOrElse(() -> server.getWorldContainer().toPath());
    }

    /**
     * Check if the world with the given name or namespaced key (e.g. minecraft:overworld) exists,
     * and return it if it does.
     * <br />
     * Note that some default world names have different namespaced key matched with them.
     * E.g.: world -> minecraft:overworld, world_nether -> minecraft:the_nether, world_the_end -> minecraft:the_end.
     *
     * @param nameOrKey Either a name or namespaced key string representation.
     * @return The world if it exists
     *
     * @since 5.6
     */
    @ApiStatus.AvailableSince("5.6")
    @NotNull
    public static Option<World> getWorldByNameOrKey(@NotNull String nameOrKey) {
        return Option.of(Bukkit.getWorld(nameOrKey))
                .orElse(() -> GET_WORLD_NAMESPACED_KEY_METHOD
                        .flatMap(method -> ReflectHelper.tryInvokeStaticMethod(method, NamespacedKey.fromString(nameOrKey)))
                        .filter(World.class::isInstance)
                        .map(World.class::cast)
                        .toOption());
    }

    private BukkitCompatibility() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
