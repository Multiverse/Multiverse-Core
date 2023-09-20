package org.mvplugins.multiverse.core.configuration.functions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.Nullable;

/**
 * Provides default suggestions for common types.
 */
public final class DefaultSuggesterProvider {

    private static final Map<Class<?>, NodeSuggester> SUGGESTERS = new HashMap<>();

    /**
     * Adds a default suggester for the given type.
     *
     * @param clazz     The type.
     * @param suggester The suggester.
     */
    public static void addDefaultSuggester(Class<?> clazz, NodeSuggester suggester) {
        SUGGESTERS.put(clazz, suggester);
    }

    /**
     * Gets the default suggester for the given type.
     *
     * @param clazz The type.
     * @return The default suggester for the given type, or null if no default suggester exists.
     */
    public static @Nullable NodeSuggester getDefaultSuggester(Class<?> clazz) {
        if (clazz.isEnum()) {
            // Special case for enums
            return enumSuggester(clazz);
        }
        return SUGGESTERS.get(clazz);
    }

    private static NodeSuggester enumSuggester(Class<?> clazz) {
        return input -> Arrays.stream(clazz.getEnumConstants())
                .map(Object::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    private static final NodeSuggester BOOLEAN_SUGGESTER = input -> List.of("true", "false");

    private static final NodeSuggester INTEGER_SUGGESTER = input -> IntStream.range(1, 10)
            .boxed()
            .map(String::valueOf)
            .collect(Collectors.toList());

    static {
        addDefaultSuggester(Boolean.class, BOOLEAN_SUGGESTER);
        addDefaultSuggester(Integer.class, INTEGER_SUGGESTER);
    }

    private DefaultSuggesterProvider() {
        // Prevent instantiation as this is a static utility class
    }
}
