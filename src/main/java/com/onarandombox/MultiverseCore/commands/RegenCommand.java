/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commandTools.WorldFlags;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class RegenCommand extends MultiverseCommand {

    public RegenCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("regen")
    @CommandPermission("multiverse.core.regen")
    @Syntax("<world>")
    @CommandCompletion("@MVWorlds")
    @Description("Regenerates a world on your server. The previous state will be lost PERMANENTLY.")
    public void onRegenCommand(@NotNull CommandSender sender,
                               //TODO: Allow regen of unloaded worlds.
                               @NotNull @Flags("other") MultiverseWorld world,
                               @NotNull WorldFlags flags) {

        this.plugin.getMVCommandManager().getQueueManager().addToQueue(
                sender,
                regenRunnable(sender, world, flags),
                String.format("Are you sure you want to regen world '%s'?", world.getColoredWorldString())
        );
    }

    private Runnable regenRunnable(@NotNull CommandSender sender,
                                   @NotNull MultiverseWorld world,
                                   @NotNull WorldFlags flags) {

        return () -> {
            sender.sendMessage(String.format("Regening world '%s'...", world.getName()));

            //TODO: regenWorld method should take world object directly
            //TODO: Shouldn't need randomSeed, just check if seed parameter is null.
            sender.sendMessage((this.plugin.getMVWorldManager().regenWorld(
                    world.getName(),
                    flags.hasFlag("-s"),
                    flags.getSeed() == null,
                    flags.getSeed())
            )
                    ? ChatColor.GREEN + "World Regenerated!"
                    : ChatColor.RED + "World could not be regenerated!");
        };
    }
}
