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
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.annotation.Values;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CommandAlias("mv")
@Subcommand("config")
@CommandPermission("multiverse.core.config")
public class ConfigCommand extends MultiverseCommand {

    public ConfigCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("list")
    @Description("Show multiverse config values.")
    public void onShowCommand(@NotNull CommandSender sender) {
        List<String> configList = new ArrayList<>();
        Map<String, Object> serializedConfig = this.plugin.getMVConfig().serialize();

        for (Map.Entry<String, Object> entry : serializedConfig.entrySet()) {

            configList.add(ChatColor.GREEN + entry.getKey()
                    + ChatColor.WHITE + " = "
                    + ChatColor.GOLD + entry.getValue().toString()
                    + ChatColor.WHITE);
        }

        sender.sendMessage(String.join(", ", configList));
    }

    @Subcommand("set")
    @Syntax("<property> <value>")
    @CommandCompletion("@MVConfigs")
    @Description("Set Global MV Variables.")
    public void onSetCommand(@NotNull CommandSender sender,
                             @NotNull @Values("@MVConfigs") String property,
                             @NotNull @Single String value) {

        property = property.toLowerCase();

        if (!this.plugin.getMVConfig().setConfigProperty(property, value)) {
            sender.sendMessage(String.format("%sSetting '%s' to '%s' failed!", ChatColor.RED, property, value));
            return;
        }

        if (!this.plugin.saveMVConfigs()) {
            sender.sendMessage(ChatColor.RED + "Failed to save config! Check your console for details.");
            return;
        }

        // special rule, don't forget to set the world!
        //TODO API: Potentially should move to MultiverseCore#loadConfigs method
        if (property.equalsIgnoreCase("firstspawnworld")) {
            this.plugin.getMVWorldManager().setFirstSpawnWorld(value);
        }

        sender.sendMessage(ChatColor.GREEN + "Success! " + ChatColor.WHITE + "Config option " + ChatColor.AQUA
                + property + ChatColor.WHITE + " is now set to " + ChatColor.GREEN + value + ChatColor.WHITE + ".");
        this.plugin.loadConfigs();
    }
}
