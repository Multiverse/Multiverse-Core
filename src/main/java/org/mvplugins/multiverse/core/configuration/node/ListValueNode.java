package org.mvplugins.multiverse.core.configuration.node;

import java.util.List;

import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ListValueNode<I> extends ValueNode<List<I>> {

    /**
     * Gets the class type of list item.
     *
     * @return The class type of list item.
     */
    @NotNull Class<I> getItemType();

    /**
     * Gets the serializer for this node.
     *
     * @return  The serializer for this node.
     */
    @Nullable NodeSerializer<I> getItemSerializer();

    /**
     * Validates the value of this node.
     *
     * @param value The value to validate.
     * @return True if the value is valid, false otherwise.
     */
    Try<Void> validateItem(@Nullable I value);

    /**
     * Called when the value of this node is set.
     *
     * @param oldValue The old value.
     * @param newValue The new value.
     */
    void onSetItemValue(@Nullable I oldValue, @Nullable I newValue);
}
