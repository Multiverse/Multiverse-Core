package com.onarandombox.MultiverseCore.utils;

import com.dumptruckman.minecraft.util.Logging;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class FileUtilsTest {

    private File parentDir;
    private File parentDirFile;
    private File childDir;
    private File childDirFile;
    private File dest;

    @Before
    public void setUp() throws Exception {
        parentDir = Files.createTempDirectory("parentDir").toFile();
        parentDirFile = new File(parentDir, "parentDirFile.txt");
        parentDirFile.createNewFile();
        childDir = Files.createTempDirectory(parentDir.toPath(), "childDir").toFile();
        childDirFile = new File(childDir, "childDirFile.txt");
        childDirFile.createNewFile();
        dest = Files.createTempDirectory("dest").toFile();

        assertTrue(parentDir.exists());
        assertTrue(parentDirFile.exists());
        assertTrue(childDir.exists());
        assertTrue(childDirFile.exists());
        assertTrue(dest.exists());
    }

    @After
    public void tearDown() throws Exception {
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(parentDir);
        } catch (IOException e) {
            if (parentDir.exists()) {
                throw e;
            }
        }
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(dest);
        } catch (IOException e) {
            if (parentDir.exists()) {
                throw e;
            }
        }
    }

    @Test
    public void deleteFolder() {
        FileUtils.deleteFolder(parentDir);
        assertFalse(parentDir.exists());
        assertFalse(parentDirFile.exists());
        assertFalse(childDir.exists());
        assertFalse(childDirFile.exists());
    }

    @Test
    public void deleteFolderContents() {
        FileUtils.deleteFolderContents(parentDir);
        assertTrue(parentDir.exists());
        assertFalse(parentDirFile.exists());
        assertFalse(childDir.exists());
        assertFalse(childDirFile.exists());
    }

    @Test
    public void copyFolder() {
        File destFile = new File(dest, "parentDirFile.txt");
        File destChildDir = new File(dest, "childDir");
        File destChildDirFile = new File(destChildDir, "childDirFile.txt");
        assertFalse(destFile.exists());
        assertFalse(destChildDir.exists());
        assertFalse(destChildDirFile.exists());
        FileUtils.copyFolder(parentDir, dest, Logging.getLogger());
    }
}