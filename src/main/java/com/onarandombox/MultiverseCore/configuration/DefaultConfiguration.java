package com.onarandombox.MultiverseCore.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * https://github.com/Nijikokun/iConomy3/blob/master/com/nijiko/coelho/iConomy/iConomy.java
 *
 * @author Nijikokun & Coelho
 */
public class DefaultConfiguration {

    public DefaultConfiguration(File folder, String name, MVConfigMigrator migrator) {
        File actual = new File(folder, name);
        if(actual.exists() && migrator.createdDefaults.contains(name)) {
            actual.delete();
        }
        // If defaults have been created, and we're being called again, we should try to migrate
        if (!actual.exists() && !migrator.migrate(name, folder)) {

            InputStream input = this.getClass().getResourceAsStream("/defaults/" + name);
            if (input != null) {
                FileOutputStream output = null;

                try {
                    output = new FileOutputStream(actual);
                    byte[] buf = new byte[8192];
                    int length = 0;

                    while ((length = input.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }
                    migrator.createdDefaults.add(name);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (input != null)
                            input.close();
                    } catch (Exception e) {
                    }

                    try {
                        if (output != null)
                            output.close();
                    } catch (Exception e) {

                    }
                }
            }
        }
    }
}
