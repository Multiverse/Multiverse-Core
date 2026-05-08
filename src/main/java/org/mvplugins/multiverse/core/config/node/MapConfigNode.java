package org.mvplugins.multiverse.core.config.node;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.config.node.functions.DefaultStringParserProvider;
import org.mvplugins.multiverse.core.config.node.functions.DefaultSuggesterProvider;
import org.mvplugins.multiverse.core.config.node.functions.NodeChangeCallback;
import org.mvplugins.multiverse.core.config.node.functions.NodeStringParser;
import org.mvplugins.multiverse.core.config.node.functions.NodeSuggester;
import org.mvplugins.multiverse.core.config.node.functions.NodeValueCallback;
import org.mvplugins.multiverse.core.config.node.functions.SenderNodeStringParser;
import org.mvplugins.multiverse.core.config.node.functions.SenderNodeSuggester;
import org.mvplugins.multiverse.core.config.node.serializer.DefaultSerializerProvider;
import org.mvplugins.multiverse.core.config.node.serializer.NodeSerializer;
import org.mvplugins.multiverse.core.exceptions.MultiverseException;
import org.mvplugins.multiverse.core.utils.REPatterns;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A config node that contains key-value mappings.
 *
 * @param <K> The key type.
 * @param <V> The value type.
 *
 * @since 5.7
 */
@ApiStatus.AvailableSince("5.7")
public class MapConfigNode<K, V> extends ConfigNode<Map<K, V>> implements MapValueNode<K, V> {

