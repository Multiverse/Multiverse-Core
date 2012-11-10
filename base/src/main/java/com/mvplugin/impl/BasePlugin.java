package com.mvplugin.impl;

import com.mvplugin.integration.APICollection;

public class BasePlugin implements MVCore {
    private BaseWorldManager worldManager;

    public BasePlugin(APICollection api) {
        this.worldManager = new BaseWorldManager(this, api.getWorldAPI());
    }
}
