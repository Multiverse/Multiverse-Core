package com.onarandombox.MultiverseCore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class InfoCommand extends MultiverseCommand {

    public InfoCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("World Information");
        this.setCommandUsage("/mvinfo" + ChatColor.GOLD + " [WORLD]");
        this.setArgRange(0, 1);
        this.addKey("mvinfo");
        this.addKey("mvi");
        this.addKey("mv info");
        this.setPermission("multiverse.world.coord", "Returns detailed information on the world.", PermissionDefault.OP);
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
        if (this.plugin.isMVWorld(worldName)) {
            for (String s : buildEntireCommand(this.plugin.getMVWorld(worldName))) {
                sender.sendMessage(s);
            }
        }
    }

    private List<String> buildEntireCommand(MVWorld world) {
        List<String> page = new ArrayList<String>();
        // World Name: 1
        page.add("World: " + world.getName());

        // World Scale: 1
        page.add("World Scale: " + world.getScaling());

        // PVP: 1
        page.add("PVP (MV): " + world.getPvp());
        page.add("PVP: " + world.getCBWorld().getPVP());
        page.add("Fake PVP: " + world.getFakePVP());
        page.add("Animals (MV): " + world.allowAnimalSpawning());
        page.add("Animals: " + world.getCBWorld().getAllowAnimals());
        page.add("Monsters (MV): " + world.allowMonsterSpawning());
        page.add("Monsters: " + world.getCBWorld().getAllowMonsters());
        page.add("Respawn to: " + world.getRespawnToWorld());

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
        return page;
    }

    private ChatColor getChatColor(boolean positive) {
        return positive ? ChatColor.GREEN : ChatColor.RED;
    }

}
