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
                .filter(worldNameChecker::isValidWorldFolder)
                .map(File::getName)
                .filter(worldNameChecker::isValidWorldName)
                .filter(worldName -> !worldManager.isWorld(worldName));
    }

    private Stream<String> findFromDimensionsFolder() {
        Path worldContainer = BukkitCompatibility.getWorldFoldersDirectory();
        return Arrays.stream(listFolders(worldContainer))
                .map(File::getName)
                .flatMap(namespace -> Arrays.stream(listFolders(worldContainer.resolve(namespace)))
                        .filter(worldNameChecker::isValidWorldFolder)
                        .map(File::getName)
                        .map(key -> namespace + ":" + key))
                .filter(namespacedKey -> NamespacedKey.fromString(namespacedKey) != null)
                .filter(namespacedKey -> !worldManager.isWorld(namespacedKey));
    }

    private @NotNull File[] listFolders(Path folder) {
        return Option.of(folder.toFile().listFiles(File::isDirectory))
                .getOrElse(() -> new File[0]);
    }
}
