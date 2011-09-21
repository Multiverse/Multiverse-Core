/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.configuration;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.util.config.Configuration;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class MVCoreConfigMigrator extends MVConfigMigrator {
    private MultiverseCore core;

    public MVCoreConfigMigrator(MultiverseCore core) {
        this.core = core;
    }

    public boolean migrate(String name, File folder) {
        File oldFolder = this.detectMultiverseFolders(folder, core);

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
            this.core.log(Level.SEVERE, "Migration FAILURE!");
            return false;
        }
        for (String key : keys) {
            newConfig.setProperty("worlds." + key + ".animals.spawn", oldConfig.getProperty("worlds." + key + ".animals"));
            newConfig.setProperty("worlds." + key + ".monsters.spawn", oldConfig.getProperty("worlds." + key + ".mobs"));
            newConfig.setProperty("worlds." + key + ".pvp", oldConfig.getProperty("worlds." + key + ".pvp"));
            newConfig.setProperty("worlds." + key + ".alias.name", oldConfig.getProperty("worlds." + key + ".alias"));
            newConfig.setProperty("worlds." + key + ".tempspawn", oldConfig.getProperty("worlds." + key + ".spawn"));
            newConfig.setProperty("worlds." + key + ".entryfee.amount", oldConfig.getProperty("worlds." + key + ".price"));
            newConfig.setProperty("worlds." + key + ".entryfee.currency", -1);
            newConfig.setProperty("worlds." + key + ".environment", oldConfig.getProperty("worlds." + key + ".environment"));
            // Have to convert CSLs to arrays
            migrateListItem(newConfig, oldConfig, key, ".blockBlacklist", ".blockblacklist");
            migrateListItem(newConfig, oldConfig, key, ".worldBlacklist", ".worldblacklist");
        }
        newConfig.save();
        this.core.log(Level.INFO, "Migration SUCCESS!");
        return true;
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
        newConfig.setProperty("version", 2.2);
        newConfig.save();
        return true;
    }
}
