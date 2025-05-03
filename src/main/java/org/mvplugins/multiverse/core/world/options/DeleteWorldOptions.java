package org.mvplugins.multiverse.core.world.options;

import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

import java.util.Collections;
import java.util.List;

/**
 * Options for customizing the deletion of a world.
 */
public final class DeleteWorldOptions {

    /**
     * Creates a new {@link DeleteWorldOptions} instance with the given world.
     *
     * @param world The world to delete.
     * @return A new {@link DeleteWorldOptions} instance.
     */
    public static @NotNull DeleteWorldOptions world(@NotNull MultiverseWorld world) {
        return new DeleteWorldOptions(world);
    }

    private final MultiverseWorld world;
    private List<String> keepFiles = Collections.emptyList();

    DeleteWorldOptions(MultiverseWorld world) {
        this.world = world;
    }

    public MultiverseWorld world() {
        return world;
    }

    /**
     * Sets the files to keep during deletion.
     *
     * @param keepFilesInput The files to keep during deletion.
     * @return This {@link DeleteWorldOptions} instance.
     */
    public @NotNull DeleteWorldOptions keepFiles(List<String> keepFilesInput) {
        this.keepFiles = keepFilesInput == null ? Collections.emptyList() : keepFilesInput.stream().toList();
        return this;
    }

    /**
     * Gets the files to keep during deletion. Note: The list is unmodifiable.
     *
     * @return The files to keep during deletion.
     */
    public List<String> keepFiles() {
        return keepFiles;
    }
}
