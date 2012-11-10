package com.mvplugin.impl;

import com.mvplugin.MultiverseCore;
import com.mvplugin.integration.APICollection;

public final class Bootstrap implements com.mvplugin.integration.Bootstrap {
    @Override
    public MultiverseCore getPlugin(APICollection api) {
        return new BasePlugin(api);
    }
}
