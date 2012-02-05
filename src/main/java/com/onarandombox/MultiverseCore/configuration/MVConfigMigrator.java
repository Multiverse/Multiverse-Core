/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.configuration;

import com.onarandombox.MultiverseCore.api.LoggablePlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/*
 * This is a mess, so I'm just going to suppress all warnings here.
 * BEGIN CHECKSTYLE-SUPPRESSION: ALL
 */

/**
 * This will be gone in 2.4.
 * @deprecated This isn't used any more, is it? No. No it is not.
 */
@Deprecated
public abstract class MVConfigMigrator {

    public List<String> createdDefaults = new ArrayList<String>();

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

    protected final File detectMultiverseFolders(File folder, LoggablePlugin mvPlugin) {
        File oldFolder = null;
        mvPlugin.log(Level.INFO, "Starting Multiverse Configuration Migrator(MVCM)!");
        // They still have MV 1 installed! Good!
        if (mvPlugin.getServer().getPluginManager().getPlugin("MultiVerse") != null) {
            mvPlugin.log(Level.INFO, "Found MultiVerse 1. Starting Config Migration...");
            Plugin plugin = mvPlugin.getServer().getPluginManager().getPlugin("MultiVerse");
            oldFolder = plugin.getDataFolder();
        } else {
            // They didn't have MV 1 enabled... let's try and find the folder...
            File[] folders = folder.getParentFile().listFiles();
            List<File> folderList = Arrays.asList(folders);
            for (File f : folderList) {
                if (f.getName().equalsIgnoreCase("MultiVerse")) {
                    mvPlugin.log(Level.INFO, "Found the MultiVerse 1 config folder. Starting Config Migration...");
                    oldFolder = f;
                }
            }
            if (oldFolder == null) {
                mvPlugin.log(Level.INFO, "Did not find the MV1 Folder. If you did not have MultiVerse 1 installed and this is the FIRST time you're running MV2, this message is GOOD. ");
                mvPlugin.log(Level.INFO, "If you did, your configs were **NOT** migrated! Go Here: INSERTURLFORHELP");
                return null;
            }
        }
        return oldFolder;
    }
}

/*
 * END CHECKSTYLE-SUPPRESSION: ALL
 */
