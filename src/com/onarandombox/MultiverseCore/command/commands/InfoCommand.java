package com.onarandombox.MultiverseCore.command.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;

public class InfoCommand extends Command {

    public InfoCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "World Information";
        this.commandDesc = "Returns detailed information on the world.";
        this.commandUsage = "/mvinfo" + ChatColor.GOLD + " [WORLD] " + ChatColor.DARK_PURPLE + " <Page #>";
        this.minimumArgLength = 0;
        this.maximumArgLength = 2;
        this.commandKeys.add("mvinfo");
        this.permission = "multiverse.world.info";
        this.opRequired = false;
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // Check if the command was sent from a Player.
        String worldName = "";
        if (sender instanceof Player && args.size() == 0) {
            worldName = ((Player) sender).getWorld().getName();
        } else if (args.size() == 0) {
            sender.sendMessage("You must enter a" + ChatColor.GOLD + " world" + ChatColor.WHITE + " from the console!");
            return;
        } else {
            worldName = args.get(0);
        }
        if (((MultiverseCore) this.plugin).isMVWorld(worldName)) {
            for (String s : buildEntireCommand(((MultiverseCore) this.plugin).getMVWorld(worldName))) {
                sender.sendMessage(s);
            }
        }
    }

    private String[] buildEntireCommand(MVWorld world) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String[]> pagedInfo = new ArrayList<String[]>();
        String[] aPage = new String[5];
        // World Name: 1
        aPage[0] = "World: " + world.getName();

        // World Scale: 1
        aPage[1] = "World Scale: " + world.getScaling();

        // PVP: 1
        aPage[2] = "PVP: " + world.getPvp();
        aPage[3] = "Animals: " + world.allowAnimalSpawning();
        aPage[4] = "Monsters: " + world.allowMonsterSpawning();

        // This feature is not mission critical and I am spending too much time on it...
        // Stopping work on it for now --FF 20110623
        // // Animal Spawning: X
        // sb.append("Animals can spawn: ");
        // sb.append(world.animals?"Yes":"No");
        // sb.append("\n");
        // if (!world.animalList.isEmpty()) {
        // sb.append("Except: \n");
        // for (String s : world.animalList) {
        // sb.append(" - " + s + "\n");
        // }
        // }
        //
        // // Monster Spawning
        // sb.append("Monsters can spawn: ");
        // sb.append(world.monsters?"Yes":"No");
        // sb.append("\n");
        // if (!world.monsterList.isEmpty()) {
        // sb.append("Except: \n");
        // for (String s : world.monsterList) {
        // sb.append(" - " + s + "\n");
        // }
        // }
        //
        // // Whitelist
        // if (!world.playerWhitelist.isEmpty()) {
        // sb.append("Whitelisted Players: \n");
        // for (String s : world.playerWhitelist) {
        // if (!s.matches("[gG]:.+")) {
        // sb.append(" - " + s + "\n");
        // }
        // }
        // sb.append("Whitelisted Groups: \n");
        // for (String s : world.playerWhitelist) {
        // if (s.matches("[gG]:")) {
        // sb.append(" - " + s.split("[g]:")[1] + "\n");
        // }
        // }
        // }
        return aPage;
    }

    private ChatColor getChatColor(boolean positive) {
        return positive ? ChatColor.GREEN : ChatColor.RED;
    }

}
