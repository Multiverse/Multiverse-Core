/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.enums.EnglishChatColor;
import com.onarandombox.MultiverseCore.exceptions.PropertyDoesNotExistException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * Used to set world-properties.
 */
public class ModifySetCommand extends MultiverseCommand {
    private MVWorldManager worldManager;

    public ModifySetCommand(MultiverseCore plugin) {
        super(plugin);
        this.worldManager = this.plugin.getMVWorldManager();
        this.setName("Modify a World (Set a value)");
        this.setCommandUsage("/mv modify" + ChatColor.GREEN + " set {PROPERTY} {VALUE}" + ChatColor.GOLD + " [WORLD]");
        this.setArgRange(1, 3);
        this.addKey("mvm set");
        this.addKey("mvmset");
        this.addKey("mv modify set");
        this.addKey("mvmodify set");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "set " + ChatColor.GREEN + "mode " + ChatColor.RED + "creative");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "set " + ChatColor.GREEN + "animals " + ChatColor.RED + "false");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "set " + ChatColor.GREEN + "monsters " + ChatColor.RED + "false");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "set " + ChatColor.GREEN + "alias " + ChatColor.RED + "MyWorld");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "set " + ChatColor.GREEN + "color " + ChatColor.RED + "green");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "set " + ChatColor.GREEN + "curr " + ChatColor.RED + "3");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "set " + ChatColor.GREEN + "price " + ChatColor.RED + "5");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "set " + ChatColor.GREEN + "scale " + ChatColor.RED + "1.2");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "set " + ChatColor.GREEN + "memory " + ChatColor.RED + "true");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "set " + ChatColor.GREEN + "diff " + ChatColor.RED + "hard");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "set " + ChatColor.GREEN + "hunger " + ChatColor.RED + "false");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "set " + ChatColor.GREEN + "hidden " + ChatColor.RED + "true");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "set " + ChatColor.GREEN + "pvp " + ChatColor.RED + "false");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "set " + ChatColor.GREEN + "heal " + ChatColor.RED + "true");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "set " + ChatColor.GREEN + "adjustspawn " + ChatColor.RED + "false");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "set " + ChatColor.GREEN + "spawn");
        this.setPermission("multiverse.core.modify.set", "Modify various aspects of worlds. See the help wiki for how to use this command properly. "
                + "If you do not include a world, the current world will be used.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // Special case for spawn:
        if (args.size() == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("You must be a player to set the" + ChatColor.GREEN + " spawn");
                return;
            }
            if (args.get(0).equalsIgnoreCase("spawn")) {
                SetSpawnCommand c = new SetSpawnCommand(this.plugin);
                c.setWorldSpawn(sender);

            } else {
                sender.sendMessage("Spawn is the only param with no" + ChatColor.GREEN + " VALUE");
                sender.sendMessage("Type " + ChatColor.GREEN + "/mv modify ?" + ChatColor.WHITE + " For help.");
            }
            return;
        }
        // We NEED a world from the command line
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }

        if (args.size() == 2 && p == null) {
            sender.sendMessage("From the command line, WORLD is required.");
            sender.sendMessage(this.getCommandDesc());
            sender.sendMessage(this.getCommandUsage());
            sender.sendMessage("Nothing changed.");
            return;
        }

        MultiverseWorld world;
        String value = args.get(1);
        String property = args.get(0);

        if (args.size() == 2) {
            world = this.worldManager.getMVWorld(p.getWorld().getName());
        } else {
            world = this.worldManager.getMVWorld(args.get(2));
        }

        if (world == null) {
            sender.sendMessage("That world does not exist!");
            return;
        }

        if ((property.equalsIgnoreCase("aliascolor") || property.equalsIgnoreCase("color")) && !EnglishChatColor.isValidAliasColor(value)) {
            sender.sendMessage(value + " is not a valid color. Please pick one of the following:");
            sender.sendMessage(EnglishChatColor.getAllColors());
            return;
        }
        try {
            if (world.setPropertyValue(property, value)) {
                sender.sendMessage(ChatColor.GREEN + "Success!" + ChatColor.WHITE + " Property " + ChatColor.AQUA + property
                        + ChatColor.WHITE + " was set to " + ChatColor.GREEN + value);
                if (!plugin.saveWorldConfig()) {
                    sender.sendMessage(ChatColor.RED + "There was an issue saving worlds.yml!  Your changes will only be temporary!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + world.getPropertyHelp(property));
            }
        } catch (PropertyDoesNotExistException e) {
            sender.sendMessage(ChatColor.RED + "Sorry, You can't set: '" + ChatColor.GRAY + property + ChatColor.RED + "'");
            sender.sendMessage("Valid world-properties: " + world.getAllPropertyNames());
        }
    }
}
