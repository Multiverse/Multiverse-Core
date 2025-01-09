package org.mvplugins.multiverse.core.mock;

import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.world.WorldMock;

import java.io.File;

public class MVWorldMock extends WorldMock {

    private final File worldFolder;

    public MVWorldMock(@NotNull WorldCreator creator) {
        super(creator);
        this.worldFolder = new File(MockBukkit.getMock().getWorldContainer(), getName());
    }

    @Override
    public @NotNull File getWorldFolder() {
        return this.worldFolder;
    }

    @Override
    public String toString() {
        return "MVWorldMock{'name': '" + this.getName() + "'}";
    }
}
