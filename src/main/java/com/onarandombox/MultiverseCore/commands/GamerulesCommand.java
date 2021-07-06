/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.display.ColorAlternator;
import com.onarandombox.MultiverseCore.display.ContentDisplay;
import com.onarandombox.MultiverseCore.display.DisplayHandlers;
import com.onarandombox.MultiverseCore.display.DisplaySettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows management of Anchor Destinations.
 */
public class GamerulesCommand extends MultiverseCommand {

    public GamerulesCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("List the Minecraft Game Rules for a World.");
        this.setCommandUsage("/mv gamerules" + ChatColor.GOLD + " [WORLD]");
        this.setArgRange(0, 1);
        this.addKey("mv gamerules");
        this.addKey("mv rules");
        this.addKey("mvgamerules");
        this.addKey("mvrules");
        this.addCommandExample("/mv gamerules");
        this.addCommandExample("/mvrules " + ChatColor.RED + "world_nether");
        this.setPermission("multiverse.core.gamerule.list", "Allows a player to list gamerules.", PermissionDefault.OP);
    }


    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // We NEED a world from the command line
        final Player p;
        if (sender instanceof Player) {
            p = (Player) sender;
        } else {
            p = null;
        }

        if (args.size() == 0 && p == null) {
            sender.sendMessage("From the command line, WORLD is required.");
            sender.sendMessage(this.getCommandDesc());
            sender.sendMessage(this.getCommandUsage());
            sender.sendMessage("Nothing changed.");
            return;
        }

        final World world;
        if (args.size() == 0) {
            world = p.getWorld();
        } else {
            world = Bukkit.getWorld(args.get(0));
            if (world == null) {
                sender.sendMessage(ChatColor.RED + "Failure!" + ChatColor.WHITE + " World " + ChatColor.AQUA + args.get(0)
                        + ChatColor.WHITE + " does not exist.");
                return;
            }
        }

        ContentDisplay.forContent(getGameRuleMap(world))
                .header("=== Gamerules for %s%s%s ===", ChatColor.AQUA, world.getName(), ChatColor.WHITE)
                .colorTool(ColorAlternator.with(ChatColor.GREEN, ChatColor.GOLD))
                .setting(DisplaySettings.OPERATOR, ": ")
                .show(sender);
    }

    private Map<String, Object> getGameRuleMap(World world) {
        Map<String, Object> gameRuleMap = new HashMap<>();
        for (GameRule<?> rule : GameRule.values()) {
            Object value = world.getGameRuleValue(rule);
            if (value == null) {
                gameRuleMap.put(rule.getName(), "null");
                continue;
            }
            gameRuleMap.put(rule.getName(), value);
        }
        return gameRuleMap;
    }
}
