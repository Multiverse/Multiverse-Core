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
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

public class EnvironmentCommand extends MultiverseCommand {

    public EnvironmentCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("List Environments");
        this.setCommandUsage("/mv env");
        this.setArgRange(0, 0);
        this.addKey("mvenv");
        this.addKey("mv env");
        this.addKey("mv environment");
        this.addKey("mv environments");
        this.addCommandExample("/mv env");
        this.setPermission("multiverse.core.list.environments", "Lists valid known environments.", PermissionDefault.OP);
    }

    public static void showEnvironments(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Valid Environments are:");
        sender.sendMessage(ChatColor.GREEN + "NORMAL");
        sender.sendMessage(ChatColor.RED + "NETHER");
        sender.sendMessage(ChatColor.AQUA + "SKYLANDS");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        EnvironmentCommand.showEnvironments(sender);
    }
}
