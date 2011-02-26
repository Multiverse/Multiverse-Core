package com.onarandombox.MultiVerseCore.configuration;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class defaultConfiguration {

    public void setupMultiVerseConfig(File config) {
            try {
                config.createNewFile();
                FileWriter fstream = new FileWriter(config);
                BufferedWriter out = new BufferedWriter(fstream);

                // TODO: Format Layout, remove unnecessary crap etc...
                
                out.write("#Prefix Chat with World Name.\n");
                out.write("prefix: true\n");
                out.write("\n");
                out.write("#Choose whether or not Players have to pay to use the portals.\n");
                out.write("iconomy: false\n");
                out.write("\n");
                out.write("#True/False - Whether MultiVerse should handle all respawns on every World including the Default.\n");
                out.write("#Disable this if you have a form of Respawn Teleportation plugin.\n");
                out.write("globalrespawn: false\n");
                out.write("#True/False - Whether MultiVerse should handle all respawns on the MultiVerse Worlds.\n");
                out.write("#If 'globalrespawn:true' then this will have no effect.\n");
                out.write("alternaterespawn: true\n");
                out.write("\n");
                out.write("#How long a player has to wait before using another portal.\n");
                out.write("#In Milliseconds - Default is '5000' which is 5 Seconds.\n");
                out.write("tpcooldown: 5000\n");
                out.write("#How long to leave in between sending an alert to the player.\n");
                out.write("#In Milliseconds - Default is '5000' which is 5 Seconds.\n");
                out.write("alertcooldown: 5000\n");
                out.write("#How long the player has to wait before they can get more information from a portal.\n");
                out.write("#In Milliseconds - Default is '5000' which is 5 Seconds.\n");
                out.write("infocooldown: 5000\n");
                out.write("\n");
                out.write("#The Item a player has to use to get information from a portal.\n");
                out.write("#Default is 49 - Obsidian\n");
                out.write("infowand: 49\n");
                out.write("\n");
                out.write("#The Item a player has to use to set the coordinates to create a portal\n");
                out.write("#Default is 270 - Wood Pickaxe\n");
                out.write("setwand: 270\n");
                out.write("\n");
                out.write("#SinglePlayer Styled Nether - You still have to \"/mvimport\" the World for Nether.\n");
                out.write("#The settings below only affect the SPLike portals.\n");
                out.write("#SPLike - True/False - Portals without a Destination or Sign will act like a SinglePlayer portal.\n");
                out.write("#AutoBuild - True/False - AutoBuild a destination portal if none are found nearby?\n");
                out.write("#Nether - Folder/WorldName of the Nether world.\n");
                out.write("#Default - Folder/WorldName of the default world setup in server.properties.\n");
                out.write("#RespawnToDefault - True/False - When a player dies do we respawn them back to the default world.\n");
                out.write("splike: false\n");
                out.write("autobuild: false\n");
                out.write("nether: nether\n");
                out.write("default: world\n");
                out.write("respawntodefault: false\n");
                
                
                out.close();
                fstream.close();
            } catch (IOException ex) {
                System.out.print("Error creating MultiVerse.yml");
            }
    }
    
    public void setupWorldConfig(File config){
        if (!config.exists()) {
            try {
                config.createNewFile();
                FileWriter fstream = new FileWriter(config);
                BufferedWriter out = new BufferedWriter(fstream);

                // TODO: Implement an Example.
                
                // out.write("\n");
                // out.write("###############################\n");
                // out.write("########### Example ###########\n");
                // out.write("###############################\n");
                // out.write("# worlds:\n");
                // out.write("#     hellworld:\n");
                // out.write("#         environment: NETHER\n");
                // out.write("#     creative:\n");
                // out.write("#         environment: NORMAL\n");
                // out.write("###############################\n");
                // out.write("\n");
                out.write("worlds:\n");

                out.close();
                fstream.close();
            } catch (IOException ex) {
                System.out.print("Error creating Worlds.yml");
            }
        }
    }
}