    /**
     * Creates a new builder for a {@link MapConfigNode}.
     *
     * @param path      The path of the node.
     * @param keyType   The map key type.
     * @param valueType The map value type.
     * @param <K>       The key type.
     * @param <V>       The value type.
     * @return The new builder.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static @NotNull <K, V> Builder<K, V> mapBuilder(
            @NotNull String path,
            @NotNull Class<K> keyType,
            @NotNull Class<V> valueType) {
        return new Builder<>(path, keyType, valueType);
    }

    protected final @NotNull Class<K> keyType;
    protected final @NotNull Class<V> valueType;
    protected final @Nullable NodeSuggester keySuggester;
    protected final @Nullable NodeSuggester valueSuggester;
    protected final @Nullable NodeStringParser<K> keyStringParser;
    protected final @Nullable NodeStringParser<V> valueStringParser;
    protected final @Nullable NodeSerializer<K> keySerializer;
    protected final @Nullable NodeSerializer<V> valueSerializer;
    protected final @Nullable Function<K, Try<Void>> keyValidator;
    protected final @Nullable Function<V, Try<Void>> valueValidator;

    protected MapConfigNode(
            @NotNull String path,
            @NotNull String[] comments,
            @Nullable String name,
            @NotNull Class<Map<K, V>> type,
            @NotNull String[] aliases,
            @Nullable Supplier<Map<K, V>> defaultValueSupplier,
            @Nullable NodeSuggester suggester,
            @Nullable NodeStringParser<Map<K, V>> stringParser,
            @Nullable NodeSerializer<Map<K, V>> serializer,
            @Nullable Function<Map<K, V>, Try<Void>> validator,
            @Nullable NodeValueCallback<Map<K, V>> onLoad,
            @Nullable NodeChangeCallback<Map<K, V>> onLoadAndChange,
            @Nullable NodeChangeCallback<Map<K, V>> onChange,
            @NotNull Class<K> keyType,
            @NotNull Class<V> valueType,
            @Nullable NodeSuggester keySuggester,
            @Nullable NodeSuggester valueSuggester,
            @Nullable NodeStringParser<K> keyStringParser,
            @Nullable NodeStringParser<V> valueStringParser,
            @Nullable NodeSerializer<K> keySerializer,
            @Nullable NodeSerializer<V> valueSerializer,
            @Nullable Function<K, Try<Void>> keyValidator,
            @Nullable Function<V, Try<Void>> valueValidator) {
        super(path, comments, name, type, aliases, defaultValueSupplier, suggester, stringParser, serializer,
                validator, onLoad, onLoadAndChange, onChange);
        this.keyType = keyType;
        this.valueType = valueType;
        this.keySuggester = keySuggester != null
                ? keySuggester
                : DefaultSuggesterProvider.getDefaultSuggester(keyType);
        this.valueSuggester = valueSuggester != null
                ? valueSuggester
                : DefaultSuggesterProvider.getDefaultSuggester(valueType);
        this.keyStringParser = keyStringParser != null
                ? keyStringParser
                : DefaultStringParserProvider.getDefaultStringParser(keyType);
        this.valueStringParser = valueStringParser != null
                ? valueStringParser
                : DefaultStringParserProvider.getDefaultStringParser(valueType);
        this.keySerializer = keySerializer != null
                ? keySerializer
                : DefaultSerializerProvider.getDefaultSerializer(keyType);
        this.valueSerializer = valueSerializer != null
                ? valueSerializer
                : DefaultSerializerProvider.getDefaultSerializer(valueType);
        this.keyValidator = keyValidator != null
                ? keyValidator
                : defaultYamlKeyValidator();
        this.valueValidator = valueValidator;

        setDefaults();
    }

    private Function<K, Try<Void>> defaultYamlKeyValidator() {
        return key -> Try.of(() -> {
            if (!REPatterns.YAML_KEY.matcher(String.valueOf(serializeKey(key))).matches()) {
                throw new MultiverseException("Invalid yaml key: '" + key + "'. Keys can only " +
                        "contain alphanumeric characters, underscores and hyphens.");
            }
            return null;
        });
    }

    private void setDefaults() {
        if ((this.keySuggester != null || this.valueSuggester != null) && this.suggester == null) {
            setDefaultSuggester();
        }
        if (this.keyStringParser != null && this.valueStringParser != null && this.stringParser == null) {
            setDefaultStringParser();
        }
        if ((this.keyValidator != null || this.valueValidator != null) && this.validator == null) {
            setDefaultValidator();
        }
        if ((this.keySerializer != null || this.valueSerializer != null) && this.serializer == null) {
            setDefaultSerializer();
        }
        if (this.defaultValue == null) {
            this.defaultValue = LinkedHashMap::new;
        }
    }

    private void setDefaultSuggester() {
        if (this.keySuggester instanceof SenderNodeSuggester || this.valueSuggester instanceof SenderNodeSuggester) {
            this.suggester = (SenderNodeSuggester) this::suggestEntries;
        } else {
            this.suggester = input -> suggestEntries(null, input);
        }
    }

    private @NotNull Collection<String> suggestEntries(@Nullable CommandSender sender, @Nullable String input) {
        String content = input == null ? "" : input;
        int lastComma = content.lastIndexOf(',');
        String prefix = lastComma == -1 ? "" : content.substring(0, lastComma + 1);
        String currentEntry = lastComma == -1 ? content : content.substring(lastComma + 1);

        String[] keyValue = REPatterns.EQUALS.split(currentEntry, 2);
        if (keyValue.length == 2 && valueSuggester != null) {
            String keyPart = keyValue[0];
            String valuePart = keyValue[1];
            return suggestFrom(valueSuggester, sender, valuePart).stream()
                    .map(suggestedValue -> prefix + keyPart + "=" + suggestedValue)
                    .toList();
        }

        if (keySuggester == null) {
            return Collections.emptyList();
        }

        return suggestFrom(keySuggester, sender, currentEntry).stream()
                .map(suggestedKey -> prefix + suggestedKey + "=")
                .toList();
    }

    private @NotNull Collection<String> suggestFrom(
            @NotNull NodeSuggester suggester,
            @Nullable CommandSender sender,
            @Nullable String input) {
        if (sender != null && suggester instanceof SenderNodeSuggester senderSuggester) {
            return senderSuggester.suggest(sender, input);
        }
        return suggester.suggest(input);
    }

    private void setDefaultStringParser() {
        this.stringParser = (input, type) -> Try.of(() -> {
            Map<K, V> parsed = new LinkedHashMap<>();
            if (input == null || input.isBlank()) {
                return parsed;
            }
            for (String entry : REPatterns.SEMICOLON.split(input)) {
                String[] keyValue = REPatterns.EQUALS.split(entry, 2);
                if (keyValue.length != 2) {
                    throw new IllegalArgumentException("Invalid map entry '" + entry + "'. Expected format key=value");
                }
                NodeStringParser<K> keyParser = Objects.requireNonNull(keyStringParser, "keyStringParser");
                NodeStringParser<V> valueParser = Objects.requireNonNull(valueStringParser, "valueStringParser");
                K parsedKey = keyParser.parse(keyValue[0].trim(), keyType).get();
                V parsedValue = valueParser.parse(keyValue[1].trim(), valueType).get();
                parsed.put(parsedKey, parsedValue);
            }
            return parsed;
        });
    }

    private void setDefaultValidator() {
        this.validator = value -> Try.of(() -> {
            if (value == null) {
                return null;
            }
            for (Map.Entry<K, V> entry : value.entrySet()) {
                if (keyValidator != null) {
                    keyValidator.apply(entry.getKey()).get();
                }
                if (valueValidator != null) {
                    valueValidator.apply(entry.getValue()).get();
                }
            }
            return null;
        });
    }

    private void setDefaultSerializer() {
        this.serializer = new NodeSerializer<>() {
            @Override
            public Map<K, V> deserialize(Object object, Class<Map<K, V>> type) {
                if (object == null) {
                    return new LinkedHashMap<>();
                }

                if (object instanceof Map<?, ?> rawMap) {
                    Map<K, V> result = new LinkedHashMap<>();
                    rawMap.forEach((key, value) -> {
                        Map.Entry<K, V> entry = deserializeEntry(key, value);
                        result.put(entry.getKey(), entry.getValue());
                    });
                    return result;
                }

                if (object instanceof ConfigurationSection section) {
                    Map<K, V> result = new LinkedHashMap<>();
                    for (String key : section.getKeys(false)) {
                        Object rawValue = section.get(key);
                        Map.Entry<K, V> entry = deserializeEntry(key, rawValue);
                        result.put(entry.getKey(), entry.getValue());
                    }
                    return result;
                }

                throw new IllegalArgumentException("Invalid map object '" + object + "'. Expected map or section.");
            }

            @Override
            public Object serialize(Map<K, V> object, Class<Map<K, V>> type) {
                Map<Object, Object> serialized = new LinkedHashMap<>();
                if (object == null) {
                    return serialized;
                }
                object.forEach((key, value) -> {
                    Map.Entry<Object, Object> entry = serializeEntry(key, value);
                    serialized.put(entry.getKey(), entry.getValue());
                });
                return serialized;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Try<Void> validateKey(@Nullable K key) {
        return keyValidator == null ? Try.success(null) : keyValidator.apply(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Try<Void> validateValue(@Nullable V value) {
        return valueValidator == null ? Try.success(null) : valueValidator.apply(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Try<Void> validateEntry(@Nullable K key, @Nullable V value) {
        return validateKey(key).flatMap(ignored -> validateValue(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Map.Entry<K, V> deserializeEntry(@Nullable Object key, @Nullable Object value) {
        K deserializedKey = keySerializer == null
                ? keyType.isInstance(key) ? keyType.cast(key) : null
                : keySerializer.deserialize(key, keyType);
        V deserializedValue = valueSerializer == null
                ? keyType.isInstance(value) ? valueType.cast(value) : null
                : valueSerializer.deserialize(value, valueType);
        return new SimpleEntry<>(deserializedKey, deserializedValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Map.Entry<Object, Object> serializeEntry(@Nullable K key, @Nullable V value) {
        return new SimpleEntry<>(serializeKey(key), serializeValue(value));
    }

    private @Nullable Object serializeKey(@Nullable K key) {
        return keySerializer == null ? key : keySerializer.serialize(key, keyType);
    }

    private @Nullable Object serializeValue(@Nullable V value) {
        return valueSerializer == null ? value : valueSerializer.serialize(value, valueType);
    }

    private record SimpleEntry<K, V>(K key, V value) implements Map.Entry<K, V> {
        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Builder for {@link MapConfigNode} instances.
     *
     * <p>This builder extends {@link ConfigNode.Builder} with additional configuration specific to map entries:
     * entry-level suggesters, string parsers, serializers and validators for both keys and values. When any
     * entry-level component is omitted the builder provides a sensible default (for example default parsers,
     * suggesters, serializers or an empty map default value).
     *
     * @param <K> The map key type.
     * @param <V> The map value type.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static class Builder<K, V> extends ConfigNode.Builder<Map<K, V>, Builder<K, V>> {

        protected final @NotNull Class<K> keyType;
        protected final @NotNull Class<V> valueType;
        protected @Nullable NodeSuggester keySuggester;
        protected @Nullable NodeSuggester valueSuggester;
        protected @Nullable NodeStringParser<K> keyStringParser;
        protected @Nullable NodeStringParser<V> valueStringParser;
        protected @Nullable NodeSerializer<K> keySerializer;
        protected @Nullable NodeSerializer<V> valueSerializer;
        protected @Nullable Function<K, Try<Void>> keyValidator;
        protected @Nullable Function<V, Try<Void>> valueValidator;

        /**
         * Creates a new builder for a map config node.
         *
         * @param path The configuration path for the node.
         * @param keyType The runtime type of the map keys.
         * @param valueType The runtime type of the map values.
         *
         * @since 5.7
         */
        @ApiStatus.AvailableSince("5.7")
        protected Builder(@NotNull String path, @NotNull Class<K> keyType, @NotNull Class<V> valueType) {
            //noinspection unchecked
            super(path, (Class<Map<K, V>>) (Object) Map.class);
            this.keyType = keyType;
            this.valueType = valueType;
            this.defaultValue = LinkedHashMap::new;
        }

