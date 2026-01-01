package org.mvplugins.multiverse.core.utils;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * A map with case-insensitive String keys. All keys are stored in lower-case form.
 *
 * @param <T> the type of mapped values
 *
 * @since 5.5
 */
@ApiStatus.AvailableSince("5.5")
public class CaseInsensitiveStringMap<T> implements Map<String, T> {

    private final Map<String, T> map;

    public CaseInsensitiveStringMap() {
        map = new HashMap<>();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(normalizeKey(key));
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public T get(Object key) {
        return map.get(normalizeKey(key));
    }

    @Override
    public @Nullable T put(String key, T value) {
        return map.put(normalizeKey(key), value);
    }

    @Override
    public T remove(Object key) {
        return map.remove(normalizeKey(key));
    }

    @Override
    public void putAll(@NonNull Map<? extends String, ? extends T> m) {
        m.forEach((key, value) -> map.put(normalizeKey(key), value));
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public @NonNull Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public @NonNull Collection<T> values() {
        return map.values();
    }

    @Override
    public @NonNull Set<Entry<String, T>> entrySet() {
        return map.entrySet();
    }

    private String normalizeKey(Object key) {
        return String.valueOf(key).toLowerCase(Locale.ROOT);
    }
}
