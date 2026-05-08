package org.mvplugins.multiverse.core.config.node;

import io.vavr.control.Try;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link ValueNode} specialization that stores a {@link Map} of key-value pairs.
 *
 * @param <K> The key type.
 * @param <V> The value type.
 *
 * @since 5.7
 */
@ApiStatus.AvailableSince("5.7")
public interface MapValueNode<K, V> extends ValueNode<Map<K, V>> {

    /**
     * Validates a single map key.
     * <br />
     * Implementations should return a successful {@link Try} when the key is acceptable, or a failed
     * {@link Try} when the key is invalid.
     *
     * @param key The key to validate, or {@code null} if the key is absent.
     * @return A successful {@link Try} if the key is valid, or a failed {@link Try} if it is not.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @NotNull Try<Void> validateKey(@Nullable K key);

    /**
     * Validates a single map value.
     * <br />
     * Implementations should return a successful {@link Try} when the value is acceptable, or a failed
     * {@link Try} when the value is invalid.
     *
     * @param value The value to validate, or {@code null} if the value is absent.
     * @return A successful {@link Try} if the value is valid, or a failed {@link Try} if it is not.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @NotNull Try<Void> validateValue(@Nullable V value);

    /**
     * Validates a key-value entry.
     * <br />
     * This is typically implemented by composing {@link #validateKey(Object)} and {@link #validateValue(Object)}.
     * If either part fails, the entry is considered invalid.
     *
     * @param key The entry key, or {@code null} if the key is absent.
     * @param value The entry value, or {@code null} if the value is absent.
     * @return A successful {@link Try} if both parts are valid, or a failed {@link Try} if either part is invalid.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @NotNull Try<Void> validateEntry(@Nullable K key, @Nullable V value);

    /**
     * Deserializes a raw key-value pair into a map entry.
     * <br />
     * This method converts a single persisted entry, not the full map. If either component cannot be converted,
     * implementations may return {@code null} for that side of the entry.
     *
     * @param key The raw key object, or {@code null} if no key is present.
     * @param value The raw value object, or {@code null} if no value is present.
     * @return A map entry containing the deserialized key and value.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @NotNull Map.Entry<K, V> deserializeEntry(@Nullable Object key, @Nullable Object value);

    /**
     * Serializes a map entry into raw configuration objects.
     * <br />
     * This method converts a single entry to a persisted representation, not the full map. The returned entry
     * contains the serialized key and serialized value.
     *
     * @param key The entry key, or {@code null} if the key is absent.
     * @param value The entry value, or {@code null} if the value is absent.
     * @return A map entry containing the serialized key and value.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @NotNull Map.Entry<Object, Object> serializeEntry(@Nullable K key, @Nullable V value);
}
