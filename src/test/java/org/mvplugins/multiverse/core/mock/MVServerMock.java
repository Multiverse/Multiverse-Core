package org.mvplugins.multiverse.core.mock;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.command.MockCommandMap;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MVServerMock extends ServerMock {

    private final File worldContainer;

    public MVServerMock() throws IOException {
        super();
        this.worldContainer = Files.createTempDirectory("world-container").toFile();
        this.worldContainer.deleteOnExit();
        System.out.println("Created test world folder: " + this.worldContainer.getAbsolutePath());
    }

    // This is required for acf reflection to work
    @Override
    public @NotNull MockCommandMap getCommandMap() {
        return super.getCommandMap();
    }

    @Override
    public @NotNull File getWorldContainer() {
        return this.worldContainer;
    }

    @Override
    public World createWorld(@NotNull WorldCreator creator) {
        WorldMock world = new MVWorldMock(creator);
        world.getWorldFolder().mkdirs();
        createFile(new File(world.getWorldFolder(), "uid.dat"));
        createFile(new File(world.getWorldFolder(), "level.dat"));
        addWorld(world);
        return world;
    }

    private void createFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
