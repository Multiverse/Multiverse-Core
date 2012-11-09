/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.mvplugin.impl;

/**
 * Thrown when a world-property doesn't exist.
 */
public class UnknownPropertyException extends Exception {
    public UnknownPropertyException(String name) {
        super(name);
    }

    public UnknownPropertyException(String name, Throwable cause) {
        super(name, cause);
    }
}
