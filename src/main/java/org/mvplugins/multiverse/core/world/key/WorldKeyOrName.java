package org.mvplugins.multiverse.core.world.key;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement;
import org.mvplugins.multiverse.core.utils.ServerProperties;
import org.mvplugins.multiverse.core.utils.compatibility.UnsafeValuesCompatibility;
import org.mvplugins.multiverse.core.utils.result.Attempt;
import org.mvplugins.multiverse.core.world.helpers.DimensionFinder;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents either a world key or a world name, with methods to parse from strings and retrieve the key or name.
 *
 * @since 5.7
 */
@ApiStatus.AvailableSince("5.7")
public sealed abstract class WorldKeyOrName implements Comparable<WorldKeyOrName> permits WorldKeyOrName.Key, WorldKeyOrName.Name {

    private static final String DEFAULT_OVERWORLD_KEY = "overworld";
    private static final String DEFAULT_NETHER_KEY = "the_nether";
    private static final String DEFAULT_END_KEY = "the_end";

    /**
     * Parse a string into a {@link WorldKeyOrName} instance.
     * <p>
     * The provided input will be interpreted as a namespaced key if it contains a ':' character,
     * otherwise it will be treated as a world name.
     *
     * @param nameOrKey The input string to parse, may be a world name or a namespaced key string
     * @return An {@link Attempt} containing either a parsed {@link WorldKeyOrName} on success or a
     * {@link WorldKeyParseFailReason} describing the failure
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static Attempt<WorldKeyOrName, WorldKeyParseFailReason> parse(@Nullable String nameOrKey) {
        if (nameOrKey == null || nameOrKey.isEmpty()) {
            return Attempt.failure(WorldKeyParseFailReason.EMPTY);
        }
        return nameOrKey.contains(":") ? parseKey(nameOrKey) : parseName(nameOrKey);
    }

    /**
     * Parse a world name into a {@link WorldKeyOrName} instance.
     * <p>
     * This will attempt to create a usable {@link NamespacedKey} from the given name using
     * {@link NamespacedKey#minecraft(String)}. If parsing fails, a failure {@link Attempt} will be
     * returned describing an invalid world name.
     *
     * @param name The world name to parse
     * @return An {@link Attempt} containing a {@link WorldKeyOrName.Name} on success or a
     * {@link WorldKeyParseFailReason#INVALID_WORLD_NAME} failure on error
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static Attempt<WorldKeyOrName, WorldKeyParseFailReason> parseName(@NonNull String name) {
        return Try.of(() -> NamespacedKey.minecraft(mapWorldNameToMinecraftKey(name)))
                .map(usableKey -> Attempt.<WorldKeyOrName, WorldKeyParseFailReason>success(new Name(name, usableKey)))
                .recover(throwable -> Attempt.failure(WorldKeyParseFailReason.INVALID_WORLD_NAME,
                        MessageReplacement.Replace.WORLD.with(name)))
                .getOrElse(() -> Attempt.failure(WorldKeyParseFailReason.INVALID_WORLD_NAME,
                        MessageReplacement.Replace.WORLD.with(name)));
    }

    private static String mapWorldNameToMinecraftKey(@NonNull String nameOrKey) {
        String defaultLevelName = getMostAccurateLevelName();
        String lowerCaseName = nameOrKey.toLowerCase(Locale.ROOT);
        if (defaultLevelName.equalsIgnoreCase(lowerCaseName)) {
            lowerCaseName = DEFAULT_OVERWORLD_KEY;
        } else if (DimensionFinder.DEFAULT_NETHER_FORMAT.replaceOverworld(defaultLevelName).equalsIgnoreCase(lowerCaseName)) {
            lowerCaseName = DEFAULT_NETHER_KEY;
        } else if (DimensionFinder.DEFAULT_END_FORMAT.replaceOverworld(defaultLevelName).equalsIgnoreCase(lowerCaseName)) {
            lowerCaseName = DEFAULT_END_KEY;
        }
        return lowerCaseName;
    }

    /**
     * Parse a namespaced key string into a {@link WorldKeyOrName} instance.
     *
     * @param nameOrKey The namespaced key string to parse (e.g. "minecraft:world")
     * @return An {@link Attempt} containing a {@link WorldKeyOrName.Key} on success or a
     * {@link WorldKeyParseFailReason#INVALID_NAMESPACED_KEY} failure on error
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static Attempt<WorldKeyOrName, WorldKeyParseFailReason> parseKey(@NonNull String nameOrKey) {
        return Option.of(NamespacedKey.fromString(nameOrKey))
                .filter(Objects::nonNull)
                .map(key -> Attempt.<WorldKeyOrName, WorldKeyParseFailReason>success(new Key(key, usableNameFromKey(key))))
                .getOrElse(() -> Attempt.failure(WorldKeyParseFailReason.INVALID_NAMESPACED_KEY,
                        MessageReplacement.Replace.NAMESPACE.with(nameOrKey)));
    }

    /**
     * Create a {@link WorldKeyOrName} representing a namespaced key.
     *
     * @param key The {@link NamespacedKey} to wrap
     * @return A {@link WorldKeyOrName} containing the provided key
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static WorldKeyOrName parseKey(@NonNull NamespacedKey key) {
        return new Key(key,  usableNameFromKey(key));
    }

    private static String usableNameFromKey(@NotNull NamespacedKey key) {
        return key.getNamespace().equals(NamespacedKey.MINECRAFT)
                ? mapMinecraftKeyToWorldName(key)
                : mapCustomKeyToWorldName(key);
    }

    private static String mapMinecraftKeyToWorldName(@NotNull NamespacedKey key) {
        String defaultLevelName = getMostAccurateLevelName();
        return switch (key.getKey()) {
            case DEFAULT_OVERWORLD_KEY -> defaultLevelName;
            case DEFAULT_NETHER_KEY -> DimensionFinder.DEFAULT_NETHER_FORMAT.replaceOverworld(defaultLevelName);
            case DEFAULT_END_KEY -> DimensionFinder.DEFAULT_END_FORMAT.replaceOverworld(defaultLevelName);
            default -> key.getKey();
        };
    }

    private static String mapCustomKeyToWorldName(@NotNull NamespacedKey key) {
        return key.getNamespace() + "_" + key.getKey();
    }

    private static String getMostAccurateLevelName() {
        return UnsafeValuesCompatibility.getMainLevelName()
                .orElse(ServerProperties::getStaticLevelName)
                .getOrElse("world"); // Worse case assume its world
    }

    /**
     * Returns true if this instance represents a world name (not a namespaced key).
     *
     * @return true if this is a name variant
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public abstract boolean isName();

    /**
     * Returns true if this instance represents a namespaced key (not a world name).
     *
     * @return true if this is a key variant
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public abstract boolean isKey();

    /**
     * Get the underlying {@link NamespacedKey} if this instance is a key variant.
     *
     * @return An {@link Option} containing the key when present, otherwise {@link Option#none()}
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public abstract @NotNull Option<NamespacedKey> getKey();

    /**
     * Get the underlying world name if this instance is a name variant.
     *
     * @return An {@link Option} containing the name when present, otherwise {@link Option#none()}
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public abstract @NotNull Option<String> getName();

    /**
     * Represent this value as an {@link Either} where the left side is the {@link NamespacedKey}
     * and the right side is the world name.
     *
     * @return An {@link Either} containing either the key (left) or the name (right)
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public abstract @NotNull Either<NamespacedKey, String> asEither();

    /**
     * Get a usable {@link NamespacedKey} for this instance.
     * <p>
     * If this instance is a key variant, the underlying key is returned. If it is a name variant,
     * an implementation-provided usable key (derived from the name) is returned.
     *
     * @return A non-null {@link NamespacedKey} that can be used where a key is required
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public abstract @NotNull NamespacedKey usableKey();

    /**
     * Get a usable name for this instance.
     * <p>
     * If this instance is a name variant, the underlying name is returned. If it is a key variant,
     * a reasonable string representation derived from the key is returned.
     *
     * @return A non-null {@link String} representing a usable name
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public abstract @NotNull String usableName();

    /**
     * Serialize this instance to a string form suitable for storage or comparison.
     *
     * @return The serialized representation of this value
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public abstract @NotNull String serialise();

    /**
     * Sorts the instances based on their serialized form, which ensures that keys and names are sorted in an
     * alphabetical manner by default.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(@NonNull WorldKeyOrName o) {
        return serialise().compareTo(o.serialise());
    }

    public static final class Key extends WorldKeyOrName {

        private final NamespacedKey key;
        private final String usableName;

        private Key(@NotNull NamespacedKey key, @NotNull String usableName) {
            this.key = key;
            this.usableName = usableName;
        }

        @Override
        public boolean isName() {
            return false;
        }

        @Override
        public boolean isKey() {
            return true;
        }

        @Override
        public @NotNull Option<NamespacedKey> getKey() {
            return Option.of(key);
        }

        @Override
        public @NotNull Option<String> getName() {
            return Option.none();
        }

        @Override
        public @NotNull Either<NamespacedKey, String> asEither() {
            return Either.left(key);
        }

        @Override
        public @NotNull NamespacedKey usableKey() {
            return key;
        }

        @Override
        public @NotNull String usableName() {
            return usableName;
        }

        @Override
        public @NotNull String serialise() {
            return key.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Key key1 = (Key) o;
            return Objects.equals(key, key1.key);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key);
        }

        @Override
        public String toString() {
            return "Key{" +
                    "key=" + key +
                    ", usableName='" + usableName + '\'' +
                    '}';
        }
    }

    public static final class Name extends WorldKeyOrName {

        private final String name;
        private final NamespacedKey usableKey;

        private Name(String name, NamespacedKey usableKey) {
            this.name = name;
            this.usableKey = usableKey;
        }

        @Override
        public boolean isName() {
            return true;
        }

        @Override
        public boolean isKey() {
            return false;
        }

        @Override
        public @NotNull Option<NamespacedKey> getKey() {
            return Option.none();
        }

        @Override
        public @NotNull Option<String> getName() {
            return Option.of(name);
        }

        @Override
        public @NotNull Either<NamespacedKey, String> asEither() {
            return Either.right(name);
        }

        @Override
        public @NotNull NamespacedKey usableKey() {
            return usableKey;
        }

        @Override
        public @NotNull String usableName() {
            return name;
        }

        @Override
        public @NotNull String serialise() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Name name1 = (Name) o;
            return Objects.equals(name, name1.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "Name{" +
                    "name='" + name + '\'' +
                    ", usableKey=" + usableKey +
                    '}';
        }
    }
}
