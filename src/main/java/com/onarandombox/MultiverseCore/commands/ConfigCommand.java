/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.enums.ConfigProperty;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

public class ConfigCommand extends MultiverseCommand {
    private MVWorldManager worldManager;

    public ConfigCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Configuration");
        this.setCommandUsage("/mv config");
        this.setArgRange(2, 2);
        this.addKey("mv config");
        this.addKey("mvconfig");
        this.addKey("mv conf");
        this.addKey("mvconf");
        this.setPermission("multiverse.core.config", "Allows you to set Global MV Variables.", PermissionDefault.OP);
        this.worldManager = this.plugin.getMVWorldManager();
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (args.get(0).equalsIgnoreCase("messagecooldown") || args.get(0).equalsIgnoreCase("teleportcooldown") || args.get(0).equalsIgnoreCase("debug")) {
            try {
                this.plugin.getConfig().setProperty(args.get(0).toLowerCase(), Integer.parseInt(args.get(1)));
                this.plugin.loadConfigs();
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Sorry, " + ChatColor.AQUA + args.get(0) + ChatColor.WHITE + " must be an integer!");
            }
        } else {
            if (ConfigProperty.valueOf(args.get(0).toLowerCase()) != null) {
                try {
                    this.plugin.getConfig().setProperty(args.get(0).toLowerCase(), Boolean.parseBoolean(args.get(0)));
                    this.plugin.loadConfigs();
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Sorry, " + ChatColor.AQUA + args.get(0) + ChatColor.WHITE + " must be true or false!");
                }

            }
        }
    }
}
