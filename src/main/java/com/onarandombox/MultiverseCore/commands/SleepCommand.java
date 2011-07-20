package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class SleepCommand extends MultiverseCommand {

    public SleepCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Go to Sleep");
        this.setCommandUsage("/mv sleep");
        this.setArgRange(0, 0);
        this.addKey("mv sleep");
        this.setPermission("multiverse.core.sleep", "Takes you the latest bed you've slept in (Currently BROKEN).", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }

        if (p == null) {
            return;
        }
//        MVPlayerSession session = this.plugin.getPlayerSession(p);
//        if (session.getBedRespawnLocation() != null) {
//            p.teleport(session.getBedRespawnLocation());
//        } else {
//            sender.sendMessage("Hmm this is awkward...");
//            sender.sendMessage("Something is wrong with your bed.");
//            sender.sendMessage("It has either been destroyed or obstructed.");
//        }
    }
}
