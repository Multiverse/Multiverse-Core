package com.onarandombox.MultiverseCore.command.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;
import com.onarandombox.utils.PurgeWorlds;

public class MVPurge extends BaseCommand {

    public MVPurge(MultiverseCore plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
//        if (!(args.length > 0)) {
//            return fal;
//        }
//
//        if (!(sender instanceof Player)) {
//            sender.sendMessage("This command needs to be used from inside the game!");
//            return true;
//        }
//
//        Player p = (Player) sender;
//        List<String> creatures = new ArrayList<String>();
//
//        for (String creature : args[0].toUpperCase().split(",")) {
//            creatures.add(creature);
//        }
//
//        new PurgeWorlds(plugin).purge(sender, p.getWorld(), creatures);
//
//        return true;
    }

}
