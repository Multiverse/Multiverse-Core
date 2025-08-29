/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.config.CoreConfig;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;

/**
 * File-utilities.
 */
@Service
public final class FileUtils {

    private final CoreConfig config;
    private final File serverFolder;
    private File bukkitYml;
    private File serverProperties;

    @Inject
    FileUtils(CoreConfig config) {
        this.config = config;
        this.serverFolder = new File(System.getProperty("user.dir"));
        Logging.finer("Server folder: " + this.serverFolder);
    }

    /**
     * The root server folder where Multiverse-Core is installed.
     *
     * @return The root server folder
     */
    public File getServerFolder() {
        return this.serverFolder;
    }

    /**
     * The bukkit.yml file
     *
     * @return The bukkit.yml file if exist, else null.
     */
    public @Nullable File getBukkitConfig() {
        if (this.bukkitYml == null) {
            this.bukkitYml = findFileFromServerDirectory(config.getBukkitYmlPath());
            Logging.finer("Bukkit.yml: " + this.bukkitYml);
        }
        return this.bukkitYml;
    }

    /**
     * The server.properties file
     *
     * @return The server.properties file if exist, else null.
     */
    public @Nullable File getServerProperties() {
        if (this.serverProperties == null) {
            this.serverProperties = findFileFromServerDirectory(config.getServerPropertiesPath());
            Logging.finer("server.properties: %s", this.serverProperties);
        }
        return this.serverProperties;
    }

    private @Nullable File findFileFromServerDirectory(String fileName) {
        if (this.serverFolder == null) {
            Logging.warning("Unable to locate server directory.");
            return null;
        }
        File file = new File(this.serverFolder, fileName);
        if (!file.exists()) {
            Logging.warning("Unable to locate file from server directory: %s", fileName);
            return null;
        }
        return file;
    }

    /**
     * Deletes the given folder completely.
     *
     * @param file  The folder to delete.
     * @return A {@link Try} that will contain {@code null} if the folder was deleted successfully, or an exception if
     *         the folder could not be deleted.
     */
    public Try<Void> deleteFolder(File file) {
        return deleteFolder(file, Collections.emptyList());
    }

    public Try<Void> deleteFolder(File file, Collection<String> keepFiles) {
        return deleteFolder(file.toPath(), keepFiles);
    }

    /**
     * Deletes the given folder completely.
     *
     * @param path  The folder to delete.
     * @return A {@link Try} that will contain {@code null} if the folder was deleted successfully, or an exception if
     *         the folder could not be deleted.
     */
    public Try<Void> deleteFolder(Path path, Collection<String> keepFiles) {
        try (Stream<Path> files = Files.walk(path)) {
            files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(file -> {
                if (!isDirectoryEmpty(file)) {
                    Logging.finest("Cannot delete folder as it is not empty: " + file);
                    return;
                }
                if (file.isFile() && keepFiles.contains(file.getName())) {
                    Logging.finest("Keeping file: " + file);
                    return;
                }
                if (!file.delete()) {
                    throw new IllegalStateException("Failed to delete file: " + file);
                }
            });
            return Try.success(null);
        } catch (IOException e) {
            Logging.severe("Failed to delete folder: " + path.toAbsolutePath());
            e.printStackTrace();
            return Try.failure(e);
        }
    }

    private boolean isDirectoryEmpty(File file) {
        if (!file.isDirectory()) {
            return true;
        }
        try (Stream<Path> entries = Files.list(file.toPath())) {
            return entries.findFirst().isEmpty();
        } catch (IOException e) {
            return true;
        }
    }

    /**
     * Copies all the content of the given folder to the given target folder.
     *
     * @param sourceDir The folder to copy.
     * @param targetDir The target folder to copy to.
     * @return A {@link Try} that will contain {@code null} if the folder was copied successfully, or an exception if
     *         the folder could not be copied.
     */
    public Try<Void> copyFolder(File sourceDir, File targetDir) {
        return copyFolder(sourceDir.toPath(), targetDir.toPath(), Collections.emptyList());
    }

    /**
     * Copies most of the content of the given folder to the given target folder, except the list of excluded files
     * specified.
     *
     * @param sourceDir     The folder to copy.
     * @param targetDir     The target folder to copy to.
     * @param excludeFiles  The list of files to exclude from copying.
     * @return A {@link Try} that will contain {@code null} if the folder was copied successfully, or an exception if
     */
    public Try<Void> copyFolder(File sourceDir, File targetDir, List<String> excludeFiles) {
        return copyFolder(sourceDir.toPath(), targetDir.toPath(), excludeFiles);
    }

    /**
     * Copies all the content of the given folder to the given target folder.
     *
     * @param sourceDir The folder to copy.
     * @param targetDir The target folder to copy to.
     * @return A {@link Try} that will contain {@code null} if the folder was copied successfully, or an exception if
     *         the folder could not be copied.
     */
    public Try<Void> copyFolder(Path sourceDir, Path targetDir) {
        return copyFolder(sourceDir, targetDir, Collections.emptyList());
    }

    /**
     * Copies most of the content of the given folder to the given target folder, except the list of excluded files
     * specified.
     *
     * @param sourceDir     The folder to copy.
     * @param targetDir     The target folder to copy to.
     * @param excludeFiles  The list of files to exclude from copying.
     * @return A {@link Try} that will contain {@code null} if the folder was copied successfully, or an exception if
     */
    public Try<Void> copyFolder(Path sourceDir, Path targetDir, List<String> excludeFiles) {
        return Try.run(() -> Files.walkFileTree(sourceDir, new CopyDirFileVisitor(sourceDir, targetDir, excludeFiles)))
                .onFailure(e -> {
                    Logging.severe("Failed to copy folder: " + sourceDir.toAbsolutePath());
                    e.printStackTrace();
                });
    }

    private static final class CopyDirFileVisitor extends SimpleFileVisitor<Path> {

        private final Path sourceDir;
        private final Path targetDir;
        private final List<String> excludeFiles;

        private CopyDirFileVisitor(@NotNull Path sourceDir, @NotNull Path targetDir, @NotNull List<String> excludeFiles) {
            this.sourceDir = sourceDir;
            this.targetDir = targetDir;
            this.excludeFiles = excludeFiles;
        }

        @Override
        public @NotNull FileVisitResult preVisitDirectory(Path dir, @NotNull BasicFileAttributes attrs) throws IOException {
            Path newDir = targetDir.resolve(sourceDir.relativize(dir));
            if (!Files.isDirectory(newDir)) {
                Files.createDirectory(newDir);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public @NotNull FileVisitResult visitFile(Path file, @NotNull BasicFileAttributes attrs) throws IOException {
            // Pass files that are set to ignore
            if (excludeFiles.contains(file.getFileName().toString())) {
                Logging.finest("Ignoring file: " + file.getFileName());
                return FileVisitResult.CONTINUE;
            }
            // Copy the files
            Path targetFile = targetDir.resolve(sourceDir.relativize(file));
            Files.copy(file, targetFile, COPY_ATTRIBUTES);
            return FileVisitResult.CONTINUE;
        }
    }
}
