package org.mvplugins.multiverse.core.configuration.functions;

import java.util.HashMap;
import java.util.Map;

import co.aikar.commands.ACFUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides default serializers for common types.
 */
public final class DefaultSerializerProvider {

    private static final Map<Class<?>, NodeSerializer<?>> SERIALIZERS = new HashMap<>();

    /**
     * Adds a default serializer for the given type.
     *
     * @param type          The type.
     * @param serializer    The serializer.
     * @param <T>           The type.
     */
    public static <T> void addDefaultSerializer(@NotNull Class<T> type, @NotNull NodeSerializer<T> serializer) {
        SERIALIZERS.put(type, serializer);
    }

    /**
     * Gets the default serializer for the given type.
     *
     * @param type  The type.
     * @param <T>   The type.
     * @return The default serializer for the given type, or null if no default serializer exists.
     */
    public static <T> @Nullable NodeSerializer<T> getDefaultSerializer(Class<T> type) {
        if (type.isEnum()) {
            // Special case for enums
            return (NodeSerializer<T>) ENUM_SERIALIZER;
        }
        return (NodeSerializer<T>) SERIALIZERS.get(type);
    }

    private static final NodeSerializer<Enum> ENUM_SERIALIZER = new NodeSerializer<>() {
        @Override
        public Enum<?> deserialize(Object object, Class<Enum> type) {
            return Enum.valueOf(type, String.valueOf(object).toUpperCase());
        }

        @Override
        public Object serialize(Enum object, Class<Enum> type) {
            return object.name();
        }
    };

    private static final NodeSerializer<Boolean> BOOLEAN_SERIALIZER = new NodeSerializer<>() {
        @Override
        public Boolean deserialize(Object object, Class<Boolean> type) {
            if (object instanceof Boolean) {
                return (Boolean) object;
            }
            return ACFUtil.isTruthy(String.valueOf(object));
        }

        @Override
        public Object serialize(Boolean object, Class<Boolean> type) {
            return object;
        }
    };

    static {
        addDefaultSerializer(Boolean.class, BOOLEAN_SERIALIZER);
    }

    private DefaultSerializerProvider() {
        // Prevent instantiation as this is a static utility class
    }
}
