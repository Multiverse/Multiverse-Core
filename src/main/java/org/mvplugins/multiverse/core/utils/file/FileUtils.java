/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.utils.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.MultiverseCore;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;

/**
 * File-utilities.
 */
@Service
//todo: Review duplicated methods in FileManipulator
public class FileUtils {

    private final File serverFolder;
    private final File bukkitYml;
    private final File serverProperties;

    @Inject
    protected FileUtils(@NotNull MultiverseCore plugin) {
        this.serverFolder = getServerFolder();
        Logging.fine("Server folder: " + this.serverFolder);
        this.bukkitYml = findFileFromServerDirectory("bukkit.yml");
        this.serverProperties = findFileFromServerDirectory("server.properties");
    }

    public File getServerFolder() {
        return new File(System.getProperty("user.dir"));
    }

    @Nullable
    private File findFileFromServerDirectory(String fileName) {
        File[] files;
        try {
            files = this.serverFolder.listFiles((file, s) -> s.equalsIgnoreCase(fileName));
        } catch (Exception e) {
            Logging.severe("Could not read from server directory. Unable to locate file: %s", fileName);
            Logging.severe(e.getMessage());
            return null;
        }

        // TODO: Implement binary search to find file, config option or use reflections to get it from configuration on CraftServer
        if (files != null && files.length == 1) {
            return files[0];
        }
        Logging.warning("Unable to locate file from server directory: %s", fileName);
        return null;
    }


    /**
     * Used to delete a folder.
     *
     * @param file The folder to delete.
     * @return true if the folder was successfully deleted.
     */
    public boolean deleteFolder(File file) {
        try (Stream<Path> files = Files.walk(file.toPath())) {
            files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
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
    public boolean deleteFolderContents(File file) {
        try (Stream<Path> files = Files.walk(file.toPath())){
            files.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .filter(f -> !f.equals(file))
                    .forEach(File::delete);
            return true;
        } catch (IOException e) {
            Logging.warning(e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to copy the world-folder.
     * @param source Source-File
     * @param target Target-File
     *
     * @return true if it had success
     */
    public boolean copyFolder(File source, File target) {
        return copyFolder(source, target, null);
    }

    /**
     * Helper method to copy the world-folder.
     * @param source Source-File
     * @param target Target-File
     * @param excludeFiles files to ignore and not copy over to Target-File
     *
     * @return true if it had success
     */
    public boolean copyFolder(File source, File target, List<String> excludeFiles) {
        Path sourceDir = source.toPath();
        Path targetDir = target.toPath();

        try {
            Files.walkFileTree(sourceDir, new CopyDirFileVisitor(sourceDir, targetDir, excludeFiles));
            return true;
        } catch (IOException e) {
            Logging.warning("Unable to copy directory", e);
            return false;
        }
    }

    @Nullable
    public File getBukkitConfig() {
        return this.bukkitYml;
    }

    @Nullable
    public File getServerProperties() {
        return this.serverProperties;
    }

    private static class CopyDirFileVisitor extends SimpleFileVisitor<Path> {

        private final Path sourceDir;
        private final Path targetDir;
        private final List<String> excludeFiles;

        private CopyDirFileVisitor(Path sourceDir, Path targetDir, List<String> excludeFiles) {
            this.sourceDir = sourceDir;
            this.targetDir = targetDir;
            this.excludeFiles = excludeFiles;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            Path newDir = targetDir.resolve(sourceDir.relativize(dir));
            if (!Files.isDirectory(newDir)) {
                Files.createDirectory(newDir);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            // Pass files that are set to ignore
            if (excludeFiles != null && excludeFiles.contains(file.getFileName().toString()))
                return FileVisitResult.CONTINUE;
            // Copy the files
            Path targetFile = targetDir.resolve(sourceDir.relativize(file));
            Files.copy(file, targetFile, COPY_ATTRIBUTES);
            return FileVisitResult.CONTINUE;
        }
    }
}