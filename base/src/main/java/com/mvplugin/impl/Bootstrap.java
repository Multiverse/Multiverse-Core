package com.mvplugin.impl;

import com.mvplugin.MVCore;
import com.mvplugin.integration.APICollection;

public final class Bootstrap implements com.mvplugin.integration.Bootstrap {
    @Override
    public MVCore getPlugin(APICollection api) {
        return new BasePlugin(api);
    }
}
