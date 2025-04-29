package org.mvplugins.multiverse.core.config.handle;

import java.util.Collection;
import java.util.Collections;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.config.node.ConfigNodeNotFoundException;
import org.mvplugins.multiverse.core.config.node.ListValueNode;
import org.mvplugins.multiverse.core.config.node.Node;
import org.mvplugins.multiverse.core.config.node.ValueNode;

/**
 * Used to map property string name to config node and parse string value to node's class type.
 */
@SuppressWarnings("unchecked")
public class StringPropertyHandle {
    private final @NotNull BaseConfigurationHandle<?> handle;

    /**
     * Creates a new string property handle.
     *
     * @param handle    The handle to wrap.
     */
    public StringPropertyHandle(@NotNull BaseConfigurationHandle<?> handle) {
        this.handle = handle;
    }

    /**
     * Get all property with names. If {@link ValueNode#getName()} returns null, it will be ignored.
     *
     * @return All property names
     */
    public Collection<String> getAllPropertyNames() {
        return handle.getNodes().getNames();
    }

    /**
     * Get property names that can be modified. ADD and REMOVE actions can only be used on list nodes.
     * @param action The target action
     * @return The property names modifiable for the action.
     */
    public Collection<String> getModifiablePropertyNames(PropertyModifyAction action) {
        return switch (action) {
            case SET, RESET -> handle.getNodes().getNames();

            case ADD, REMOVE -> handle.getNodes().stream()
                    .filter(node -> node instanceof ListValueNode)
                    .map(node -> ((ValueNode<?>) node).getName())
                    .filter(Option::isDefined)
                    .map(Option::get)
                    .toList();

            default -> Collections.emptyList();
        };
    }

    /**
     * Gets the type of the property identified by the given name.
     *
     * @param name The name of the property.
     * @return A Try containing the class type of the property if found.
     */
    public Try<Class<?>> getPropertyType(@Nullable String name) {
        return findNode(name, ValueNode.class).map(ValueNode::getType);
    }

    /**
     * Suggests property values for command auto-complete based on the input and action type.
     *
     * @param name The name of the property.
     * @param input The input value to suggest based on.
     * @param action The modification action being performed.
     * @return A collection of suggested values.
     */
    public Collection<String> getSuggestedPropertyValue(
            @Nullable String name, @Nullable String input, @NotNull PropertyModifyAction action) {
        return switch (action) {
            case SET -> findNode(name, ValueNode.class)
                    .map(node -> node.suggest(input))
                    .getOrElse(Collections.emptyList());

            case ADD -> findNode(name, ListValueNode.class)
                    .map(node -> node.suggestItem(input))
                    .getOrElse(Collections.emptyList());

            case REMOVE -> findNode(name, ListValueNode.class)
                    .map(node -> handle.get((ListValueNode<?>) node))
                    .map(valueList -> valueList.stream()
                            .map(String::valueOf)
                            .toList())
                    .getOrElse(Collections.emptyList());

            default -> Collections.emptyList();
        };
    }

    /**
     * Retrieves the value of the specified property name.
     *
     * @param name The name of the property.
     * @return A Try containing the property value if found.
     */
    public Try<Object> getProperty(@Nullable String name) {
        return findNode(name, ValueNode.class).map(handle::get);
    }

    /**
     * Sets the value of the specified property name.
     *
     * @param name The name of the property.
     * @param value The value to set.
     * @return A Try indicating success or failure.
     */
    public Try<Void> setProperty(@Nullable String name, @Nullable Object value) {
        return findNode(name, ValueNode.class).flatMap(node -> handle.set(node, value));
    }

    /**
     * Adds a value to a list property name.
     *
     * @param name The name of the property.
     * @param value The value to add.
     * @return A Try indicating success or failure.
     */
    public Try<Void> addProperty(@Nullable String name, @Nullable Object value) {
        return findNode(name, ListValueNode.class).flatMap(node -> handle.add(node, value));
    }

