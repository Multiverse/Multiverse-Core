/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.util.List;

/**
 * States who is in what world.
 */
public class ScriptCommand extends MultiverseCommand {

    public ScriptCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Runs a script.");
        this.setCommandUsage("/mv script" + ChatColor.GOLD + " {script} [target]");
        this.setArgRange(1, 2);
        this.addKey("mv script");
        this.addKey("mvscript");
        this.addCommandExample(String.format("/mv script %sscript.txt", ChatColor.GOLD));
        this.addCommandExample(String.format("/mv script %stest.txt %ssomeplayer", ChatColor.GOLD, ChatColor.GREEN));
        this.setPermission("multiverse.core.script", "Runs a script.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (plugin.getScriptAPI() == null) {
            sender.sendMessage("Buscript failed to load while the server was starting. Scripts cannot be run.");
            return;
        }
        File file = new File(plugin.getScriptAPI().getScriptFolder(), args.get(0));
        if (!file.exists()) {
            sender.sendMessage("That script file does not exist in the Multiverse-Core scripts directory!");
            return;
        }
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        String target = null;
        if (args.size() == 2) {
            target = args.get(1);
        }
        plugin.getScriptAPI().executeScript(file, target, player);
        sender.sendMessage(String.format("Script '%s%s%s' finished!", ChatColor.GOLD, file.getName(), ChatColor.WHITE));
    }
}
