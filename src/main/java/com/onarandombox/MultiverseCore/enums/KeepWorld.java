package com.onarandombox.MultiverseCore.enums;

/**
 * Custom enum for method argument in deciding what to keep when deleting world files.
 */
public enum KeepWorld {
    /**
     * Keep paper's config file if it exists.
     */
    CONFIG,
    /**
     * Keeps the directory of the world folder, delete all the contents.
     */
    FOLDER,
    /**
     * Delete everything, including the directory.
     */
    NONE
}
