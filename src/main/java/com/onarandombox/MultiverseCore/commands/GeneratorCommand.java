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
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("mv")
public class GeneratorCommand extends MultiverseCommand {

    public GeneratorCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("gens|generators")
    @CommandPermission("multiverse.core.generator")
    @Description("Shows a list of Loaded Generator Plugins.")
    public void onGeneratorCommand(@NotNull CommandSender sender) {
        showAvailableGenerator(sender);
    }

    public static void showAvailableGenerator(@NotNull CommandSender sender) {
        List<String> generators = Arrays.stream(Bukkit.getServer().getPluginManager().getPlugins())
                .filter(Plugin::isEnabled)
                .filter(plugin -> plugin.getDefaultWorldGenerator("world", "") != null)
                .map(plugin -> plugin.getDescription().getName())
                .collect(Collectors.toList());

        if (generators.size() == 0) {
             sender.sendMessage(String.format("%sYou do not have any generator plugins installed.", ChatColor.RED));
            return;
        }

        //TODO ACF: Use List Display
        StringBuilder loadedGens = new StringBuilder();
        boolean altColor = false;
        for (String s : generators) {
            loadedGens.append(altColor ? ChatColor.YELLOW : ChatColor.WHITE)
                    .append(s)
                    .append(' ');
            altColor ^= true;
        }

        sender.sendMessage(String.format("%s--- Available Generator Plugins ---", ChatColor.AQUA));
        sender.sendMessage(loadedGens.toString());
    }
}
