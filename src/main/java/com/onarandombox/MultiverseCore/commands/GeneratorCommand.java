/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandtools.flag.CoreFlags;
import com.onarandombox.MultiverseCore.displaytools.ColorAlternator;
import com.onarandombox.MultiverseCore.displaytools.ContentDisplay;
import com.onarandombox.MultiverseCore.displaytools.DisplayHandlers;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@CommandAlias("mv")
public class GeneratorCommand extends MultiverseCoreCommand {

    public GeneratorCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("gens|generators")
    @CommandPermission("multiverse.core.generator")
    @Description("Shows a list of Loaded Generator Plugins.")
    public void onGeneratorCommand(@NotNull CommandSender sender) {
        new ContentDisplay.Builder<Collection<String>>()
                .sender(sender)
                .header("%s--- Available Generator Plugins ---", ChatColor.GOLD)
                .contents(CoreFlags.GENERATOR.suggestValue())
                .emptyMessage("%sYou do not have any generator plugins installed.", ChatColor.RED)
                .displayHandler(DisplayHandlers.INLINE_LIST)
                .colorTool(ColorAlternator.with(ChatColor.AQUA, ChatColor.WHITE))
                .display();
    }
}
