/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.enums.ConfigProperty;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

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
        this.addCommandExample("All values: " + ConfigProperty.getAllValues());
        this.addCommandExample("/mv config show");
        this.addCommandExample("/mv config " + ChatColor.GREEN + "debug" + ChatColor.AQUA + " 3");
        this.addCommandExample("/mv config " + ChatColor.GREEN + "enforceaccess" + ChatColor.AQUA + " false");
        this.setPermission("multiverse.core.config", "Allows you to set Global MV Variables.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            StringBuilder builder = new StringBuilder();

            builder.append(ChatColor.GREEN).append("enforceAccess").append(ChatColor.WHITE).append(" = ");
            builder.append(ChatColor.GOLD).append(MultiverseCore.getStaticConfig().getEnforceAccess());
            builder.append(ChatColor.WHITE).append(", ");
            builder.append(ChatColor.GREEN).append("prefixChat").append(ChatColor.WHITE).append(" = ");
            builder.append(ChatColor.GOLD).append(MultiverseCore.getStaticConfig().getPrefixChat()).append(", ");
            builder.append(ChatColor.WHITE).append(", ");
            builder.append(ChatColor.GREEN).append("teleportIntercept").append(ChatColor.WHITE).append(" = ");
            builder.append(ChatColor.GOLD).append(MultiverseCore.getStaticConfig().getTeleportIntercept());
            builder.append(ChatColor.WHITE).append(", ");
            builder.append(ChatColor.GREEN).append("firstSpawnOverride").append(ChatColor.WHITE).append(" = ");
            builder.append(ChatColor.GOLD).append(MultiverseCore.getStaticConfig().getFirstSpawnOverride());
            builder.append(ChatColor.WHITE).append(", ");
            builder.append(ChatColor.GREEN).append("firstSpawnWorld").append(ChatColor.WHITE).append(" = ");
            builder.append(ChatColor.GOLD).append(MultiverseCore.getStaticConfig().getFirstSpawnWorld());
            builder.append(ChatColor.WHITE).append(", ");
            builder.append(ChatColor.GREEN).append("displayPermErrors").append(ChatColor.WHITE).append(" = ");
            builder.append(ChatColor.GOLD).append(MultiverseCore.getStaticConfig().getDisplayPermErrors());
            builder.append(ChatColor.WHITE).append(", ");
            builder.append(ChatColor.GREEN).append("globalDebug").append(ChatColor.WHITE).append(" = ");
            builder.append(ChatColor.GOLD).append(MultiverseCore.getStaticConfig().getGlobalDebug());
            builder.append(ChatColor.WHITE).append(", ");
            builder.append(ChatColor.GREEN).append("messageCooldown").append(ChatColor.WHITE).append(" = ");
            builder.append(ChatColor.GOLD).append(MultiverseCore.getStaticConfig().getMessageCooldown());
            builder.append(ChatColor.WHITE).append(", ");
            builder.append(ChatColor.GREEN).append("version").append(ChatColor.WHITE).append(" = ");
            builder.append(ChatColor.GOLD).append(MultiverseCore.getStaticConfig().getVersion());
            sender.sendMessage(new StringBuilder().append(ChatColor.AQUA)
                    .append(" === [ All Values ] ===").toString());
            sender.sendMessage(builder.toString());
            return;
        }
        if (args.get(0).equalsIgnoreCase("firstspawnworld")) {
            MultiverseCore.getStaticConfig().setFirstSpawnWorld(args.get(1));
            // Don't forget to set the world!
            this.plugin.getMVWorldManager().setFirstSpawnWorld(args.get(1));
        } else if (args.get(0).equalsIgnoreCase("messagecooldown")) {
            try {
                this.plugin.getMVConfiguration().set(args.get(0).toLowerCase(), Integer.parseInt(args.get(1)));
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Sorry, " + ChatColor.AQUA + args.get(0) + ChatColor.WHITE + " must be an integer!");
                return;
            }
        } else if (args.get(0).equalsIgnoreCase("debug")) {
            try {
                MultiverseCore.getStaticConfig().setGlobalDebug(Integer.parseInt(args.get(1)));
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Sorry, " + ChatColor.AQUA + args.get(0) + ChatColor.WHITE + " must be an integer!");
                return;
            }
        } else {
            boolean value = false;
            try {
                value =  Boolean.parseBoolean(args.get(1));
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Sorry, " + ChatColor.AQUA + args.get(0) + ChatColor.WHITE + " must be true or false!");
                return;
            }
            if (args.get(0).equalsIgnoreCase("enforceaccess")) {
                MultiverseCore.getStaticConfig().setEnforceAccess(value);
            } else if (args.get(0).equalsIgnoreCase("prefixchat")) {
                MultiverseCore.getStaticConfig().setPrefixChat(value);
            } else if (args.get(0).equalsIgnoreCase("teleportintercept")) {
                MultiverseCore.getStaticConfig().setTeleportIntercept(value);
            } else if (args.get(0).equalsIgnoreCase("firstspawnoverride")) {
                MultiverseCore.getStaticConfig().setFirstSpawnOverride(value);
            } else if (args.get(0).equalsIgnoreCase("displaypermerrors")) {
                MultiverseCore.getStaticConfig().setDisplayPermErrors(value);
            } else {
                sender.sendMessage(ChatColor.RED + "Sorry, " + ChatColor.AQUA
                        + args.get(0) + ChatColor.WHITE + " you can't set " + ChatColor.AQUA + args.get(0));
                sender.sendMessage(ChatColor.GREEN + "Valid values are:");
                sender.sendMessage(ConfigProperty.getAllValues());
                return;
            }
        }
        if (this.plugin.saveMVConfigs()) {
            sender.sendMessage(ChatColor.GREEN + "SUCCESS!" + ChatColor.WHITE + " Values were updated successfully!");
            this.plugin.loadConfigs();
        } else {
            sender.sendMessage(ChatColor.RED + "FAIL!" + ChatColor.WHITE + " Check your console for details!");
        }
    }
}
