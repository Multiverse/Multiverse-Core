package org.mvplugins.multiverse.core.mock;

import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.world.WorldMock;

import java.io.File;

public class MVWorldMock extends WorldMock {

    private final File worldFolder;
    private final boolean generateStructures;

    public MVWorldMock(@NotNull WorldCreator creator) {
        super(creator);
        this.worldFolder = new File(MockBukkit.getMock().getWorldContainer(), getName());
        this.generateStructures = creator.generateStructures();
    }

    @Override
    public @NotNull File getWorldFolder() {
        return this.worldFolder;
    }

    @Override
    public boolean canGenerateStructures() {
        return this.generateStructures;
    }
}
