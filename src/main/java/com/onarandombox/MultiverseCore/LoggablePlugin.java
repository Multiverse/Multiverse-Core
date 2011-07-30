package com.onarandombox.MultiverseCore;

import java.util.logging.Level;

import org.bukkit.Server;

public interface LoggablePlugin {
    public void log(Level level, String msg);

    public Server getServer();
}
