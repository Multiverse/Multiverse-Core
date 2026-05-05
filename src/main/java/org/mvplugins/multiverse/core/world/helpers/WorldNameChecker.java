package org.mvplugins.multiverse.core.world.helpers;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.vavr.control.Option;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.utils.REPatterns;
import org.mvplugins.multiverse.core.utils.compatibility.BukkitCompatibility;
import org.mvplugins.multiverse.core.world.key.WorldKeyOrName;

/**
 * <p>Utility class in helping to check the status of a world name and it's associated world folder.</p>
 *
 * <p>Note this is for preliminary checks and better command output. A valid result will suggest but not
 * 100% determine that a world name can be created, loaded or imported.</p>
 */
@Service
public final class WorldNameChecker {

    private static final Set<String> BLACKLIST_NAMES = Set.of(
            "cache",
            "config",
            "crash-reports",
            "libraries",
            "logs",
            "plugins",
            "versions");

    private static final List<WorldFolderSchema> WORLD_FOLDER_SCHEMA = List.of(
            // OLD
            WorldFolderSchema.file("level.dat"),
            WorldFolderSchema.folder("DIM1"),
            WorldFolderSchema.folder("DIM-1"),
            // NEW
            WorldFolderSchema.file("paper-world.yml"),
            WorldFolderSchema.folder("data"),
            WorldFolderSchema.folder("entities"),
            WorldFolderSchema.folder("poi"),
            WorldFolderSchema.folder("region"));

    /**
     * Checks if a world name is valid.
     *
     * @param worldName The world name to check on.
     * @return True if check result is valid, else false.
     */
    public boolean isValidWorldName(@Nullable String worldName) {
        return checkName(worldName) == NameStatus.VALID;
    }

    /**
     * Checks the current validity status of a world name.
     *
     * @param worldName The world name to check on.
     * @return The resulting name status.
     */
    @NotNull
    public NameStatus checkName(@Nullable String worldName) {
        return Option.of(worldName)
                .map(name -> name.toLowerCase(Locale.ENGLISH))
                .map(name -> {
                    if (name.isEmpty()) {
                        return NameStatus.EMPTY;
                    }
                    if (!BukkitCompatibility.isUsingNewDimensionStorage() && BLACKLIST_NAMES.contains(name)) {
                        return NameStatus.BLACKLISTED;
                    }
                    if (!REPatterns.NAMESPACE_KEY.matcher(name).matches()) {
                        return NameStatus.INVALID_CHARS;
                    }
                    return NameStatus.VALID;
                })
                .getOrElse(NameStatus.EMPTY);
    }

    /**
     * Check if a world name has a world folder directory. It may not contain valid world data.
     *
     * @param worldName The world name to check on.
     * @return True if the folder exists, else false.
     *
     * @deprecated Use {@link #hasWorldFolder(WorldKeyOrName)} instead, which is more robust and supports namespaced keys.
     */
    @Deprecated(forRemoval = true, since = "5.7")
    @ApiStatus.ScheduledForRemoval(inVersion = "6.0")
    public boolean hasWorldFolder(@Nullable String worldName) {
        return checkFolder(worldName) != FolderStatus.DOES_NOT_EXIST;
    }

    /**
     * Check if a world name has a world folder directory. It may not contain valid world data.
     *
     * @param worldKey The world key to check on.
     * @return True if the folder exists, else false.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public boolean hasWorldFolder(@Nullable WorldKeyOrName worldKey) {
        return checkFolder(worldKey) != FolderStatus.DOES_NOT_EXIST;
    }

    /**
     * Checks if a world name has a valid world folder with basic world data.
     *
     * @param worldName The world name to check on.
     * @return True if check result is valid, else false.
     *
     * @deprecated Use {@link #isValidWorldFolder(WorldKeyOrName)} instead, which is more robust and supports
     * namespaced keys.
     */
    @Deprecated(forRemoval = true, since = "5.7")
    @ApiStatus.ScheduledForRemoval(inVersion = "6.0")
    public boolean isValidWorldFolder(@Nullable String worldName) {
        return checkFolder(worldName).loadable;
    }

    /**
     * Checks if a world name has a valid world folder with basic world data.
     *
     * @param nameOrKey The world key to check on.
     * @return True if check result is valid, else false.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public boolean isValidWorldFolder(@Nullable WorldKeyOrName nameOrKey) {
        return checkFolder(nameOrKey).loadable;
    }

    /**
     * Checks if a world folder is valid with basic world data.
     *
     * @param worldFolder   The world folder to check on.
     * @return True if check result is valid, else false.
     */
    public boolean isValidWorldFolder(@Nullable File worldFolder) {
        return checkFolder(worldFolder).loadable;
    }

