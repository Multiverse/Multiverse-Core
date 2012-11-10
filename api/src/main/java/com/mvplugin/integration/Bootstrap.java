package com.mvplugin.integration;

import com.mvplugin.MVCore;

public interface Bootstrap {
    MVCore getPlugin(APICollection api);
}
