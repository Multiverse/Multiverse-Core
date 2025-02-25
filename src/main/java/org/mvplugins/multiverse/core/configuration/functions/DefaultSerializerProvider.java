package org.mvplugins.multiverse.core.configuration.functions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.dumptruckman.minecraft.util.Logging;
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
            if (type.isInstance(object)) {
                return (Enum<?>) object;
            }
            return Enum.valueOf(type, String.valueOf(object).toUpperCase());
        }

        @Override
        public Object serialize(Enum object, Class<Enum> type) {
            return object.name().toLowerCase();
        }
    };

    private static final NodeSerializer<String> STRING_SERIALIZER = new NodeSerializer<>() {
        @Override
        public String deserialize(Object object, Class<String> type) {
            if (object instanceof String) {
                return (String) object;
            }
            return String.valueOf(object);
        }

        @Override
        public Object serialize(String object, Class<String> type) {
            return object;
        }
    };

    private static final NodeSerializer<Boolean> BOOLEAN_SERIALIZER = new NodeSerializer<>() {
        @Override
        public Boolean deserialize(Object object, Class<Boolean> type) {
            if (object instanceof Boolean) {
                return (Boolean) object;
            }
            String input = String.valueOf(object);
            //todo: this is a copy from string parser
            return switch (input.toLowerCase()) {
                case "t", "true", "on", "y", "yes", "1", "allow" -> true;
                case "f", "false", "off", "n", "no", "0", "deny" -> false;
                default -> throw new RuntimeException("Unable to convert '" + input + "' to boolean.");
            };
        }

        @Override
        public Object serialize(Boolean object, Class<Boolean> type) {
            return object;
        }
    };

    private static final NodeSerializer<Integer> INTEGER_SERIALIZER = new NodeSerializer<>() {
        @Override
        public Integer deserialize(Object object, Class<Integer> type) {
            if (object instanceof Integer) {
                return (Integer) object;
            }
            return Integer.parseInt(String.valueOf(object));
        }

        @Override
        public Object serialize(Integer object, Class<Integer> type) {
            return object;
        }
    };

    private static final NodeSerializer<Double> DOUBLE_SERIALIZER = new NodeSerializer<>() {
        @Override
        public Double deserialize(Object object, Class<Double> type) {
            if (object instanceof Double number) {
                return number;
            }
            Logging.finer("Converting %s to double", object);
            return Double.parseDouble(String.valueOf(object));
        }

        @Override
        public Object serialize(Double object, Class<Double> type) {
            return object;
        }
    };

    private static final NodeSerializer<Float> FLOAT_SERIALIZER = new NodeSerializer<>() {
        @Override
        public Float deserialize(Object object, Class<Float> type) {
            if (object instanceof Float number) {
                return number;
            }
            Logging.finer("Converting %s to float", object);
            return Float.parseFloat(String.valueOf(object));
        }

        @Override
        public Object serialize(Float object, Class<Float> type) {
            return object;
        }
    };

    private static final NodeSerializer<Long> LONG_SERIALIZER = new NodeSerializer<>() {
        @Override
        public Long deserialize(Object object, Class<Long> type) {
            if (object instanceof Long number) {
                return number;
            }
            Logging.finer("Converting %s to long", object);
            return Long.parseLong(String.valueOf(object));
        }

        @Override
        public Object serialize(Long object, Class<Long> type) {
            return object;
        }
    };

    private static final NodeSerializer<Locale> LOCALE_SERIALIZER = new NodeSerializer<>() {
        @Override
        public Locale deserialize(Object object, Class<Locale> type) {
            if (object instanceof Locale) {
                return (Locale) object;
            }
            String[] split = String.valueOf(object).split("_", 2);
            return split.length > 1 ? new Locale(split[0], split[1]) : new Locale(split[0]);
        }

        @Override
        public Object serialize(Locale object, Class<Locale> type) {
            return object.toLanguageTag();
        }
    };

    static {
        addDefaultSerializer(String.class, STRING_SERIALIZER);
        addDefaultSerializer(Boolean.class, BOOLEAN_SERIALIZER);
        addDefaultSerializer(Integer.class, INTEGER_SERIALIZER);
        addDefaultSerializer(Double.class, DOUBLE_SERIALIZER);
        addDefaultSerializer(Float.class, FLOAT_SERIALIZER);
        addDefaultSerializer(Long.class, LONG_SERIALIZER);
        addDefaultSerializer(Locale.class, LOCALE_SERIALIZER);
    }

    private DefaultSerializerProvider() {
        // Prevent instantiation as this is a static utility class
    }
}
