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
public class RemoveCommand extends MultiverseCoreCommand {

    public RemoveCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("remove")
    @CommandPermission("multiverse.core.spawn.other")
    @CommandCompletion("@MVWorlds|@unloadedWorlds")
    @Syntax("<world>")
    @Description("Unloads a world from Multiverse and removes it from worlds.yml, this does NOT DELETE the world folder.")
    public void onRemoveCommand(@NotNull CommandSender sender,

                                @Syntax("<world>")
                                @Description("World you want to remove from mv's knowledge.")
                                @NotNull
                                @Single
                                @Flags("type=world name")
                                @Conditions("isWorldInConfig") String worldName) {

        sender.sendMessage((this.plugin.getMVWorldManager().removeWorldFromConfig(worldName))
                ? String.format("World '%s' is removed from config!", worldName)
                : String.format("%sError trying to remove world from config!", ChatColor.RED));
    }
}
