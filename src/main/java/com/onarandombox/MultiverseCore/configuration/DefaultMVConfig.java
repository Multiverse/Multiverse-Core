package com.onarandombox.MultiverseCore.configuration;

import java.nio.file.Path;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.utils.settings.MVSettings;

public class DefaultMVConfig {

    private final MVSettings settings;

    public DefaultMVConfig(MultiverseCore core) {
        Path configPath = Path.of(core.getDataFolder().getPath(), "config2.yml");
        settings = MVSettings.builder(configPath)
                .logger(core.getLogger())
                .defaultNodes(MVConfigNodes.getNodes())
                .build();
        settings.load();
        settings.save();
    }
}
