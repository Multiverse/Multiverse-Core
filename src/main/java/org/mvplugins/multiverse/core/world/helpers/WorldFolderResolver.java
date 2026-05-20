package org.mvplugins.multiverse.core.world.helpers;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.utils.compatibility.BukkitCompatibility;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.key.WorldKeyOrName;

import java.io.File;

/**
 * <p>Utility class for resolving the file system location of a world folder.</p>
 *
 * <p>This class handles the differences in world folder locations between server versions,
 * particularly the new dimension storage system introduced in PaperMC 26.1 which stores worlds
 * in a dimensions folder under the world level directory.</p>
 *
 * @since 5.7
 */
@ApiStatus.AvailableSince("5.7")
public final class WorldFolderResolver {

    /**
     * Resolves the world folder file path for the given world key or name.
     * <br />
     * This method automatically selects the appropriate resolution method based on whether the server
     * is using the new dimension storage system introduced in PaperMC 26.1.
     *
     * @param keyOrName The world key or name to resolve.
     * @return The resolved world folder file.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @NotNull
    public static File resolve(@NotNull WorldKeyOrName keyOrName) {
        return BukkitCompatibility.isUsingNewDimensionStorage()
                ? resolveAsDimensionKey(keyOrName.usableKey())
                : resolveAsLegacyWorldName(keyOrName.usableName());
    }

    /**
     * Resolves the world folder file path for the given Multiverse world.
     * <br />
     * This method automatically selects the appropriate resolution method based on whether the server
     * is using the new dimension storage system introduced in PaperMC 26.1.
     *
     * @param multiverseWorld The Multiverse world to resolve.
     * @return The resolved world folder file.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @NotNull
    public static File resolve(@NotNull MultiverseWorld multiverseWorld) {
        return BukkitCompatibility.isUsingNewDimensionStorage()
                ? resolveAsDimensionKey(multiverseWorld.getKey())
                : resolveAsLegacyWorldName(multiverseWorld.getName());
    }

    /**
     * Gets the world folder using the new dimension storage path format of {@code [namespace]/[key]}.
     * <br />
     * This method is only valid for PaperMC 26.1 and above. For earlier server versions,
     * use {@link #resolveAsLegacyWorldName(String)} instead.
     *
     * @param namespacedKey The namespaced key of the world.
     * @return The resolved world folder file using the new dimension storage format.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @NotNull
    public static File resolveAsDimensionKey(@NotNull NamespacedKey namespacedKey) {
        return BukkitCompatibility.getWorldFoldersDirectory()
                .resolve(namespacedKey.getNamespace())
                .resolve(namespacedKey.getKey())
                .toFile();
    }

    /**
     * Gets the world folder using the legacy world name format from the server root directory.
     * <br />
     * This method is the default for server versions before PaperMC 26.1.
     *
     * @param worldName The name of the world.
     * @return The resolved world folder file using the legacy world name format.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @NotNull
    public static File resolveAsLegacyWorldName(@NotNull String worldName) {
        return Bukkit.getWorldContainer()
                .toPath()
                .resolve(worldName)
                .toFile();
    }

    private WorldFolderResolver() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
