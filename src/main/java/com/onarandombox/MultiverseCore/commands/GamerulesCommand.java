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
        MultiverseWorld world = getTargetWorld(sender, args, 0);
        if (world == null) {
            return;
        }

        final World CBWorld = world.getCBWorld();
        final StringBuilder gameRules = new StringBuilder();
        for (final String gameRule : CBWorld.getGameRules()) {
            if (gameRules.length() != 0) {
                gameRules.append(ChatColor.WHITE).append(", ");
            }
            gameRules.append(ChatColor.AQUA).append(gameRule).append(ChatColor.WHITE).append(": ");
            gameRules.append(ChatColor.GREEN).append(CBWorld.getGameRuleValue(GameRule.getByName(gameRule)));
        }
        sender.sendMessage("=== Gamerules for " + ChatColor.AQUA + world.getName() + ChatColor.WHITE + " ===");
        sender.sendMessage(gameRules.toString());
    }
}
