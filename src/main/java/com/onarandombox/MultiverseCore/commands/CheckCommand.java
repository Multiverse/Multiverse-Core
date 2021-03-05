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
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.commandtools.contexts.RequiredPlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class CheckCommand extends MultiverseCoreCommand {

    public CheckCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("check")
    @CommandPermission("multiverse.core.debug")
    @Syntax("<player> <destination>")
    @CommandCompletion("@players @destinations|@MVWorlds")
    @Description("Checks to see if a player can go to a destination. Prints debug if false.")
    public void onCheckCommand(@NotNull CommandSender sender,

                               @NotNull
                               @Syntax("<player>")
                               @Description("Player to check destination on.")
                               RequiredPlayer player,

                               @NotNull
                               @Syntax("<destination>")
                               @Description("A destination location, e.g. a world name.")
                               MVDestination destination) {

        this.plugin.getMVPerms().tellMeWhyICantDoThis(sender, player.get(), destination);
    }
}
