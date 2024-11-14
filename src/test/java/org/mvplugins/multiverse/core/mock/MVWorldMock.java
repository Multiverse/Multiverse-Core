package org.mvplugins.multiverse.core.mock;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.WorldMock;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;

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
