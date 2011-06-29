package com.onarandombox.MultiverseCore.command.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        System.out.println("Purged");
        this.plugin.purgeWorlds();

        return;
    }

}