    /**
     * Checks the current folder status for a world name.
     *
     * @param worldName The world name to check on.
     * @return The resulting folder status.
     *
     * @deprecated Use {@link #checkFolder(WorldKeyOrName)} instead, which is more robust and supports namespaced keys.
     */
    @Deprecated(forRemoval = true, since = "5.7")
    @ApiStatus.ScheduledForRemoval(inVersion = "6.0")
    @NotNull
    public FolderStatus checkFolder(@Nullable String worldName) {
        if (worldName == null) {
            return FolderStatus.DOES_NOT_EXIST;
        }

        return WorldKeyOrName.parse(worldName)
                .map(this::checkFolder)
                .getOrElse(FolderStatus.NOT_A_WORLD);
    }

    /**
     * Checks the current folder status for a world key or name.
     *
     * @param keyOrName The world key or name to check on.
     * @return The resulting folder status.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @NotNull
    public FolderStatus checkFolder(@Nullable WorldKeyOrName keyOrName) {
        if (keyOrName == null) {
            return FolderStatus.DOES_NOT_EXIST;
        }

        if (BukkitCompatibility.isUsingNewDimensionStorage()) {
            File oldWorldFolder = WorldFolderResolver.resolveAsLegacyWorldName(keyOrName.usableName());
            if (checkFolder(oldWorldFolder) == FolderStatus.VALID) {
                return FolderStatus.REQUIRES_MIGRATION;
            }
        }

        return checkFolder(WorldFolderResolver.resolve(keyOrName));
    }

    /**
     * Checks the current folder status.
     *
     * @param worldFolder   The world folder to check on.
     * @return The resulting folder status.
     */
    @NotNull
    public FolderStatus checkFolder(@Nullable File worldFolder) {
        if (worldFolder == null || !worldFolder.exists() || !worldFolder.isDirectory()) {
            return FolderStatus.DOES_NOT_EXIST;
        }
        if (!folderWorldSchemaCheck(worldFolder)) {
            return FolderStatus.NOT_A_WORLD;
        }
        return FolderStatus.VALID;
    }

    /**
     * A very basic check to see if a folder has a level.dat file. If it does, we can safely assume
     * it's a world folder.
     *
     * @param worldFolder   The File that may be a world.
     * @return True if it looks like a world, else false.
     */
    private boolean folderWorldSchemaCheck(@NotNull File worldFolder) {
        return WORLD_FOLDER_SCHEMA.stream()
                .filter(schema -> schema.check(worldFolder))
                .count() >= 2;
    }

    /**
     * Helper class to check if a file or folder exist.
     */
    private interface WorldFolderSchema {

        static WorldFolderSchema file(String path) {
            return new WorldFile(path);
        }

        static WorldFolderSchema folder(String path) {
            return new WorldFolder(path);
        }

        boolean check(File worldFolder);

        final class WorldFile implements WorldFolderSchema {
            private final String path;

            private WorldFile(String path) {
                this.path = path;
            }

            @Override
            public boolean check(File worldFolder) {
                File thisFolder = worldFolder.toPath().resolve(path).toFile();
                return thisFolder.exists() && thisFolder.isFile();
            }
        }

        final class WorldFolder implements WorldFolderSchema {
            private final String path;

            private WorldFolder(String path) {
                this.path = path;
            }

            @Override
            public boolean check(File worldFolder) {
                File thisFolder = worldFolder.toPath().resolve(path).toFile();
                return thisFolder.exists() && thisFolder.isDirectory();
            }
        }
    }

    /**
     * Result after checking validity of world name.
     */
    public enum NameStatus {
        /**
         * Name is valid.
         */
        VALID,

        /**
         * Name not valid as it contains invalid characters.
         */
        INVALID_CHARS,

        /**
         * Name string that is null or length 0.
         */
        EMPTY,

        /**
         * Name not valid as it is deemed blacklisted.
         */
        BLACKLISTED
    }

    /**
     * Result after checking validity of world folder.
     */
    public enum FolderStatus {
        /**
         * Folder is valid.
         */
        VALID(true),

        /**
         * This folder will cause PaperMC to migrate to new dimension world folder in 26.1+
         */
        REQUIRES_MIGRATION(true),

        /**
         * Folder exist, but contents in it doesnt look like a world.
         */
        NOT_A_WORLD(false),

        /**
         * Folder does not exist.
         */
        DOES_NOT_EXIST(false),
        ;

        private final boolean loadable;

        FolderStatus(boolean loadable) {
            this.loadable = loadable;
        }

        /**
         * Whether this folder status is loadable, meaning it has the basic world data and can be loaded as a world.
         * Note that this does not guarantee the server will definitely load the world with no errors.
         *
         * @return True if folder probably is loadable by the server, else false.
         */
        @ApiStatus.AvailableSince("5.6")
        public boolean isLoadable() {
            return loadable;
        }
    }
}
