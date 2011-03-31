package com.onarandombox.MultiVerseCore.commands;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiVerseCore.MVCommandHandler;
import com.onarandombox.MultiVerseCore.MultiVerseCore;

public class MVList extends MVCommandHandler {

    public MVList(MultiVerseCore plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
            if (!(plugin.ph.has(p, "multiverse.world.list"))) {
                sender.sendMessage("You do not have access to this command.");
                return true;
            }
        }

        String output = ChatColor.GREEN + "Worlds which you can view - \n";
        for (int i = 0; i < plugin.getServer().getWorlds().size(); i++) {

            World world = plugin.getServer().getWorlds().get(i);

            if (!(plugin.worlds.containsKey(world.getName()))) {
                continue;
            }
            if (p != null && (!plugin.ph.canEnterWorld(p, world))) {
                continue;
            }

            ChatColor color;

            if (world.getEnvironment() == Environment.NETHER)
                color = ChatColor.RED;
            else
                color = ChatColor.GREEN;

            output += color + world.getName() + " - " + world.getEnvironment().toString() + " \n";

        }
        String[] response = output.split("\n");
        for (String msg : response) {
            sender.sendMessage(msg);
        }
        return true;
    }
}
