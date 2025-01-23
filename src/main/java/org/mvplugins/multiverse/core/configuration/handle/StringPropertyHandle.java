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

    public Collection<String> getAllPropertyNames() {
        return handle.getNodes().getNames();
    }

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

    public Try<Class<?>> getPropertyType(@Nullable String name) {
        return findNode(name, ValueNode.class).map(ValueNode::getType);
    }

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

    public Try<Object> getProperty(@Nullable String name) {
        return findNode(name, ValueNode.class).map(node -> handle.get(node));
    }

    public Try<Void> setProperty(@Nullable String name, @Nullable Object value) {
        return findNode(name, ValueNode.class).flatMap(node -> handle.set(node, value));
    }

    public Try<Void> addProperty(@Nullable String name, @Nullable Object value) {
        return findNode(name, ListValueNode.class).flatMap(node -> handle.add(node, value));
    }

    public Try<Void> removeProperty(@Nullable String name, @Nullable Object value) {
        return findNode(name, ListValueNode.class).flatMap(node -> handle.remove(node, value));
    }

    public Try<Void> resetProperty(@Nullable String name) {
        return findNode(name, ValueNode.class).flatMap(node -> handle.reset(node));
    }

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

    public Try<Void> setPropertyString(@Nullable String name, @Nullable String value) {
        return findNode(name, ValueNode.class)
                .flatMap(node -> node.parseFromString(value)
                        .flatMap(parsedValue -> handle.set(node, parsedValue)));
    }

    public Try<Void> addPropertyString(@Nullable String name, @Nullable String value) {
        return findNode(name, ListValueNode.class)
                .flatMap(node -> node.parseItemFromString(value)
                        .flatMap(parsedValue -> handle.add(node, parsedValue)));
    }

    public Try<Void> removePropertyString(@Nullable String name, @Nullable String value) {
        return findNode(name, ListValueNode.class)
                .flatMap(node -> node.parseItemFromString(value)
                        .flatMap(parsedValue -> handle.remove(node, parsedValue)));
    }

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

    private <T extends Node> Try<T> findNode(@Nullable String name, @NotNull Class<T> type) {
        return handle.getNodes().findNode(name, type)
                .toTry(() -> new ConfigNodeNotFoundException(name));
    }
}