    /**
     * Removes a value from a list property name.
     *
     * @param name The name of the property.
     * @param value The value to remove.
     * @return A Try indicating success or failure.
     */
    public Try<Void> removeProperty(@Nullable String name, @Nullable Object value) {
        return findNode(name, ListValueNode.class).flatMap(node -> handle.remove(node, value));
    }

    /**
     * Resets the property name to its default value.
     *
     * @param name The name of the property.
     * @return A Try indicating success or failure.
     */
    public Try<Void> resetProperty(@Nullable String name) {
        return findNode(name, ValueNode.class).flatMap(handle::reset);
    }

    /**
     * Modifies a property name based on the given action.
     *
     * @param name The name of the property.
     * @param value The new value (if applicable).
     * @param action The modification action.
     * @return A Try indicating success or failure.
     */
    public Try<Void> modifyProperty(
            @Nullable String name, @Nullable Object value, @NotNull PropertyModifyAction action) {
        return switch (action) {
            case SET -> setProperty(name, value);
            case ADD -> addProperty(name, value);
            case REMOVE -> removeProperty(name, value);
            case RESET -> resetProperty(name);
            default -> Try.failure(new IllegalArgumentException("Unknown action: " + action));
        };
    }

    /**
     * Sets the property value from a string representation.
     *
     * @param name The name of the property.
     * @param value The string value to set.
     * @return A Try indicating success or failure.
     */
    public Try<Void> setPropertyString(@Nullable String name, @Nullable String value) {
        return findNode(name, ValueNode.class)
                .flatMap(node -> node.parseFromString(value)
                        .flatMap(parsedValue -> handle.set(node, parsedValue)));
    }

    /**
     * Adds a value to a list property using its string representation.
     *
     * @param name The name of the property.
     * @param value The string value to add.
     * @return A Try indicating success or failure.
     */
    public Try<Void> addPropertyString(@Nullable String name, @Nullable String value) {
        return findNode(name, ListValueNode.class)
                .flatMap(node -> node.parseItemFromString(value)
                        .flatMap(parsedValue -> handle.add(node, parsedValue)));
    }

    /**
     * Removes a value from a list property using its string representation.
     *
     * @param name The name of the property.
     * @param value The string value to remove.
     * @return A Try indicating success or failure.
     */
    public Try<Void> removePropertyString(@Nullable String name, @Nullable String value) {
        return findNode(name, ListValueNode.class)
                .flatMap(node -> node.parseItemFromString(value)
                        .flatMap(parsedValue -> handle.remove(node, parsedValue)));
    }

    /**
     * Modifies a property using a string value based on the given action.
     *
     * @param name The name of the property.
     * @param value The string value (if applicable).
     * @param action The modification action.
     * @return A Try indicating success or failure.
     */
    public Try<Void> modifyPropertyString(
            @Nullable String name, @Nullable String value, @NotNull PropertyModifyAction action) {
        if (action.isRequireValue() && (value == null)) {
            return Try.failure(new IllegalArgumentException("Value is required for PropertyModifyAction: " + action));
        }
        return switch (action) {
            case SET -> setPropertyString(name, value);
            case ADD -> addPropertyString(name, value);
            case REMOVE -> removePropertyString(name, value);
            case RESET -> resetProperty(name);
            default -> Try.failure(new IllegalArgumentException("Unknown action: " + action));
        };
    }

    /**
     * Finds a configuration node by name and type.
     *
     * @param name The name of the node.
     * @param type The expected class type of the node.
     * @param <T> The type of node.
     * @return A Try containing the found node or a failure if not found.
     */
    private <T extends Node> Try<T> findNode(@Nullable String name, @NotNull Class<T> type) {
        return handle.getNodes().findNode(name, type)
                .toTry(() -> new ConfigNodeNotFoundException(name));
    }
}
