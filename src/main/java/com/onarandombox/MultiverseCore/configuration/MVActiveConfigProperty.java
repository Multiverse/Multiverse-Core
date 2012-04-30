/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2012.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.configuration;

/**
 * An "active" {@link MVConfigProperty} that uses the specified method to be "actually" set.
 * @param <T> The type of the config-property.
 * @deprecated This is deprecated.
 * @see MVConfigProperty
 */
@Deprecated
public interface MVActiveConfigProperty<T> extends MVConfigProperty<T> {
    /**
     * Gets the method that will be executed.
     *
     * @return The name of the method in MVWorld to be called.
     */
    String getMethod();

    /**
     * Sets the method that will be executed.
     *
     * @param methodName The name of the method in MVWorld to be called.
     */
    void setMethod(String methodName);

    /**
     * Returns the class of the object we're looking at.
     * @return the class of the object we're looking at.
     */
    Class<?> getPropertyClass();
}
