/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;

/**
 * Regenerates a world.
 */
public class RegenCommand extends MultiverseCommand {

    public RegenCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Regenerates a World");
        this.setCommandUsage("/mv regen" + ChatColor.GREEN + " {WORLD}" + ChatColor.GOLD + " [-s [SEED]]");
        this.setArgRange(1, 3);
        this.addKey("mvregen");
        this.addKey("mv regen");
        this.addCommandExample("You can use the -s with no args to get a new seed:");
        this.addCommandExample("/mv regen " + ChatColor.GREEN + "MyWorld" + ChatColor.GOLD + " -s");
        this.addCommandExample("or specifiy a seed to get that one:");
        this.addCommandExample("/mv regen " + ChatColor.GREEN + "MyWorld" + ChatColor.GOLD + " -s" + ChatColor.AQUA + " gargamel");
        this.setPermission("multiverse.core.regen", "Regenerates a world on your server. The previous state will be lost "
                + ChatColor.RED + "PERMANENTLY.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Boolean useseed = (!(args.size() == 1));
        Boolean randomseed = (args.size() == 2 && args.get(1).equalsIgnoreCase("-s"));
        String seed = (args.size() == 3) ? args.get(2) : "";

        Class<?>[] paramTypes = {String.class, Boolean.class, Boolean.class, String.class};
        List<Object> objectArgs = new ArrayList<Object>();
        objectArgs.add(args.get(0));
        objectArgs.add(useseed);
        objectArgs.add(randomseed);
        objectArgs.add(seed);
        this.plugin.getCommandHandler().queueCommand(sender, "mvregen", "regenWorld", objectArgs,
                paramTypes, ChatColor.GREEN + "World Regenerated!", ChatColor.RED + "World could NOT be regenerated!");
    }
}
