package org.mvplugins.multiverse.core.world.helpers;

import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.utils.compatibility.BukkitCompatibility;
import org.mvplugins.multiverse.core.world.WorldManager;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Tool that traversals the server folders to find all potential worlds that can be imported, based on folder contents
 * and name.
 *
 * @since 5.7
 */
@ApiStatus.AvailableSince("5.7")
@Service
public final class PotentialWorldFinder {

    private static final int MAX_RECURSION_DEPTH = 3;
    private static final int MAX_FOLDER_TRAVERSAL_LIMIT = 100;

    private final WorldManager worldManager;
    private final WorldNameChecker worldNameChecker;

    @Inject
    private PotentialWorldFinder(@NotNull WorldManager worldManager, @NotNull WorldNameChecker worldNameChecker) {
        this.worldManager = worldManager;
        this.worldNameChecker = worldNameChecker;
    }

    /**
     * Gets a list of all potential worlds that can be loaded from the server folders.
     * Checks based on folder contents and name.
     *
     * @return A list of all potential worlds.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @Unmodifiable
    @NotNull
    public List<String> findPotentialWorlds() {
        return BukkitCompatibility.isUsingNewDimensionStorage()
                ? Stream.concat(findFromDimensionsFolder(), findFromRootFolder()).toList()
                : findFromRootFolder().toList();
    }

    private Stream<String> findFromRootFolder() {
        Path worldContainer = Bukkit.getWorldContainer().toPath();
        return Arrays.stream(listFolders(worldContainer))
                .limit(MAX_FOLDER_TRAVERSAL_LIMIT)
                .filter(worldNameChecker::isValidWorldFolder)
                .map(File::getName)
                .filter(worldNameChecker::isValidWorldName)
                .filter(worldName -> !worldManager.isWorld(worldName));
    }

    private Stream<String> findFromDimensionsFolder() {
        Path worldContainer = BukkitCompatibility.getWorldFoldersDirectory();
        return Arrays.stream(listFolders(worldContainer))
                .limit(MAX_FOLDER_TRAVERSAL_LIMIT)
                .flatMap(namespaceFile -> recursiveFindFromNamespaceFolder("", namespaceFile.toPath(), 0)
                        .map(worldKey -> namespaceFile.getName() + ":" + worldKey))
                .filter(namespacedKey -> NamespacedKey.fromString(namespacedKey) != null)
                .filter(namespacedKey -> !worldManager.isWorld(namespacedKey));
    }

    private Stream<String> recursiveFindFromNamespaceFolder(String prefix, Path subWorldContainer, int depth) {
        if (depth >= MAX_RECURSION_DEPTH) {
            return Stream.empty();
        }
        return Arrays.stream(listFolders(subWorldContainer))
                .limit(MAX_FOLDER_TRAVERSAL_LIMIT)
                .flatMap(folder -> worldNameChecker.isValidWorldFolder(folder)
                        ? Stream.of(concatPath(prefix, folder.getName()))
                        : recursiveFindFromNamespaceFolder(
                                concatPath(prefix, folder.getName()), folder.toPath(), depth + 1));
    }

    private String concatPath(String prefix, String folderName) {
        return prefix.isEmpty() ? folderName : prefix + "/" + folderName;
    }

    private @NotNull File[] listFolders(Path folder) {
        return Option.of(folder.toFile().listFiles(File::isDirectory))
                .getOrElse(() -> new File[0]);
    }
}
