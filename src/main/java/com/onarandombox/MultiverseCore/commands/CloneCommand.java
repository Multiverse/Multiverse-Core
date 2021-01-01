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
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class CloneCommand extends MultiverseCommand {

    public CloneCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("clone")
    @CommandPermission("multiverse.core.clone")
    @Syntax("<world> <name>")
    @CommandCompletion("@MVWorlds|@unloadedWorlds @empty")
    @Description("Clones a world.")
    public void onCloneCommand(@NotNull CommandSender sender,

                               @Syntax("<world>")
                               @Description("Current multiverse world")
                               @NotNull @Conditions("isWorldInConfig") String worldName,

                               @Syntax("<name>")
                               @Description("New cloned world name.")
                               @NotNull @Single @Flags("trim") @Conditions("creatableWorldName") String newWorldName) {

        sender.sendMessage((this.plugin.getMVWorldManager().cloneWorld(worldName, newWorldName))
                ? String.format("%sWorld cloned!", ChatColor.GREEN)
                : String.format("%sWorld could not be cloned! See console for more details.", ChatColor.RED));
    }
}
