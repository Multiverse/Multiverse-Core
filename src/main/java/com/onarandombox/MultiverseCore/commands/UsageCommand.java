/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class UsageCommand extends MultiverseCommand {

    public UsageCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("help")
    @HelpCommand
    @CommandPermission("multiverse.core.help")
    @Syntax("[filter] [page]")
    @Description("Show Multiverse Command usage.")
    public void onUsageCommand(@NotNull CommandSender sender,
                               @NotNull CommandHelp help) {

        //TODO ACF: Proper formatting and paging.
        help.setPerPage(6);
        help.showHelp();
    }
}
