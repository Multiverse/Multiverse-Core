package com.mvplugin.integration;

import com.mvplugin.MultiverseCore;

public interface Bootstrap {
    MultiverseCore getPlugin(APICollection api);
}
