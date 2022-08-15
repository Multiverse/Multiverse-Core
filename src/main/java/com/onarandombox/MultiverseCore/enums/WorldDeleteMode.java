package com.onarandombox.MultiverseCore.enums;

/**
 * Custom enum for method argument in deciding what to delete when deleting a world
 */
public enum WorldDeleteMode {
    /**
     * Delete all contents in the directory except for Paper's configuration file.
     */
    WORLD,
    /**
     * Delete all contents in the directory.
     */
    CONFIG_AND_WORLD,
    /**
     * Delete everything, including the directory.
     */
    ALL
}
