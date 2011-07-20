package com.onarandombox.MultiverseCore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class WhoCommand extends MultiverseCommand {

    public WhoCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Who?");
        this.setCommandUsage("/mvwho" + ChatColor.GOLD + " [WORLD]");
        this.setArgRange(0, 1);
        this.addKey("mvwho");
        this.setPermission("multiverse.core.list.who", "States who is in what world.", PermissionDefault.OP);
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
            if (this.plugin.isMVWorld(args.get(0))) {
                worlds.add(this.plugin.getMVWorld(args.get(0)));
            } else {
                sender.sendMessage(ChatColor.RED + "World does not exist");
                return;
            }
        } else {
            worlds = new ArrayList<MVWorld>(this.plugin.getMVWorlds());
        }

        for (MVWorld world : worlds) {
            if (!(this.plugin.isMVWorld(world.getName()))) {
                continue;
            }

            if (p != null && (!this.plugin.getPermissions().canEnterWorld(p, world))) {
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
