package org.mvplugins.multiverse.core.configuration.node;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ListConfigNode<I> extends ConfigNode<List<I>> implements ListValueNode<I> {

    /**
     * Creates a new builder for a {@link ConfigNode}.
     *
     * @param path  The path of the node.
     * @param type  The type of the value.
     * @param <I>   The type of the value.
     * @return The new builder.
     */
    public static @NotNull <I> Builder<I, ? extends Builder<I, ?>> listBuilder(
            @NotNull String path,
            @NotNull Class<I> type) {
        return new Builder<>(path, type);
    }

    protected final Class<I> itemType;
    protected final NodeSerializer<I> itemSerializer;
    protected final Function<I, Try<Void>> itemValidator;
    protected final BiConsumer<I, I> onSetItemValue;

    protected ListConfigNode(
            @NotNull String path,
            @NotNull String[] comments,
            @Nullable String name,
            @NotNull Class<List<I>> type,
            @Nullable Supplier<List<I>> defaultValueSupplier,
            @Nullable NodeSerializer<List<I>> serializer,
            @Nullable Function<List<I>, Try<Void>> validator,
            @Nullable BiConsumer<List<I>, List<I>> onSetValue,
            @NotNull Class<I> itemType,
            @Nullable NodeSerializer<I> itemSerializer,
            @Nullable Function<I, Try<Void>> itemValidator,
            @Nullable BiConsumer<I, I> onSetItemValue) {
        super(path, comments, name, type, defaultValueSupplier, serializer, validator, onSetValue);
        this.itemType = itemType;
        this.itemSerializer = itemSerializer;
        this.itemValidator = itemValidator;
        this.onSetItemValue = onSetItemValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Class<I> getItemType() {
        return itemType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable NodeSerializer<I> getItemSerializer() {
        return itemSerializer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> validateItem(@Nullable I value) {
        if (itemValidator != null) {
            return itemValidator.apply(value);
        }
        return Try.success(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSetItemValue(@Nullable I oldValue, @Nullable I newValue) {
        if (onSetItemValue != null) {
            onSetItemValue.accept(oldValue, newValue);
        }
    }

    public static class Builder<I, B extends ListConfigNode.Builder<I, B>> extends ConfigNode.Builder<List<I>, B> {

        protected final @NotNull Class<I> itemType;
        protected NodeSerializer<I> itemSerializer;
        protected Function<I, Try<Void>> itemValidator;
        protected BiConsumer<I, I> onSetItemValue;

        /**
         * Creates a new builder.
         *
         * @param path      The path of the node.
         * @param itemType  The type of the item value in list.
         */
        protected Builder(@NotNull String path, @NotNull Class<I> itemType) {
            //noinspection unchecked
            super(path, (Class<List<I>>) (Object) List.class);
            this.itemType = itemType;
            this.defaultValueSupplier = () -> (List<I>) new ArrayList<Object>();
        }

        /**
         * Sets the serializer for the node.
         *
         * @param serializer The serializer for the node.
         * @return This builder.
         */
        public @NotNull B itemSerializer(@Nullable NodeSerializer<I> serializer) {
            this.itemSerializer = serializer;
            return self();
        }

        /**
         * Sets the validator for the node.
         *
         * @param validator The validator for the node.
         * @return This builder.
         */
        public @NotNull B itemValidator(@Nullable Function<I, Try<Void>> validator) {
            this.itemValidator = validator;
            if (validator == null) {
                setDefaultValidator();
            }
            return self();
        }

        private void setDefaultValidator() {
            this.validator = value -> {
                if (value != null) {
                    return Try.sequence(value.stream().map(itemValidator).toList()).map(v -> null);
                }
                return Try.success(null);
            };
        }

        /**
         * Sets the onSetValue for the node.
         *
         * @param onSetValue The onSetValue for the node.
         * @return This builder.
         */
        public @NotNull B onSetItemValue(@Nullable BiConsumer<I, I> onSetValue) {
            this.onSetItemValue = onSetValue;
            if (onSetValue == null) {
                setDefaultOnSetValue();
            }
            return self();
        }

        private void setDefaultOnSetValue() {
            this.onSetValue = (oldValue, newValue) -> {
                if (oldValue != null) {
                    oldValue.stream()
                            .filter(value -> !newValue.contains(value))
                            .forEach(item -> onSetItemValue.accept(item, null));
                }
                newValue.forEach(item -> onSetItemValue.accept(null, item));
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull ListConfigNode<I> build() {
            return new ListConfigNode<>(
                    path,
                    comments.toArray(new String[0]),
                    name,
                    type,
                    defaultValueSupplier,
                    serializer,
                    validator,
                    onSetValue,
                    itemType,
                    itemSerializer,
                    itemValidator,
                    onSetItemValue);
        }
    }
}
