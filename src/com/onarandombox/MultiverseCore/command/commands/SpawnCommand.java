package com.onarandombox.MultiverseCore.command.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;

public class SpawnCommand extends Command {

    public SpawnCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "Spawn";
        this.commandDesc = "Transports the player to the that player's current world Spawn Point.";
        this.commandUsage = "/mvspawn" + ChatColor.GOLD + " [PLAYER]";
        this.minimumArgLength = 0;
        this.maximumArgLength = 1;
        this.commandKeys.add("mvspawn");
        this.permission = "multiverse.world.spawn.self";
        this.opRequired = false;
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Player commandSender = null;
        if (sender instanceof Player) {
            commandSender = (Player) sender;
        }
        // If a persons name was passed in, you must be A. the console, or B have permissions
        if (args.size() == 1) {
            if (commandSender != null && !((MultiverseCore) this.plugin).getPermissions().hasPermission(commandSender, "multiverse.world.spawn.other", true)) {
                sender.sendMessage("You don't have permission to teleport another player to spawn.");
                return;
            }
            Player target = this.plugin.getServer().getPlayer(args.get(0));
            if (target != null) {
                target.sendMessage("Teleporting to this world's spawn...");
                target.teleport(target.getWorld().getSpawnLocation());
                if (commandSender != null) {
                    target.sendMessage("You were teleported by: " + ChatColor.YELLOW + commandSender.getName());
                } else {
                    target.sendMessage("You were teleported by: " + ChatColor.LIGHT_PURPLE + "the console");
                }
            } else {
                sender.sendMessage(args.get(0) + " is not logged on right now!");
            }
        } else {
            if (commandSender != null) {
                commandSender.sendMessage("Teleporting to this world's spawn...");
                commandSender.teleport(commandSender.getWorld().getSpawnLocation());
            } else {
                sender.sendMessage("From the console, you must provide a PLAYER.");
            }
        }
    }
}
