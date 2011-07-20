package com.onarandombox.MultiverseCore;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

public class MVConfigMigrator {
    private MultiverseCore core;

    public MVConfigMigrator(MultiverseCore core) {
        this.core = core;
    }

    public boolean migrate(String name, File folder) {
        File oldFolder = null;

        // They still have MV 1 installed! Good!
        if (this.core.getServer().getPluginManager().getPlugin("MultiVerse") != null) {
            this.core.log(Level.INFO, "Found MultiVerse 1. Starting Config Migration...");
            if (!this.core.getServer().getPluginManager().isPluginEnabled("MultiVerse")) {
                Plugin plugin = this.core.getServer().getPluginManager().getPlugin("MultiVerse");
                oldFolder = plugin.getDataFolder();
                this.core.getServer().getPluginManager().disablePlugin(plugin);
            }
        } else {
            // They didn't have MV 1 enabled... let's try and find the folder...
            File[] folders = folder.getParentFile().listFiles();
            List<File> folderList = Arrays.asList(folders);
            for (File f : folderList) {
                if (f.getName().equalsIgnoreCase("MultiVerse")) {
                    this.core.log(Level.INFO, "Found the MultiVerse 1 config folder. Starting Config Migration...");
                    oldFolder = f;
                }

            }
            if (oldFolder == null) {
                this.core.log(Level.INFO, "Did not find the MV1 Folder. If you did not have MultiVerse 1 installed and this is the FIRST time you're running MV2, this message is GOOD. ");
                this.core.log(Level.INFO, "If you did, your configs were **NOT** migrated! Go Here: INSERTURLFORHELP");
                return false;
            }
        }

        if (name.equalsIgnoreCase("worlds.yml")) {
            return this.migrateWorlds(name, oldFolder, folder);
        }

        if (name.equalsIgnoreCase("config.yml")) {
            return this.migrateMainConfig(name, oldFolder, folder);
        }
        return true;

    }

    private boolean migrateWorlds(String name, File oldFolder, File newFolder) {
        Configuration newConfig = new Configuration(new File(newFolder, "worlds.yml"));
        this.core.log(Level.INFO, "Trying to migrate worlds.yml...");
        Configuration oldConfig = new Configuration(new File(oldFolder, "Worlds.yml"));
        oldConfig.load();
        List<String> keys = oldConfig.getKeys("worlds");
        if (keys == null) {
            this.core.log(Level.WARNING, "Migration FAILURE!");
            return false;
        }
        for (String key : keys) {
            newConfig.setProperty("worlds." + key + ".animals.spawn", oldConfig.getProperty("worlds." + key + ".animals"));
            newConfig.setProperty("worlds." + key + ".monsters.spawn", oldConfig.getProperty("worlds." + key + ".mobs"));
            newConfig.setProperty("worlds." + key + ".pvp", oldConfig.getProperty("worlds." + key + ".pvp"));
            newConfig.setProperty("worlds." + key + ".alias.name", oldConfig.getProperty("worlds." + key + ".alias"));
            newConfig.setProperty("worlds." + key + ".tempspawn", oldConfig.getProperty("worlds." + key + ".spawn"));
            newConfig.setProperty("worlds." + key + ".price", oldConfig.getProperty("worlds." + key + ".price"));
            newConfig.setProperty("worlds." + key + ".environment", oldConfig.getProperty("worlds." + key + ".environment"));
            // Have to convert CSLs to arrays
            migrateListItem(newConfig, oldConfig, key, ".blockBlacklist", ".blockblacklist");
            migrateListItem(newConfig, oldConfig, key, ".worldBlacklist", ".worldblacklist");
            migrateListItem(newConfig, oldConfig, key, ".playerBlacklist", ".playerblacklist");
            migrateListItem(newConfig, oldConfig, key, ".playerWhitelist", ".playerwhitelist");
        }
        newConfig.save();
        this.core.log(Level.INFO, "Migration SUCCESS!");
        return true;
    }

    private void migrateListItem(Configuration newConfig, Configuration oldConfig, String key, String oldProperty, String newProperty) {
        List<String> list = Arrays.asList(oldConfig.getString("worlds." + key + oldProperty).split(","));
        if (list.size() > 0) {
            if (list.get(0).length() == 0) {
                list = new ArrayList<String>();
            }
        }
        newConfig.setProperty("worlds." + key + newProperty, list);
    }

    private boolean migrateMainConfig(String name, File oldFolder, File newFolder) {
        Configuration newConfig = new Configuration(new File(newFolder, "config.yml"));
        this.core.log(Level.INFO, "Migrating config.yml...");
        Configuration oldConfig = new Configuration(new File(oldFolder, "MultiVerse.yml"));
        oldConfig.load();
        newConfig.setProperty("worldnameprefix", oldConfig.getProperty("prefix"));
        newConfig.setProperty("messagecooldown", oldConfig.getProperty("alertcooldown"));
        // Default values:
        newConfig.setProperty("opfallback", true);
        newConfig.setProperty("disableautoheal", false);
        newConfig.setProperty("fakepvp", false);
        newConfig.setProperty("bedrespawn", true);
        newConfig.setProperty("version", 2.0);
        newConfig.save();
        return true;
    }
}