        /**
         * Sets a suggester for map keys. The suggester is used when computing tab-completion suggestions for the
         * key part of an entry.
         *
         * @param keySuggester The suggester to use for keys.
         * @return This builder for chaining.
         *
         * @since 5.7
         */
        @ApiStatus.AvailableSince("5.7")
        public @NotNull Builder<K, V> keySuggester(@NotNull NodeSuggester keySuggester) {
            this.keySuggester = keySuggester;
            return self();
        }

        /**
         * Sets a sender-aware suggester for map keys. Use this when suggestions depend on the command sender
         * (permissions, location, etc.).
         *
         * @param keySuggester The sender-aware suggester.
         * @return This builder for chaining.
         *
         * @since 5.7
         */
        @ApiStatus.AvailableSince("5.7")
        public @NotNull Builder<K, V> keySuggester(@NotNull SenderNodeSuggester keySuggester) {
            this.keySuggester = keySuggester;
            return self();
        }

        /**
         * Sets a suggester for map values. The suggester is used when computing tab-completion suggestions for the
         * value part of an entry.
         *
         * @param valueSuggester The suggester to use for values.
         * @return This builder for chaining.
         *
         * @since 5.7
         */
        @ApiStatus.AvailableSince("5.7")
        public @NotNull Builder<K, V> valueSuggester(@NotNull NodeSuggester valueSuggester) {
            this.valueSuggester = valueSuggester;
            return self();
        }

