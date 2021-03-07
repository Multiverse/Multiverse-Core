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
import com.onarandombox.MultiverseCore.commandtools.display.ContentFilter;
import com.onarandombox.MultiverseCore.displaytools.ColorAlternator;
import com.onarandombox.MultiverseCore.displaytools.ContentDisplay;
import com.onarandombox.MultiverseCore.displaytools.DisplayHandlers;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@CommandAlias("mv")
@Subcommand("config")
@CommandPermission("multiverse.core.config")
public class ConfigCommand extends MultiverseCoreCommand {

    public ConfigCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("list")
    @Syntax("[filter]")
    @Description("Show multiverse config values.")
    public void onShowCommand(@NotNull CommandSender sender,
                              @NotNull ContentFilter filter) {

        new ContentDisplay.Builder<Map<String, Object>>()
                .sender(sender)
                .header("%s===[ Multiverse Config ]===", ChatColor.LIGHT_PURPLE)
                .contents(this.plugin.getMVConfig().serialize())
                .emptyMessage("No config values found.")
                .displayHandler(DisplayHandlers.INLINE_MAP)
                .colorTool(ColorAlternator.with(ChatColor.GREEN, ChatColor.GOLD))
                //TODO: Filter
                .display();
    }

    @Subcommand("set")
    @Syntax("<property> <value>")
    @CommandCompletion("@MVConfigs")
    @Description("Set Global MV Variables.")
    public void onSetCommand(@NotNull CommandSender sender,

                             @NotNull
                             @Syntax("<property>")
                             @Description("Config option.")
                             @Values("@MVConfigs") String property,

                             @NotNull
                             @Syntax("<value>")
                             @Description("New value for the given config option.")
                             @Single String value) {

        property = property.toLowerCase();

        if (!this.plugin.getMVConfig().setConfigProperty(property, value)) {
            sender.sendMessage(String.format("%sSetting '%s' to '%s' failed!", ChatColor.RED, property, value));
            return;
        }

        if (!this.plugin.saveMVConfigs()) {
            sender.sendMessage(String.format("%sFailed to save config! Check your console for details.", ChatColor.RED));
            return;
        }

        // special rule, don't forget to set the world!
        //TODO API: Potentially should move to MultiverseCore#loadConfigs method
        if (property.equalsIgnoreCase("firstspawnworld")) {
            this.plugin.getMVWorldManager().setFirstSpawnWorld(value);
        }

        sender.sendMessage(String.format("%sSuccess! %sConfig option %s%s %sis now set to %s%s%s.",
                ChatColor.GREEN, ChatColor.WHITE, ChatColor.AQUA, property, ChatColor.WHITE, ChatColor.GREEN, value, ChatColor.WHITE));

        this.plugin.loadConfigs();
    }
}
