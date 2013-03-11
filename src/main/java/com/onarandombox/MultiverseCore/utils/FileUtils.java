/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.dumptruckman.minecraft.util.Logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * File-utilities.
 */
public class FileUtils {
    protected FileUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Used to delete a folder.
     *
     * @param file The folder to delete.
     * @return true if the folder was successfully deleted.
     */
    public static boolean deleteFolder(File file) {
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(file);
            return true;
        } catch (IOException e) {
            Logging.warning(e.getMessage());
            return false;
        }
    }

    /**
     * Used to delete the contents of a folder, without deleting the folder itself.
     *
     * @param file The folder whose contents to delete.
     * @return true if the contents were successfully deleted
     */
    public static boolean deleteFolderContents(File file) {
        try {
            org.apache.commons.io.FileUtils.cleanDirectory(file);
            return true;
        } catch (IOException e) {
            Logging.warning(e.getMessage());
            return false;
        }
    }

    private static final int COPY_BLOCK_SIZE = 1024;

    /**
     * Helper method to copy the world-folder.
     * @param source Source-File
     * @param target Target-File
     * @param log A logger that logs the operation
     *
     * @return if it had success
     */
    public static boolean copyFolder(File source, File target, Logger log) {
        try {
            org.apache.commons.io.FileUtils.copyDirectory(source, target);
            return true;
        } catch (IOException e) {
            log.warning(e.getMessage());
            return false;
        }
    }
}
