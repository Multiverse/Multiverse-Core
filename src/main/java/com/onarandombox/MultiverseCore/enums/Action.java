/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.enums;

/**
 * A enum containing all actions that can be used to modify world-properties.
 */
public enum Action {
    /**
     * Setting a property.
     */
    Set,
    /**
     * Adding something to a list-property.
     */
    Add,
    /**
     * Removing something from a list-property.
     */
    Remove,
    /**
     * Clearing a list-property.
     */
    Clear
}
