package com.onarandombox.MultiverseCore.command.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;

public class WhoCommand extends Command {

    public WhoCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "Who";
        this.commandDesc = "States who is in what world";
        this.commandUsage = "/mvwho" + ChatColor.GOLD + " [WORLD]";
        this.minimumArgLength = 0;
        this.maximumArgLength = 1;
        this.commandKeys.add("mvwho");
        this.permission = "multiverse.world.list.who";
        this.opRequired = false;
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // If this command was sent from a Player then we need to check Permissions
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }

        List<MVWorld> worlds = new ArrayList<MVWorld>();

        if (args.size() > 0) {
            if (((MultiverseCore) this.plugin).isMVWorld(args.get(0))) {
                worlds.add(((MultiverseCore) this.plugin).getMVWorld(args.get(0)));
            } else {
                sender.sendMessage(ChatColor.RED + "World does not exist");
                return;
            }
        } else {
            worlds = new ArrayList<MVWorld>(((MultiverseCore) this.plugin).getMVWorlds());
        }

        for (MVWorld world : worlds) {
            if (!(((MultiverseCore) this.plugin).isMVWorld(world.getName()))) {
                continue;
            }

            if (p != null && (!((MultiverseCore) this.plugin).getPermissions().canEnterWorld(p, world))) {
                continue;
            }
            List<Player> players = world.getCBWorld().getPlayers();

            String result = "";
            if (players.size() <= 0) {
                result = "Empty";
            } else {
                for (Player player : players) {
                    result += player.getName() + " ";
                }
            }

            sender.sendMessage(world.getColoredWorldString() + ChatColor.WHITE + " - " + result);
        }
    }
}
