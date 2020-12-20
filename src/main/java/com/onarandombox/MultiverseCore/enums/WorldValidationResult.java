package com.onarandombox.MultiverseCore.enums;

public enum WorldValidationResult {
    /**
     * World folder does not exists.
     */
    DOES_NOT_EXIST,
    /**
     * Target file for a World is not a directory.
     */
    NOT_A_DIRECTORY,
    /**
     * World folder does not contain a .dat file.
     */
    FOLDER_LACK_DAT,
    /**
     * World name is a reserved server folder.
     */
    NAME_BLACKLISTED,
    /**
     * World name contains .dat.
     */
    NAME_CONTAINS_DAT,
    /**
     * World name contains invalid space and special characters.
     */
    NAME_INVALID,
    /**
     * World name/folder is valid.
     */
    VALID
}
