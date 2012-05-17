/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.configuration;

/**
 * A generic config-property.
 *
 * @param <T> The type of the config-property.
 * @deprecated This is deprecated.
 */
@Deprecated
public interface MVConfigProperty<T> {
    /**
     * Gets the name of this property.
     *
     * @return The name of this property.
     */
    String getName();

    /**
     * Gets the value of this property.
     *
     * @return The value of this property.
     */
    T getValue();

    /**
     * Gets the string representation of this value.
     *
     * @return The value of this property as a string.
     */
    String toString();

    /**
     * Gets the help string for this.
     *
     * @return The value of this property as a string.
     */
    String getHelp();

    /**
     * Sets the value of this property.
     *
     * @param value The T representation of this value.
     * @return True the value was successfully set.
     */
    boolean setValue(T value);

    /**
     * This parseValue should be used with strings.
     *
     * @param value The string representation of the value to set.
     *
     * @return True if the value was set, false if not.
     */
    boolean parseValue(String value);

    /**
     * Gets the name of the config-node that this {@link MVConfigProperty} is saved as.
     *
     * @return The name of the config-node that this {@link MVConfigProperty} is saved as.
     */
    String getConfigNode();
}
