/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.localization.MultiverseMessage;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Returns a list of loaded generator plugins.
 */
public class GeneratorCommand extends MultiverseCommand {

    public GeneratorCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Generator Information");
        this.setCommandUsage("/mv generators");
        this.setArgRange(0, 0);
        this.addKey("mv generators");
        this.addKey("mvgenerators");
        this.addKey("mv gens");
        this.addKey("mvgens");
        this.addCommandExample("/mv generators");
        this.setPermission("multiverse.core.generator", "Returns a list of Loaded Generator Plugins.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        this.plugin.log(Level.INFO, "PLEASE IGNORE the 'Plugin X does not contain any generators' message below!");
        Plugin[] plugins = this.plugin.getServer().getPluginManager().getPlugins();
        List<String> generators = new ArrayList<String>();
        for (Plugin p : plugins) {
            if (p.isEnabled() && p.getDefaultWorldGenerator("world", "") != null) {
                generators.add(p.getDescription().getName());
            }
        }
        this.messaging.sendMessage(sender, MultiverseMessage.CMD_GENERATOR_LISTHEADER);
        String loadedGens = "";
        boolean altColor = false;
        for (String s : generators) {
            loadedGens += (altColor ? ChatColor.YELLOW : ChatColor.WHITE) + s + " ";
            altColor = !altColor;
        }
        if (loadedGens.length() == 0)
            this.messaging.sendMessage(sender, MultiverseMessage.CMD_GENERATOR_NOGENSFOUND);
        else
            this.messaging.sendMessage(sender, loadedGens, false);
    }
}
