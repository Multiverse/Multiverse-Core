package com.onarandombox.MultiverseCore.commands_helper;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class WorldAndPage {
    private final MultiverseWorld world;
    private final int page;

    public WorldAndPage(MultiverseWorld world, int page) {
        this.world = world;
        this.page = page;
    }

    public MultiverseWorld getWorld() {
        return world;
    }

    public int getPage() {
        return page;
    }
}
