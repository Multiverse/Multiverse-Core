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
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commandTools.contexts.WorldFlags;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@CommandAlias("mv")
public class RegenCommand extends MultiverseCommand {

    public RegenCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("regen")
    @CommandPermission("multiverse.core.regen")
    @Syntax("<world> [-s [seed]]")
    @CommandCompletion("@MVWorlds -s @empty")
    @Description("Regenerates a world on your server. The previous state will be lost PERMANENTLY.")
    public void onRegenCommand(@NotNull CommandSender sender,

                               @Syntax("<world>")
                               @Description("World that you want to regen.")
                               @NotNull @Flags("other") MultiverseWorld world,

                               @Syntax("[-s [seed]]")
                               @Description("Other world settings. See: http://gg.gg/nn8lk")
                               @Nullable @Optional String[] flagsArray) {

        WorldFlags flags = new WorldFlags(sender, this.plugin, flagsArray);

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

            //TODO: API should allow regen of unloaded worlds.
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
