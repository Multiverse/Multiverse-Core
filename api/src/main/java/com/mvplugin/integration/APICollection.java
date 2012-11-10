package com.mvplugin.integration;

public final class APICollection {
    private final WorldAPI worldAPI;

    public APICollection(WorldAPI wapi) {
        this.worldAPI = wapi;
    }

    public WorldAPI getWorldAPI() {
        return this.worldAPI;
    }
}
