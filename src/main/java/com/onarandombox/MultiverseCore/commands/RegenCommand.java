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
import com.onarandombox.MultiverseCore.commandtools.flag.FlagGroup;
import com.onarandombox.MultiverseCore.commandtools.flag.FlagResult;
import com.onarandombox.MultiverseCore.commandtools.flag.CoreFlags;
import com.onarandombox.MultiverseCore.commandtools.queue.QueuedCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@CommandAlias("mv")
public class RegenCommand extends MultiverseCoreCommand {

    public RegenCommand(MultiverseCore plugin) {
        super(plugin);
        this.setFlagGroup(FlagGroup.of(CoreFlags.RANDOM_SEED));
    }

    @Subcommand("regen")
    @CommandPermission("multiverse.core.regen")
    @Syntax("<world> [-s [seed]]")
    @CommandCompletion("@MVWorlds @worldFlags:-s")
    @Description("Regenerates a world on your server. The previous state will be lost PERMANENTLY.")
    public void onRegenCommand(@NotNull CommandSender sender,

                               @NotNull
                               @Syntax("<world>")
                               @Description("World that you want to regen.")
                               @Flags("other") MultiverseWorld world,

                               @Nullable
                               @Syntax("[-s [seed]]")
                               @Description("Other world settings. See: http://gg.gg/nn8lk")
                               String[] flagsArray) {

        FlagResult flags = this.getFlagGroup().calculateResult(flagsArray);

        this.plugin.getMVCommandManager().getQueueManager().addToQueue(new QueuedCommand.Builder()
                .sender(sender)
                .action(regenRunnable(sender, world, flags))
                .prompt("Are you sure you want to regen world '%s'?", world.getColoredWorldString())
                .build()
        );
    }

    private Runnable regenRunnable(@NotNull CommandSender sender,
                                   @NotNull MultiverseWorld world,
                                   @NotNull FlagResult flags) {

        return () -> {
            sender.sendMessage(String.format("Regening world '%s'...", world.getName()));

            //TODO: API should allow regen of unloaded worlds.
            sender.sendMessage((this.plugin.getMVWorldManager().regenWorld(
                    world.getName(),
                    !flags.isDefaulted(CoreFlags.RANDOM_SEED),
                    !flags.isByUserInput(CoreFlags.RANDOM_SEED),
                    flags.getValue(CoreFlags.RANDOM_SEED))
            )
                    ? String.format("%sWorld Regenerated!", ChatColor.GREEN)
                    : String.format("%sWorld could not be regenerated!", ChatColor.RED));
        };
    }
}
