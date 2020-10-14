/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * Allows management of Anchor Destinations.
 */
public class GameruleCommand extends MultiverseCommand {

    public GameruleCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Set a Minecraft Game Rule for a World.");
        this.setCommandUsage("/mv gamerule " + ChatColor.GREEN + "{RULE} {VALUE}" + ChatColor.GOLD + " [WORLD]");
        this.setArgRange(2, 3);
        this.addKey("mv gamerule");
        this.addKey("mv rule");
        this.addKey("mvgamerule");
        this.addKey("mvrule");
        this.addCommandExample("/mv gamerule " + ChatColor.GREEN + "doMobLoot false");
        this.addCommandExample("/mvrule " + ChatColor.GREEN + "keepInventory true " + ChatColor.RED + "world_nether");
        this.setPermission("multiverse.core.gamerule.set", "Allows a player to set a gamerule.", PermissionDefault.OP);
    }


    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        MultiverseWorld world = getTargetWorld(sender, args, 2);
        if (world == null) {
            return;
        }

        final World CBWorld = world.getCBWorld();
        final GameRule gameRule = GameRule.getByName(args.get(0));
        final String value = args.get(1);

        if (gameRule == null) {
            sender.sendMessage(ChatColor.RED + "Failure! " + ChatColor.AQUA + args.get(0) + ChatColor.WHITE
                    + " is not a valid gamerule.");
        } else {
            if (gameRule.getType() == Boolean.class) {
                boolean booleanValue;
                if (value.equalsIgnoreCase("true")) {
                    booleanValue = true;
                } else if (value.equalsIgnoreCase("false")) {
                    booleanValue = false;
                } else {
                    sender.sendMessage(getErrorMessage(gameRule.getName(), value) + "it can only be set to true or false.");
                    return;
                }

                if (!CBWorld.setGameRule(gameRule, booleanValue)) {
                    sender.sendMessage(getErrorMessage(gameRule.getName(), value) + "something went wrong.");
                    return;
                }
            } else if (gameRule.getType() == Integer.class) {
                try {
                    if (!CBWorld.setGameRule(gameRule, Integer.parseInt(value))) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(getErrorMessage(gameRule.getName(), value) + "it can only be set to a positive integer.");
                    return;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Failure!" + ChatColor.WHITE + " Gamerule " + ChatColor.AQUA + gameRule.getName()
                        + ChatColor.WHITE + " isn't supported yet, please let us know about it.");
                return;
            }

            sender.sendMessage(ChatColor.GREEN + "Success!" + ChatColor.WHITE + " Gamerule " + ChatColor.AQUA + gameRule.getName()
                    + ChatColor.WHITE + " was set to " + ChatColor.GREEN + value + ChatColor.WHITE + ".");
        }
    }

    private String getErrorMessage(String gameRule, String value) {
        return ChatColor.RED + "Failure!" + ChatColor.WHITE + " Gamerule " + ChatColor.AQUA + gameRule
                + ChatColor.WHITE + " could not be set to " + ChatColor.RED + value + ChatColor.WHITE + ", ";
    }
}
