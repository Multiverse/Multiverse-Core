package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class ListCommand extends MultiverseCommand {

    public ListCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("World Listing");
        this.setCommandUsage("/mvlist");
        this.setArgRange(0, 0);
        this.addKey("mvlist");
        this.addKey("mvl");
        this.addKey("mv list");
        this.setPermission("multiverse.core.list.worlds", "Displays a listing of all worlds that you can enter.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }

        String output = ChatColor.LIGHT_PURPLE + "Worlds which you can view:\n";
        for (MVWorld world : this.plugin.getMVWorlds()) {

            if (p != null && (!this.plugin.ph.canEnterWorld(p, world))) {
                continue;
            }

            ChatColor color = ChatColor.GOLD;
            Environment env = world.getEnvironment();
            if (env == Environment.NETHER) {
                color = ChatColor.RED;
            } else if (env == Environment.NORMAL) {
                color = ChatColor.GREEN;
            } else if (env == Environment.SKYLANDS) {
                color = ChatColor.AQUA;
            }
            output += world.getColoredWorldString() + ChatColor.WHITE + " - " + color + world.getEnvironment() + " \n";

        }
        String[] response = output.split("\n");
        for (String msg : response) {
            sender.sendMessage(msg);
        }
    }
}
