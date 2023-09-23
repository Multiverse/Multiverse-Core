package org.mvplugins.multiverse.core.configuration.handle;

import java.util.Collection;
import java.util.Collections;

import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.configuration.node.ConfigNodeNotFoundException;
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
    public Collection<String> getPropertyNames() {
        return handle.getNodes().getNames();
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
    public Collection<String> getPropertySuggestedValues(@Nullable String name, @Nullable String input) {
        return findNode(name, ValueNode.class)
                .map(node -> node.suggest(input))
                .getOrElse(Collections.emptyList());
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
     * Sets the value of a node, if the validator is not null, it will be tested first.
     *
     * @param name  The name of the node.
     * @param value The value to set.
     * @return Empty try if the value was set, try containing an error otherwise.
     */
    public Try<Void> setProperty(@Nullable String name, @Nullable Object value) {
        return findNode(name, ValueNode.class).flatMap(node -> handle.set(node, value));
    }

    /**
     * Sets the string value of a node, if the validator is not null, it will be tested first.
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

    private <T extends Node> Try<T> findNode(@Nullable String name, @NotNull Class<T> type) {
        return handle.getNodes().findNode(name, type)
                .toTry(() -> new ConfigNodeNotFoundException(name));
    }
}