        /**
         * Sets a sender-aware suggester for map values. Use this when value suggestions depend on the
         * command sender.
         *
         * @param valueSuggester The sender-aware suggester.
         * @return This builder for chaining.
         *
         * @since 5.7
         */
        @ApiStatus.AvailableSince("5.7")
        public @NotNull Builder<K, V> valueSuggester(@NotNull SenderNodeSuggester valueSuggester) {
            this.valueSuggester = valueSuggester;
            return self();
        }

        /**
         * Sets the string parser for keys. The parser is used when parsing user-provided key strings into key
         * instances (e.g. when parsing a map from a single-line string representation).
         *
         * @param keyStringParser The parser to use for keys.
         * @return This builder for chaining.
         *
         * @since 5.7
         */
        @ApiStatus.AvailableSince("5.7")
        public @NotNull Builder<K, V> keyStringParser(@NotNull NodeStringParser<K> keyStringParser) {
            this.keyStringParser = keyStringParser;
            return self();
        }

        /**
         * Sets a sender-aware string parser for keys. Use this when parsing depends on the sender context.
         *
         * @param keyStringParser The sender-aware key parser.
         * @return This builder for chaining.
         *
         * @since 5.7
         */
        @ApiStatus.AvailableSince("5.7")
        public @NotNull Builder<K, V> keyStringParser(@NotNull SenderNodeStringParser<K> keyStringParser) {
            this.keyStringParser = keyStringParser;
            return self();
        }

