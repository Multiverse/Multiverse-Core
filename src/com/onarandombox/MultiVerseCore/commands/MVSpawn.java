package com.onarandombox.MultiVerseCore.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiVerseCore.MVCommandHandler;
import com.onarandombox.MultiVerseCore.MultiVerseCore;

public class MVSpawn extends MVCommandHandler {

    public MVSpawn(MultiVerseCore plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        // TODO: Permissions
        if(sender instanceof Player){
            Player p = (Player) sender;
            p.teleportTo(p.getWorld().getSpawnLocation());
        } else {
            sender.sendMessage("Must be used in game.");
        }
        return true;
    }

}
