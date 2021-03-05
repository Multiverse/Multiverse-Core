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
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandtools.flags.FlagGroup;
import com.onarandombox.MultiverseCore.commandtools.flags.FlagResult;
import com.onarandombox.MultiverseCore.commandtools.flags.MVFlags;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.bukkit.World.*;

@CommandAlias("mv")
public class CreateCommand extends MultiverseCoreCommand {

    public CreateCommand(MultiverseCore plugin) {
        super(plugin);
        this.setFlagGroup(FlagGroup.of(
                MVFlags.WORLD_TYPE,
                MVFlags.SEED,
                MVFlags.GENERATOR,
                MVFlags.GENERATE_STRUCTURES,
                MVFlags.SPAWN_ADJUST
        ));
    }

    @Subcommand("create")
    @CommandPermission("multiverse.core.create")
    @Syntax("<name> <env> -s [seed] -g [generator[:id]] -t [worldtype] [-n] -a [true|false]")
    @CommandCompletion("@empty @environments @flags")
    @Description("Creates a new world and loads it.")
    public void onCreateCommand(@NotNull CommandSender sender,

                                @NotNull
                                @Syntax("<name>")
                                @Description("New world name.")
                                @Flags("trim")
                                @Conditions("creatableWorldName") String worldName,

                                @NotNull
                                @Syntax("<env>")
                                @Description("The world's environment. See: /mv env")
                                Environment environment,

                                @Nullable
                                @Syntax("[world-flags]")
                                @Description("Other world settings. See: http://gg.gg/nn8bl")
                                String[] flagsArray) {

        FlagResult flags = this.getFlagGroup().calculateResult(flagsArray);

        Command.broadcastCommandMessage(sender, String.format("Starting creation of world '%s'...", worldName));
        Command.broadcastCommandMessage(sender, (this.plugin.getMVWorldManager().addWorld(
                worldName,
                environment,
                // TODO API: Should Allow FlagResult object to be passed directly
                flags.getValue(MVFlags.SEED),
                flags.getValue(MVFlags.WORLD_TYPE),
                flags.getValue(MVFlags.GENERATE_STRUCTURES),
                flags.getValue(MVFlags.GENERATOR),
                flags.getValue(MVFlags.SPAWN_ADJUST))
        )
                ? String.format("%sComplete!", ChatColor.GREEN)
                : String.format("%sFailed! See console for errors.", ChatColor.RED));
    }
}
