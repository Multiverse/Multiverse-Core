package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class ListCommand extends BaseCommand {

    public ListCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "World Listing";
        this.description = "Displays a listing of all worlds that you can enter";
        this.usage = "/mvlist";
        this.minArgs = 0;
        this.maxArgs = 0;
        this.identifiers.add("mvlist");
        this.permission = "multiverse.world.list";
        this.requiresOp = false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }

        String output = ChatColor.LIGHT_PURPLE + "Worlds which you can view:\n";
        for (MVWorld world : this.plugin.getMVWorlds()) {

            if (p != null && (!this.plugin.ph.canEnterWorld(p, world.getCBWorld()))) {
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
            String worldName = world.getName();
            if (world.getAlias() != null && world.getAlias().length() > 0) {
                worldName = world.getAliasColor() + world.getAlias() + ChatColor.WHITE;
            }
            output += ChatColor.WHITE + worldName + " - " + color + world.getEnvironment() + " \n";

        }
        String[] response = output.split("\n");
        for (String msg : response) {
            sender.sendMessage(msg);
        }
    }
}
