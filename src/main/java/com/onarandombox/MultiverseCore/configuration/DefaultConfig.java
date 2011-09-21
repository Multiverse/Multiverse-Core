/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class DefaultConfig {

    public DefaultConfig(File folder, String name, MVConfigMigrator migrator) {
        File actual = new File(folder, name);
        if (actual.exists() && migrator.createdDefaults.contains(name)) {
            actual.delete();
        }
        // If defaults have been created, and we're being called again, we should try to migrate
        if (!actual.exists() && !migrator.migrate(name, folder)) {

            InputStream defConfig = this.getClass().getResourceAsStream("/defaults/" + name);
            if (defConfig != null) {
                FileOutputStream newConfig = null;

                try {
                    newConfig = new FileOutputStream(actual);
                    byte[] buf = new byte[8192];
                    int length = 0;

                    while ((length = defConfig.read(buf)) > 0) {
                        newConfig.write(buf, 0, length);
                    }
                    migrator.createdDefaults.add(name);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (defConfig != null)
                            defConfig.close();
                    } catch (Exception e) {
                    }

                    try {
                        if (newConfig != null)
                            newConfig.close();
                    } catch (Exception e) {

                    }
                }
            }
        }
    }
}
