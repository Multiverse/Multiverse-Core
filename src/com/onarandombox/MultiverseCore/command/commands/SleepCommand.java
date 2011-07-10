package com.onarandombox.MultiverseCore.command.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MVPlayerSession;
import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;

public class SleepCommand extends Command {

    public SleepCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "Go To Sleep";
        this.commandDesc = "Takes you the latest bed you've slept in.";
        this.commandUsage = "/mv sleep";
        this.minimumArgLength = 0;
        this.maximumArgLength = 0;
        this.commandKeys.add("mv sleep");
        this.permission = "multiverse.sleep";
        this.opRequired = false;
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }
        
        if(p == null) {
            return;
        }
        MVPlayerSession session = ((MultiverseCore) this.plugin).getPlayerSession(p);
        if(session.getBedRespawnLocation() != null) {
            p.teleport(session.getBedRespawnLocation());
        } else {
            sender.sendMessage("Hmm this is awkward...");
            sender.sendMessage("Something is wrong with your bed.");
            sender.sendMessage("It has either been destroyed or obstructed.");
        }
    }
}
