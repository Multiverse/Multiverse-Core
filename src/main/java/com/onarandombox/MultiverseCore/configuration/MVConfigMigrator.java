package com.onarandombox.MultiverseCore.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.util.config.Configuration;

public abstract class MVConfigMigrator {

    public abstract boolean migrate(String name, File folder);

    protected final void migrateListItem(Configuration newConfig, Configuration oldConfig, String key, String oldProperty, String newProperty) {
        List<String> list = Arrays.asList(oldConfig.getString("worlds." + key + oldProperty).split(","));
        if (list.size() > 0) {
            if (list.get(0).length() == 0) {
                list = new ArrayList<String>();
            }
        }
        newConfig.setProperty("worlds." + key + newProperty, list);
    }
}
