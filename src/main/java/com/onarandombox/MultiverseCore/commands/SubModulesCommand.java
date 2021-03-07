package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import com.onarandombox.MultiverseCore.displaytools.ColorAlternator;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SubModulesCommand {

    public static class NetherPortals extends BaseCommand {

        @HelpCommand
        @CommandAlias("mvnp")
        @CommandPermission("multiverse.core.submodules")
        @Description("Suggest download for Multiverse-NetherPortals.")
        public void onSuggestCommand(@NotNull CommandSender sender,
                                     @NotNull CommandHelp help) {

            suggestDownload(
                    sender,
                    "Multiverse-NetherPortals",
                    ColorAlternator.with(ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE),
                    "https://dev.bukkit.org/projects/multiverse-netherportals"
            );
        }
    }

    public static class Portals extends BaseCommand {

        @HelpCommand
        @CommandAlias("mvp")
        @CommandPermission("multiverse.core.submodules")
        @Description("Suggest download for Multiverse-Portals.")
        public void onSuggestCommand(@NotNull CommandSender sender,
                                     @NotNull CommandHelp help) {

            suggestDownload(
                    sender,
                    "Multiverse-Portals",
                    ColorAlternator.with(ChatColor.DARK_RED, ChatColor.RED),
                    "https://dev.bukkit.org/projects/multiverse-portals"
            );
        }
    }

    public static class Inventories extends BaseCommand {

        @HelpCommand
        @CommandAlias("mvinv")
        @CommandPermission("multiverse.core.submodules")
        @Description("Suggest download for Multiverse-Inventories.")
        public void onSuggestCommand(@NotNull CommandSender sender,
                                     @NotNull CommandHelp help) {

            suggestDownload(
                    sender,
                    "Multiverse-Inventories",
                    ColorAlternator.with(ChatColor.DARK_AQUA, ChatColor.AQUA),
                    "https://dev.bukkit.org/projects/multiverse-inventories"
            );
        }
    }

    private static void suggestDownload(@NotNull CommandSender sender,
                                        @NotNull String pluginName,
                                        @NotNull ColorAlternator colours,
                                        @NotNull String downloadLink) {

        sender.sendMessage(String.format("%s%s%s is not installed on this server. You can learn more and download it at:",
                colours.getThisColor(), pluginName, ChatColor.WHITE));
        sender.sendMessage(String.format("%s%s", colours.getThatColor(), downloadLink));
    }
}
