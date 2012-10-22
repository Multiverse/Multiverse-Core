package com.onarandombox.MultiverseCore.configuration;

import org.bukkit.Bukkit;

import com.onarandombox.MultiverseCore.api.Core;
import com.onarandombox.MultiverseCore.api.MVDestination;

import me.main__.util.SerializationConfig.IllegalPropertyValueException;
import me.main__.util.SerializationConfig.Serializor;

public class DestinationSerializor implements Serializor<MVDestination, String> {
    private static Core getCore() {
        Core c = (Core) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
        if (c == null)
            throw new IllegalStateException("We need our MVCore-plugin!");
        return c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MVDestination deserialize(String arg0, Class<MVDestination> arg1)
            throws IllegalPropertyValueException {
        return getCore().getDestFactory().getDestination(arg0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String serialize(MVDestination arg0) {
        return arg0.toString();
    }
}
