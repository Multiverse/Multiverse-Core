/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandtools.queue.QueuedCommand;
import com.pneumaticraft.commandhandler.CommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Regenerates a world.
 */
public class RegenCommand extends MultiverseCommand {

    public RegenCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Regenerates a World");
        this.setCommandUsage("/mv regen" + ChatColor.GREEN + " {WORLD}" + ChatColor.GOLD + " [-s [SEED]] [--keep-gamerules]");
        this.setArgRange(1, 4);
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
        String worldName = args.get(0);
        boolean useseed = CommandHandler.hasFlag("-s", args);
        String seedflag = CommandHandler.getFlag("-s", args);
        boolean keepGamerules = CommandHandler.hasFlag("--keep-gamerules", args);

        boolean randomseed = seedflag == null || seedflag.isEmpty() || seedflag.equalsIgnoreCase("--keep-gamerules");
        String seed = randomseed ? "" : seedflag;

        this.plugin.getCommandQueueManager().addToQueue(new QueuedCommand(
                sender,
                doWorldRegen(sender, worldName, useseed, randomseed, seed, keepGamerules),
                String.format("Are you sure you want to regen '%s'? You cannot undo this action.", worldName)
        ));
    }

    private Runnable doWorldRegen(@NotNull CommandSender sender,
                                  @NotNull String worldName,
                                  boolean useSeed,
                                  boolean randomSeed,
                                  @NotNull String seed,
                                  boolean keepGamerules) {

        return () -> {
            if (this.plugin.getMVWorldManager().regenWorld(worldName, useSeed, randomSeed, seed, keepGamerules)) {
                sender.sendMessage(ChatColor.GREEN + "World Regenerated!");
                return;
            }
            sender.sendMessage(ChatColor.RED + "World could NOT be regenerated!");
        };
    }
}
