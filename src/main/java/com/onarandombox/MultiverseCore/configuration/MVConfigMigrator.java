package com.onarandombox.MultiverseCore.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

import com.onarandombox.MultiverseCore.MultiverseCore;

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
    
    protected final File detectMultiverseFolders(File folder, MultiverseCore core) {
        File oldFolder = null;

        // They still have MV 1 installed! Good!
        if (core.getServer().getPluginManager().getPlugin("MultiVerse") != null) {
            core.log(Level.INFO, "Found MultiVerse 1. Starting Config Migration...");
            Plugin plugin = core.getServer().getPluginManager().getPlugin("MultiVerse");
            // We found MV1, but it's enabled, get the data folder, then disable it!
            if (!core.getServer().getPluginManager().isPluginEnabled("MultiVerse")) {
                core.getServer().getPluginManager().disablePlugin(plugin);
            }
            oldFolder = plugin.getDataFolder();
        } else {
            // They didn't have MV 1 enabled... let's try and find the folder...
            File[] folders = folder.getParentFile().listFiles();
            List<File> folderList = Arrays.asList(folders);
            for (File f : folderList) {
                if (f.getName().equalsIgnoreCase("MultiVerse")) {
                    core.log(Level.INFO, "Found the MultiVerse 1 config folder. Starting Config Migration...");
                    oldFolder = f;
                }

            }
            if (oldFolder == null) {
                core.log(Level.INFO, "Did not find the MV1 Folder. If you did not have MultiVerse 1 installed and this is the FIRST time you're running MV2, this message is GOOD. ");
                core.log(Level.INFO, "If you did, your configs were **NOT** migrated! Go Here: INSERTURLFORHELP");
                return null;
            }
        }
        return oldFolder;
    }
}
