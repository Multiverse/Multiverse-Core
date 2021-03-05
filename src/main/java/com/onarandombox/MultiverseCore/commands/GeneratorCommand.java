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
import com.onarandombox.MultiverseCore.commandtools.display.ColorAlternator;
import com.onarandombox.MultiverseCore.commandtools.display.ContentCreator;
import com.onarandombox.MultiverseCore.commandtools.display.inline.ListDisplay;
import com.onarandombox.MultiverseCore.commandtools.flags.CoreFlags;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@CommandAlias("mv")
public class GeneratorCommand extends MultiverseCoreCommand {

    public GeneratorCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("gens|generators")
    @CommandPermission("multiverse.core.generator")
    @Description("Shows a list of Loaded Generator Plugins.")
    public void onGeneratorCommand(@NotNull CommandSender sender) {
        showAvailableGenerator(this.plugin, sender);
    }

    public static void showAvailableGenerator(@NotNull MultiverseCore plugin,
                                              @NotNull CommandSender sender) {

        new ListDisplay().withSender(sender)
                .withHeader(String.format("%s--- Available Generator Plugins ---", ChatColor.GOLD))
                .withCreator(getGeneratorContent())
                .withColors(new ColorAlternator(ChatColor.AQUA, ChatColor.WHITE))
                .withEmptyMessage(String.format("%sYou do not have any generator plugins installed.", ChatColor.RED))
                .build()
                .run();
    }

    private static ContentCreator<List<String>> getGeneratorContent() {
        return () -> (List<String>) CoreFlags.GENERATOR.suggestValue();
    }
}
