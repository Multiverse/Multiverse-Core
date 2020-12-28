package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import com.onarandombox.MultiverseCore.commandTools.ColourAlternator;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SubModulesCommand {

    public static class NetherPortals extends BaseCommand {
        @CommandAlias("mvnp")
        @CommandPermission("multiverse.core.submodules")
        @Description("Suggest download for Multiverse-NetherPortals.")
        public void onSuggestCommand(@NotNull CommandSender sender) {
            suggestDownload(
                    sender,
                    "Multiverse-NetherPortals",
                    new ColourAlternator(ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE),
                    "https://dev.bukkit.org/projects/multiverse-netherportals"
            );
        }
    }

    public static class Portals extends BaseCommand {
        @CommandAlias("mvp")
        @CommandPermission("multiverse.core.submodules")
        @Description("Suggest download for Multiverse-Portals.")
        public void onSuggestCommand(@NotNull CommandSender sender) {
            suggestDownload(
                    sender,
                    "Multiverse-Portals",
                    new ColourAlternator(ChatColor.DARK_RED, ChatColor.RED),
                    "https://dev.bukkit.org/projects/multiverse-portals"
            );
        }
    }

    public static class Inventories extends BaseCommand {
        @CommandAlias("mvinv")
        @CommandPermission("multiverse.core.submodules")
        @Description("Suggest download for Multiverse-Inventories.")
        public void onSuggestCommand(@NotNull CommandSender sender) {
            suggestDownload(
                    sender,
                    "Multiverse-Inventories",
                    new ColourAlternator(ChatColor.DARK_AQUA, ChatColor.AQUA),
                    "https://dev.bukkit.org/projects/multiverse-inventories"
            );
        }
    }

    private static void suggestDownload(@NotNull CommandSender sender,
                                @NotNull String pluginName,
                                @NotNull ColourAlternator colours,
                                @NotNull String downloadLink) {

        sender.sendMessage(colours.getColorThis() + pluginName + ChatColor.WHITE +
                " is not installed on this server. You can learn more and download it at:");
        sender.sendMessage(colours.getColorThat() + downloadLink);
    }
}