        /**
         * Sets the string parser for values. The parser converts the textual part after '=' in an entry into the
         * value type.
         *
         * @param valueStringParser The parser to use for values.
         * @return This builder for chaining.
         *
         * @since 5.7
         */
        @ApiStatus.AvailableSince("5.7")
        public @NotNull Builder<K, V> valueStringParser(@NotNull NodeStringParser<V> valueStringParser) {
            this.valueStringParser = valueStringParser;
            return self();
        }

        /**
         * Sets a sender-aware string parser for values.
         *
         * @param valueStringParser The sender-aware value parser.
         * @return This builder for chaining.
         *
         * @since 5.7
         */
        @ApiStatus.AvailableSince("5.7")
        public @NotNull Builder<K, V> valueStringParser(@NotNull SenderNodeStringParser<V> valueStringParser) {
            this.valueStringParser = valueStringParser;
            return self();
        }

        /**
         * Sets the serializer for keys. Serializers convert keys to/from persisted forms stored in the configuration
         * backend.
         *
         * @param keySerializer The key serializer.
         * @return This builder for chaining.
         *
         * @since 5.7
         */
        @ApiStatus.AvailableSince("5.7")
        public @NotNull Builder<K, V> keySerializer(@NotNull NodeSerializer<K> keySerializer) {
            this.keySerializer = keySerializer;
            return self();
        }

        /**
         * Sets the serializer for values.
         *
         * @param valueSerializer The value serializer.
         * @return This builder for chaining.
         *
         * @since 5.7
         */
        @ApiStatus.AvailableSince("5.7")
        public @NotNull Builder<K, V> valueSerializer(@NotNull NodeSerializer<V> valueSerializer) {
            this.valueSerializer = valueSerializer;
            return self();
        }

        /**
         * Sets a validator for keys. The validator should return a successful {@link Try} for valid keys or a failed
         * {@link Try} describing the validation error for invalid keys.
         *
         * @param keyValidator The key validator.
         * @return This builder for chaining.
         *
         * @since 5.7
         */
        @ApiStatus.AvailableSince("5.7")
        public @NotNull Builder<K, V> keyValidator(@NotNull Function<K, Try<Void>> keyValidator) {
            this.keyValidator = keyValidator;
            return self();
        }

        /**
         * Sets a validator for values.
         *
         * @param valueValidator The value validator.
         * @return This builder for chaining.
         *
         * @since 5.7
         */
        @ApiStatus.AvailableSince("5.7")
        public @NotNull Builder<K, V> valueValidator(@NotNull Function<V, Try<Void>> valueValidator) {
            this.valueValidator = valueValidator;
            return self();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull MapConfigNode<K, V> build() {
            return new MapConfigNode<>(
                    path,
                    comments.toArray(new String[0]),
                    name,
                    type,
                    aliases,
                    defaultValue,
                    suggester,
                    stringParser,
                    serializer,
                    validator,
                    onLoad,
                    onLoadAndChange,
                    onChange,
                    keyType,
                    valueType,
                    keySuggester,
                    valueSuggester,
                    keyStringParser,
                    valueStringParser,
                    keySerializer,
                    valueSerializer,
                    keyValidator,
                    valueValidator);
        }
    }
}
