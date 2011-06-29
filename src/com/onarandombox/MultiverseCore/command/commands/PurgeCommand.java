package com.onarandombox.MultiverseCore.command.commands;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sun.tools.tree.ArrayAccessExpression;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;
import com.onarandombox.utils.PurgeWorlds;

public class PurgeCommand extends BaseCommand {

    public PurgeCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Purge the world ";
        this.description = "Removed the specified type of mob from the specified world.";
        this.usage = "/mvpurge" + ChatColor.GOLD + " [WORLD|all] " + ChatColor.GREEN + "{all|animals|monsters|MOBNAME}";
        this.minArgs = 1;
        this.maxArgs = 2;
        this.identifiers.add("mvpurge");
        this.permission = "multiverse.world.purge";
        this.requiresOp = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = null;
        if(sender instanceof Player) {
            p = (Player) sender;
        }
        if (args.length == 1 && p == null) {
            sender.sendMessage("This command requires a WORLD when being run from the console!");
            sender.sendMessage(this.usage);
            return;
        }
        String worldName = null;
        String deathName = null;
        if(args.length == 1) {
            worldName = p.getWorld().getName();
            deathName = args[0];
        } else {
            worldName = args[0];
            deathName = args[1];
        }
        
        if(!this.plugin.isMVWorld(worldName)) {
            sender.sendMessage("Multiverse doesn't know about " + worldName);
            sender.sendMessage("... so It cannot be purged");
            return;
        }
        MVWorld world = this.plugin.getMVWorld(worldName);
        
        System.out.println("Purged");
        PurgeWorlds purger = this.plugin.getWorldPurger();
        ArrayList<String> thingsToKill = new ArrayList<String>();
        if(deathName.equalsIgnoreCase("all") || deathName.equalsIgnoreCase("animals") || deathName.equalsIgnoreCase("monsters")) {
            thingsToKill.add(deathName.toUpperCase());
        } else {
            Collections.addAll(thingsToKill, deathName.split(","));
        }
        purger.purgeWorld(sender, world, thingsToKill, false, false);

        return;
    }

}
