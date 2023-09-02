package com.onarandombox.MultiverseCore.worldnew;

import com.onarandombox.MultiverseCore.worldnew.config.WorldConfig;
import io.vavr.control.Try;

public class OfflineWorld {

    protected final String worldName;
    protected final WorldConfig worldConfig;

    public OfflineWorld(String worldName, WorldConfig worldConfig) {
        this.worldName = worldName;
        this.worldConfig = worldConfig;
    }

    public String getName() {
        return worldName;
    }

    public Try<Void> setProperty(String name, Object value) {
        return worldConfig.setProperty(name, value);
    }

    public Try<Object> getProperty(String name) {
        return worldConfig.getProperty(name);
    }
}
