/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.exceptions;

/**
 * Thrown when a world-property doesn't exist.
 */
public class PropertyDoesNotExistException extends Exception {
    public PropertyDoesNotExistException(String name) {
        super(name);
    }

    public PropertyDoesNotExistException(String name, Throwable cause) {
        super(name, cause);
    }
}
