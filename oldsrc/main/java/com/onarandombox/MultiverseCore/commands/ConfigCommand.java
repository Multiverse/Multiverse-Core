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
import java.util.Map;

/**
 * Allows you to set Global MV Variables.
 */
public class ConfigCommand extends MultiverseCommand {
    public ConfigCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Configuration");
        this.setCommandUsage("/mv config " + ChatColor.GREEN + "{PROPERTY} {VALUE}");
        this.setArgRange(1, 2);
        this.addKey("mv config");
        this.addKey("mvconfig");
        this.addKey("mv conf");
        this.addKey("mvconf");
        this.addCommandExample("/mv config show");
        this.addCommandExample("/mv config " + ChatColor.GREEN + "debug" + ChatColor.AQUA + " 3");
        this.addCommandExample("/mv config " + ChatColor.GREEN + "enforceaccess" + ChatColor.AQUA + " false");
        this.setPermission("multiverse.core.config", "Allows you to set Global MV Variables.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            StringBuilder builder = new StringBuilder();
            Map<String, Object> serializedConfig = this.plugin.getMVConfig().serialize();
            for (Map.Entry<String, Object> entry : serializedConfig.entrySet()) {
                builder.append(ChatColor.GREEN);
                builder.append(entry.getKey());
                builder.append(ChatColor.WHITE).append(" = ").append(ChatColor.GOLD);
                builder.append(entry.getValue().toString());
                builder.append(ChatColor.WHITE).append(", ");
            }
            String message = builder.toString();
            message = message.substring(0, message.length() - 2);
            sender.sendMessage(message);
            return;
        }
        if (!this.plugin.getMVConfig().setConfigProperty(args.get(0).toLowerCase(), args.get(1))) {
            sender.sendMessage(String.format("%sSetting '%s' to '%s' failed!", ChatColor.RED, args.get(0).toLowerCase(), args.get(1)));
            return;
        }

        // special rule
        if (args.get(0).equalsIgnoreCase("firstspawnworld")) {
            // Don't forget to set the world!
            this.plugin.getMVWorldManager().setFirstSpawnWorld(args.get(1));
        }

        if (this.plugin.saveMVConfigs()) {
            sender.sendMessage(ChatColor.GREEN + "SUCCESS!" + ChatColor.WHITE + " Values were updated successfully!");
            this.plugin.loadConfigs();
        } else {
            sender.sendMessage(ChatColor.RED + "FAIL!" + ChatColor.WHITE + " Check your console for details!");
        }
    }
}
