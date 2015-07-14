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
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * States who is in what world.
 */
public class WhoCommand extends MultiverseCommand {

    private MVWorldManager worldManager;

    public WhoCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Who?");
        this.setCommandUsage("/mv who" + ChatColor.GOLD + " [WORLD|--all]");
        this.setArgRange(0, 1);
        this.addKey("mv who");
        this.addKey("mvw");
        this.addKey("mvwho");
        this.addCommandExample("/mv who");
        this.addCommandExample(String.format("/mv who %s--all", ChatColor.GREEN));
        this.addCommandExample(String.format("/mv who %smyworld", ChatColor.GOLD));
        this.setPermission("multiverse.core.list.who", "States who is in what world.", PermissionDefault.OP);
        this.worldManager = this.plugin.getMVWorldManager();
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // If this command was sent from a Player then we need to check Permissions
        Player p = null;
        // By default, show all from the console
        boolean showAll = true;
        if (sender instanceof Player) {
            p = (Player) sender;
            showAll = false;
        }

        final Collection onlinePlayers = plugin.getServer().getOnlinePlayers();
        final Collection<Player> visiblePlayers = new HashSet<Player>(onlinePlayers.size());
        for (final Object player : onlinePlayers) {
            if (player instanceof Player && (p == null || p.canSee((Player) player))) {
                visiblePlayers.add((Player) player);
            }
        }

        if (args.size()  == 1) {
            if (args.get(0).equalsIgnoreCase("--all") || args.get(0).equalsIgnoreCase("-a")) {
                showAll = true;
            } else {
                // single world mode
                MultiverseWorld world = this.worldManager.getMVWorld(args.get(0));
                if (world == null) {
                    sender.sendMessage(ChatColor.RED + "That world does not exist.");
                    return;
                }

                if (!this.plugin.getMVPerms().canEnterWorld(p, world)) {
                    sender.sendMessage(ChatColor.RED + "You aren't allowed to access to this world!");
                    return;
                }

                sender.sendMessage(String.format("%s--- Players in %s%s ---", ChatColor.AQUA,
                        world.getColoredWorldString(), ChatColor.AQUA));
                sender.sendMessage(this.buildPlayerString(world, p, visiblePlayers));
                return;
            }
        }

        // multiworld mode
        sender.sendMessage(ChatColor.AQUA + "--- Worlds and their players --- "
                + visiblePlayers.size() + "/" + plugin.getServer().getMaxPlayers());
        boolean shownOne = false;
        for (MultiverseWorld world : this.worldManager.getMVWorlds()) {
            if (this.plugin.getMVPerms().canEnterWorld(p, world)) { // only show world if the player can access it
                if (showAll || !world.getCBWorld().getPlayers().isEmpty()) { // either show all or show if the world is not empty
                    sender.sendMessage(String.format("%s%s - %s", world.getColoredWorldString(), ChatColor.WHITE, buildPlayerString(world, p, visiblePlayers)));
                    shownOne = true;
                }
            }
        }
        if (!shownOne) {
            sender.sendMessage("No worlds found.");
        }
        return;
    }

    private static String buildPlayerString(MultiverseWorld world, Player viewer, final Collection<Player> visiblePlayers) {
        // Retrieve the players in this world
        List<Player> players = world.getCBWorld().getPlayers();
        StringBuilder playerBuilder = new StringBuilder();
        for (Player player : players) {
            // If the viewer is the console or the viewier is allowed to see the player, show them.
            // Make sure we're also ONLY showing online players.
            // Since we already checked visible players, we'll just make sure who we're about to show is in that.
            if (visiblePlayers.contains(player))
                playerBuilder.append(player.getDisplayName()).append(", ");
        }
        String bString = playerBuilder.toString();
        return (bString.length() == 0) ? "No players found." : bString.substring(0, bString.length() - 2);
    }
}
