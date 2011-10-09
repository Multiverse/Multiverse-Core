/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.utils.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;

public class WhoCommand extends MultiverseCommand {

    private WorldManager worldManager;

    public WhoCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Who?");
        this.setCommandUsage("/mv who" + ChatColor.GOLD + " [WORLD]");
        this.setArgRange(0, 1);
        this.addKey("mv who");
        this.addKey("mvw");
        this.addKey("mvwho");
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

        List<MVWorld> worlds = new ArrayList<MVWorld>();

        if (args.size() > 0) {
            MVWorld world = this.worldManager.getMVWorld(args.get(0));
            if (args.get(0).equalsIgnoreCase("--all") || args.get(0).equalsIgnoreCase("-a")) {
                showAll = true;
                worlds = new ArrayList<MVWorld>(this.worldManager.getMVWorlds());
            } else if (world != null) {
                if (!world.isHidden()) {
                    worlds.add(world);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "World does not exist");
                return;
            }
        } else {
            worlds = new ArrayList<MVWorld>(this.worldManager.getMVWorlds());
        }

        if (worlds.size() == 0) {
            sender.sendMessage("Multiverse does not know about any of your worlds :(");
        } else if (worlds.size() == 1) {
            sender.sendMessage(ChatColor.AQUA + "--- Players in" + worlds.get(0).getColoredWorldString() + ChatColor.AQUA + " ---");
        } else {
            sender.sendMessage(ChatColor.AQUA + "--- There are players in ---");
        }

        for (MVWorld world : worlds) {
            if (!(this.worldManager.isMVWorld(world.getName()))) {
                continue;
            }

            if (p != null && (!this.plugin.getMVPerms().canEnterWorld(p, world))) {
                continue;
            }
            List<Player> players = world.getCBWorld().getPlayers();

            String result = "";
            if (players.size() <= 0 && !showAll) {
                continue;
            }

            if (players.size() <= 0) {
                result = "Empty";
            } else {
                for (Player player : players) {
                    result += player.getDisplayName() + " ";
                }
            }

            sender.sendMessage(world.getColoredWorldString() + ChatColor.WHITE + " - " + result);
        }
    }
}
