package org.mvplugins.multiverse.core.configuration.handle;

import java.util.Collection;
import java.util.Collections;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.configuration.node.ConfigNodeNotFoundException;
import org.mvplugins.multiverse.core.configuration.node.ListValueNode;
import org.mvplugins.multiverse.core.configuration.node.Node;
import org.mvplugins.multiverse.core.configuration.node.ValueNode;

/**
 * Handles setting of config with string names and values.
 */
public class StringPropertyHandle {
    private final @NotNull GenericConfigHandle<?> handle;

    /**
     * Creates a new string property handle.
     *
     * @param handle    The handle to wrap.
     */
    public StringPropertyHandle(@NotNull GenericConfigHandle<?> handle) {
        this.handle = handle;
    }

    /**
     * Gets the names of all properties in this handle.
     *
     * @return  The names of all properties in this handle.
     */
    public Collection<String> getAllPropertyNames() {
        return handle.getNodes().getNames();
    }

    /**
     * Gets the names of all properties in this handle that can be modified by the given action.
     *
     * @param action    The action to perform.
     * @return The names of all properties in this handle that can be modified by the given action.
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
     * Gets the type of property.
     *
     * @param name  The name of the property.
     * @return The type of the property, or an error if the property was not found.
     */
    public Try<Class<?>> getPropertyType(@Nullable String name) {
        return findNode(name, ValueNode.class).map(ValueNode::getType);
    }

    /**
     * Auto-complete suggestions for a property.
     *
     * @param name  The name of the node.
     * @param input The current user input.
     * @return A collection of possible string values.
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
     * Gets the value of a node, if the node has a default value, it will be returned if the node is not found.
     *
     * @param name  The name of the node.
     * @return The value of the node, or an error if the node was not found.
     */
    public Try<Object> getProperty(@Nullable String name) {
        return findNode(name, ValueNode.class).map(node -> handle.get(node));
    }

    /**
     * Sets the value of a node.
     *
     * @param name  The name of the node.
     * @param value The value to set.
     * @return Empty try if the value was set, try containing an error otherwise.
     */
    public Try<Void> setProperty(@Nullable String name, @Nullable Object value) {
        return findNode(name, ValueNode.class).flatMap(node -> handle.set(node, value));
    }

    /**
     * Adds a value to a list node.
     *
     * @param name  The name of the node.
     * @param value The value to add.
     * @return Empty try if the value was added, try containing an error otherwise.
     */
    public Try<Void> addProperty(@Nullable String name, @Nullable Object value) {
        return findNode(name, ListValueNode.class).flatMap(node -> handle.add(node, value));
    }

    /**
     * Removes a value from a list node.
     *
     * @param name  The name of the node.
     * @param value The value to remove.
     * @return Empty try if the value was removed, try containing an error otherwise.
     */
    public Try<Void> removeProperty(@Nullable String name, @Nullable Object value) {
        return findNode(name, ListValueNode.class).flatMap(node -> handle.remove(node, value));
    }

    /**
     * Resets the value of a node to its default value.
     *
     * @param name  The name of the node.
     * @return Empty try if the value was reset, try containing an error otherwise.
     */
    public Try<Void> resetProperty(@Nullable String name) {
        return findNode(name, ValueNode.class).flatMap(node -> handle.reset(node));
    }

    /**
     * Modifies the value of a node based on the given action.
     *
     * @param name      The name of the node.
     * @param value     The value to modify.
     * @param action    The action to perform.
     * @return Empty try if the value was modified, try containing an error otherwise.
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
     * Sets the string value of a node.
     *
     * @param name  The name of the node.
     * @param value The string value to set.
     * @return Empty try if the value was set, try containing an error otherwise.
     */
    public Try<Void> setPropertyString(@Nullable String name, @Nullable String value) {
        return findNode(name, ValueNode.class)
                .flatMap(node -> node.parseFromString(value)
                        .flatMap(parsedValue -> handle.set(node, parsedValue)));
    }

    /**
     * Adds a string value to a list node.
     *
     * @param name  The name of the node.
     * @param value The string value to add.
     * @return Empty try if the value was added, try containing an error otherwise.
     */
    public Try<Void> addPropertyString(@Nullable String name, @Nullable String value) {
        return findNode(name, ListValueNode.class)
                .flatMap(node -> node.parseItemFromString(value)
                        .flatMap(parsedValue -> handle.add(node, parsedValue)));
    }

    /**
     * Removes a string value from a list node.
     *
     * @param name  The name of the node.
     * @param value The string value to remove.
     * @return Empty try if the value was removed, try containing an error otherwise.
     */
    public Try<Void> removePropertyString(@Nullable String name, @Nullable String value) {
        return findNode(name, ListValueNode.class)
                .flatMap(node -> node.parseItemFromString(value)
                        .flatMap(parsedValue -> handle.remove(node, parsedValue)));
    }

    /**
     * Modifies the value of a node based on the given action.
     *
     * @param name      The name of the node.
     * @param value     The string value to modify.
     * @param action    The action to perform.
     * @return Empty try if the value was modified, try containing an error otherwise.
     */
    public Try<Void> modifyPropertyString(
            @Nullable String name, @Nullable String value, @NotNull PropertyModifyAction action) {
        return switch (action) {
            case SET -> setPropertyString(name, value);
            case ADD -> addPropertyString(name, value);
            case REMOVE -> removePropertyString(name, value);
            case RESET -> resetProperty(name);
            default -> Try.failure(new IllegalArgumentException("Unknown action: " + action));
        };
    }

    private <T extends Node> Try<T> findNode(@Nullable String name, @NotNull Class<T> type) {
        return handle.getNodes().findNode(name, type)
                .toTry(() -> new ConfigNodeNotFoundException(name));
    }
}
