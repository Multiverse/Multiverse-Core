/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.GeneratorPlugin;
import com.onarandombox.MultiverseCore.generators.SimpleGeneratorPlugin;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Returns a list of loaded generator plugins.
 */
public class GeneratorCommand extends MultiverseCommand {

    public GeneratorCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("World Information");
        this.setCommandUsage("/mv generators [plugin]");
        this.setArgRange(0, 1);
        this.addKey("mv generators");
        this.addKey("mvgenerators");
        this.addKey("mv gens");
        this.addKey("mvgens");
        this.addCommandExample("/mv generators");
        this.addCommandExample("/mv generators VoidGenerator");
        this.setPermission("multiverse.core.generator", "Returns info of Loaded Generator Plugins.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (args.size() == 0) {
            listKnownGenerators(sender);
            return;
        }
        showGeneratorDetails(sender, args.get(0));
    }

    private void listKnownGenerators(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "--- Loaded Generator Plugins ---");
        StringBuilder loadedGens = new StringBuilder();
        boolean altColor = false;
        for (String s : this.plugin.getMVGeneratorManager().getGeneratorPluginNames()) {
            loadedGens.append(altColor ? ChatColor.YELLOW : ChatColor.WHITE).append(s).append(' ');
            altColor = !altColor;
        }
        if (loadedGens.length() == 0) {
            loadedGens.append(ChatColor.RED).append("No Generator Plugins found.");
        }
        sender.sendMessage(loadedGens.toString());
    }

    private void showGeneratorDetails(CommandSender sender, String pluginName) {
        Plugin genPlugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (genPlugin == null) {
            sender.sendMessage(ChatColor.RED + "You do not have a plugin named '"+ pluginName + "' on your server.");
            return;
        }

        GeneratorPlugin generatorPlugin = this.plugin.getMVGeneratorManager().getGeneratorPlugin(genPlugin);
        if (generatorPlugin == null) {
            sender.sendMessage(ChatColor.RED + "'" + pluginName + "' does not look like a generator plugin.");
            return;
        }

        sender.sendMessage(ChatColor.AQUA + "--- Info on " + generatorPlugin.getPlugin().getName() + " ---");
        boolean hasAddInfo = false;

        Collection<String> exampleUsages = generatorPlugin.getExampleUsages();
        if (exampleUsages != null && exampleUsages.size() > 0) {
            hasAddInfo = true;
            sender.sendMessage("Usages:");
            exampleUsages.forEach(sender::sendMessage);
        }
        String infoLink = generatorPlugin.getInfoLink();
        if (infoLink != null) {
            hasAddInfo = true;
            sender.sendMessage("More Info: " + infoLink);
        }

        if (!hasAddInfo) {
            sender.sendMessage(ChatColor.RED + "'" + pluginName + "' did not provide additional info to Multiverse.");
        }
    }
}
