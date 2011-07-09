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
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class WhoCommand extends BaseCommand {

    public WhoCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Who";
        this.description = "States who is in what world.";
        this.usage = "/mvwho" + ChatColor.GOLD + " [WORLD]";
        this.minArgs = 0;
        this.maxArgs = 1;
        this.identifiers.add("mvwho");
        this.permission = "multiverse.world.list.who";
        this.requiresOp = false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // If this command was sent from a Player then we need to check Permissions
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }

        List<MVWorld> worlds = new ArrayList<MVWorld>();

        if (args.length > 0) {
            if (this.plugin.isMVWorld(args[0])) {
                worlds.add(this.plugin.getMVWorld(args[0]));
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

            World w = this.plugin.getServer().getWorld(world.getName());
            if (p != null && (!this.plugin.ph.canEnterWorld(p, w))) {
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
            List<Player> players = w.getPlayers();

            String result = "";
            if (players.size() <= 0) {
                result = "Empty";
            } else {
                for (Player player : players) {
                    result += player.getName() + " ";
                }
            }
            String worldName = world.getName();
            if(world.getAlias() != null && world.getAlias().length() > 0) {
                worldName = world.getAlias();
                color = world.getAliasColor();
            }

            sender.sendMessage(color + worldName + ChatColor.WHITE + " - " + result);
        }
        return;
    }

}
