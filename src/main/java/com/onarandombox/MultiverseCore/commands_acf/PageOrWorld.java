package com.onarandombox.MultiverseCore.commands_acf;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class PageOrWorld {
    private MultiverseWorld world;
    private int page;

    public PageOrWorld(MultiverseWorld world) {
        this.world = world;
        this.page = 1;
    }

    public PageOrWorld(MultiverseWorld world, int page) {
        this.world = world;
        this.page = page;
    }

    public MultiverseWorld getWorld() {
        return world;
    }

    public void setWorld(MultiverseWorld world) {
        this.world = world;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
