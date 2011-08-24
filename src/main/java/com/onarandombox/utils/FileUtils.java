package com.onarandombox.utils;

import java.io.File;

public class FileUtils {
    /*
     * Delete a folder Courtesy of: lithium3141
     * 
     * @param file The folder to delete
     * 
     * @return true if success
     */
    public static boolean deleteFolder(File file) {
        if (file.exists()) {
            // If the file exists, and it has more than one file in it.
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    if (!FileUtils.deleteFolder(f)) {
                        return false;
                    }
                }
            }
            file.delete();
            return !file.exists();
        } else {
            return false;
        }
    }

}
