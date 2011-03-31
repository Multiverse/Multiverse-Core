package com.onarandombox.MultiverseCore.configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * https://github.com/Nijikokun/iConomy3/blob/master/com/nijiko/coelho/iConomy/iConomy.java
 * @author Nijikokun & Coelho
 */
public class DefaultConfiguration {

    public DefaultConfiguration(File folder, String name) {
        new DefaultConfiguration(folder, name, null);
    }

    public DefaultConfiguration(File folder, String name, String contains) {
        File actual = new File(folder, name);

        if (!actual.exists()) {
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

                    // MultiverseCore.log.info(MultiverseCore.logPrefix + "Default config file written: " + name);
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
        } else {
            if (contains == null) {
                return;
            }

            boolean found = false;

            try {
                // Open the file that is the first
                // command line parameter
                FileInputStream fstream = new FileInputStream(actual);
                // Get the object of DataInputStream
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                // Read File Line By Line

                while ((strLine = br.readLine()) != null) {
                    if (strLine.equals(contains)) {
                        found = true;
                        break;
                    }
                }
                // Close the input stream
                in.close();
            } catch (Exception e) {// Catch exception if any
                System.err.println("Error: Could not verify the contents of " + actual.toString());
                System.err.println("Error: " + e.getMessage());
                return;
            }

            if (!found) {
                try {
                    BufferedWriter out = new BufferedWriter(new FileWriter(actual, true));
                    out.newLine();
                    out.write(contains);
                    out.close();
                } catch (Exception e) {
                    System.err.println("Error: Could not write default node to " + actual.toString());
                    System.err.println("Error: " + e.getMessage());
                    return;
                }
            }

        }

    }

}
