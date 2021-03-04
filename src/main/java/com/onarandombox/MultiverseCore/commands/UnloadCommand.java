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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class UnloadCommand extends MultiverseCoreCommand {

    public UnloadCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("unload")
    @CommandPermission("multiverse.core.unload")
    @Syntax("<world>")
    @CommandCompletion("@MVWorlds")
    @Description("Unloads a world from Multiverse. This does NOT remove the world folder. This does NOT remove it from the config file.")
    public void onUnloadCommand(@NotNull CommandSender sender,

                                @Syntax("<world>")
                                @Description("Name of world you want to unload.")
                                @NotNull @Flags("other") MultiverseWorld world) {

        //TODO API: Should be able to use MVWorld object directly
        if (!this.plugin.getMVWorldManager().unloadWorld(world.getName())) {
            sender.sendMessage(String.format("Error unloading world '%s'! See console for more details.",
                    world.getColoredWorldString()));
            return;
        }
        Command.broadcastCommandMessage(sender,  String.format("Unloaded world '%s'!", world.getColoredWorldString()));
    }
}
