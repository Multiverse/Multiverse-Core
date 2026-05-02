package org.mvplugins.multiverse.core.utils.compatibility;

import io.vavr.control.Try;
import org.bukkit.NamespacedKey;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.utils.ReflectHelper;
import org.mvplugins.multiverse.core.world.key.WorldKeyOrName;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Compatibility class used to handle {@link WorldCreator} API changes across different server versions.
 *
 * @since 5.7
 */
@ApiStatus.AvailableSince("5.7")
public final class WorldCreatorCompatibility {

    private static final Try<Method> OF_KEY_METHOD;
    private static final Try<Method> OF_NAME_AND_KEY_METHOD;

    static {
        OF_KEY_METHOD = ReflectHelper.tryGetMethod(WorldCreator.class, "ofKey", NamespacedKey.class);
        OF_NAME_AND_KEY_METHOD = ReflectHelper.tryGetMethod(WorldCreator.class, "ofNameAndKey", String.class, NamespacedKey.class);
    }

    /**
     * Check whether the current server version supports creating worlds with a {@link NamespacedKey}.
     * <br />
     * This is based on whether the {@code ofKey(NamespacedKey)} method exists in the {@link WorldCreator} class,
     * which was introduced in later server versions.
     *
     * @return True if the server supports world creation with NamespacedKey, else false.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static boolean canCreateWorldWithKey() {
        return OF_KEY_METHOD.isSuccess();
    }

    /**
     * Creates a {@link WorldCreator} from a {@link WorldKeyOrName}, using the most appropriate method
     * based on the server version and the key/name state.
     * <br />
     * If the server supports NamespacedKey-based creation and the provided keyOrName is a key,
     * this method will use {@code ofKey()}. Otherwise, it falls back to {@code name()}.
     *
     * @param keyOrName Either a world key or name to create from.
     * @return The {@link WorldCreator} initialized for the given key or name.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static WorldCreator ofKeyOrName(@NotNull WorldKeyOrName keyOrName) {
        if (OF_KEY_METHOD.isSuccess() && keyOrName.isKey()) {
            // Use namespace key
            return WorldCreator.ofKey(keyOrName.usableKey());
        }
        // Creating worlds with namespace key not allowed
        return WorldCreator.name(keyOrName.usableName());
    }

    /**
     * Creates a {@link WorldCreator} from a {@link NamespacedKey} and world name, using the most
     * appropriate method based on the server version and dimension storage configuration.
     * <br />
     * This method attempts to use the most feature-rich creation method available:
     * if {@code ofNameAndKey()} is available and the parameters are compatible, use it;
     * otherwise, if {@code ofKey()} is available, use it; as a fallback, use {@code name()}.
     *
     * @param worldKey The namespaced key for the world.
     * @param worldName The display name for the world.
     * @return The {@link WorldCreator} initialized with the given key and name.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static WorldCreator ofNameAndKey(@NotNull NamespacedKey worldKey, String worldName) {
        if (OF_NAME_AND_KEY_METHOD.isSuccess() && canPassIntoNameAndKey(worldKey, worldName)) {
            return WorldCreator.ofNameAndKey(worldName, worldKey);
        }
        if  (OF_KEY_METHOD.isSuccess()) {
            return WorldCreator.ofKey(worldKey);
        }
        return WorldCreator.name(worldName);
    }

    /**
     * Check if the given world key and name can be passed into the {@code ofNameAndKey()} method.
     * <br />
     * The parameters are compatible only if the server is not using new dimension storage,
     * or if the namespace is the Minecraft namespace and the world name (lowercased) matches the key.
     *
     * @param worldKey The namespaced key for the world.
     * @param worldName The display name for the world.
     * @return True if the parameters can be safely passed to {@code ofNameAndKey()}, else false.
     */
    private static boolean canPassIntoNameAndKey(@NotNull NamespacedKey worldKey, @NotNull String worldName) {
        return !BukkitCompatibility.isUsingNewDimensionStorage()
                || (worldKey.getNamespace().equals(NamespacedKey.MINECRAFT)
                && worldName.toLowerCase(Locale.ROOT).equals(worldKey.getKey()));
    }
}
