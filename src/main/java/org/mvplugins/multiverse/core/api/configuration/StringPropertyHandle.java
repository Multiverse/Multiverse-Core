package org.mvplugins.multiverse.core.api.configuration;

import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Handles setting of config with string names and values.
 *
 * @since 5.0
 */
public interface StringPropertyHandle {
    /**
     * Gets the names of all properties in this handle.
     *
     * @return The names of all properties in this handle.
     * @since 5.0
     */
    Collection<String> getAllPropertyNames();

    /**
     * Gets the names of all properties in this handle that can be modified by the given action.
     *
     * @param action The action to perform.
     * @return The names of all properties in this handle that can be modified by the given action.
     * @since 5.0
     */
    Collection<String> getModifiablePropertyNames(PropertyModifyAction action);

    /**
     * Gets the type of property.
     *
     * @param name The name of the property.
     * @return The type of the property, or an error if the property was not found.
     * @since 5.0
     */
    Try<Class<?>> getPropertyType(@Nullable String name);

    /**
     * Auto-complete suggestions for a property.
     *
     * @param name  The name of the node.
     * @param input The current user input.
     * @return A collection of possible string values.
     * @since 5.0
     */
    Collection<String> getSuggestedPropertyValue(
            @Nullable String name, @Nullable String input, @NotNull PropertyModifyAction action);

    /**
     * Gets the value of a node, if the node has a default value, it will be returned if the node is not found.
     *
     * @param name The name of the node.
     * @return The value of the node, or an error if the node was not found.
     * @since 5.0
     */
    Try<Object> getProperty(@Nullable String name);

    /**
     * Sets the value of a node.
     *
     * @param name  The name of the node.
     * @param value The value to set.
     * @return Empty try if the value was set, try containing an error otherwise.
     * @since 5.0
     */
    Try<Void> setProperty(@Nullable String name, @Nullable Object value);

    /**
     * Adds a value to a list node.
     *
     * @param name  The name of the node.
     * @param value The value to add.
     * @return Empty try if the value was added, try containing an error otherwise.
     * @since 5.0
     */
    Try<Void> addProperty(@Nullable String name, @Nullable Object value);

    /**
     * Removes a value from a list node.
     *
     * @param name  The name of the node.
     * @param value The value to remove.
     * @return Empty try if the value was removed, try containing an error otherwise.
     * @since 5.0
     */
    Try<Void> removeProperty(@Nullable String name, @Nullable Object value);

    /**
     * Resets the value of a node to its default value.
     *
     * @param name The name of the node.
     * @return Empty try if the value was reset, try containing an error otherwise.
     * @since 5.0
     */
    Try<Void> resetProperty(@Nullable String name);

    /**
     * Modifies the value of a node based on the given action.
     *
     * @param name   The name of the node.
     * @param value  The value to modify.
     * @param action The action to perform.
     * @return Empty try if the value was modified, try containing an error otherwise.
     * @since 5.0
     */
    Try<Void> modifyProperty(
            @Nullable String name, @Nullable Object value, @NotNull PropertyModifyAction action);

    /**
     * Sets the string value of a node.
     *
     * @param name  The name of the node.
     * @param value The string value to set.
     * @return Empty try if the value was set, try containing an error otherwise.
     * @since 5.0
     */
    Try<Void> setPropertyString(@Nullable String name, @Nullable String value);

    /**
     * Adds a string value to a list node.
     *
     * @param name  The name of the node.
     * @param value The string value to add.
     * @return Empty try if the value was added, try containing an error otherwise.
     * @since 5.0
     */
    Try<Void> addPropertyString(@Nullable String name, @Nullable String value);

    /**
     * Removes a string value from a list node.
     *
     * @param name  The name of the node.
     * @param value The string value to remove.
     * @return Empty try if the value was removed, try containing an error otherwise.
     * @since 5.0
     */
    Try<Void> removePropertyString(@Nullable String name, @Nullable String value);

    /**
     * Modifies the value of a node based on the given action.
     *
     * @param name   The name of the node.
     * @param value  The string value to modify.
     * @param action The action to perform.
     * @return Empty try if the value was modified, try containing an error otherwise.
     * @since 5.0
     */
    Try<Void> modifyPropertyString(
            @Nullable String name, @Nullable String value, @NotNull PropertyModifyAction action);
}
