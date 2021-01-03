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
import com.onarandombox.MultiverseCore.commandTools.display.ColorAlternator;
import com.onarandombox.MultiverseCore.commandTools.display.ContentCreator;
import com.onarandombox.MultiverseCore.commandTools.display.ContentFilter;
import com.onarandombox.MultiverseCore.commandTools.display.inline.KeyValueDisplay;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@CommandAlias("mv")
@Subcommand("config")
@CommandPermission("multiverse.core.config")
public class ConfigCommand extends MultiverseCommand {

    public ConfigCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("list")
    @Syntax("[filter]")
    @Description("Show multiverse config values.")
    public void onShowCommand(@NotNull CommandSender sender,
                              @NotNull ContentFilter filter) {

        new KeyValueDisplay().withSender(sender)
                .withHeader(ChatColor.LIGHT_PURPLE + "===[ Multiverse Config ]===")
                .withCreator(getConfigMap())
                .withFilter(filter)
                .withColors(new ColorAlternator(ChatColor.GREEN, ChatColor.GOLD))
                .build()
                .runTaskAsynchronously(this.plugin);
    }

    private ContentCreator<Map<String, Object>> getConfigMap() {
        return () -> this.plugin.getMVConfig().serialize();
    }

    @Subcommand("set")
    @Syntax("<property> <value>")
    @CommandCompletion("@MVConfigs")
    @Description("Set Global MV Variables.")
    public void onSetCommand(@NotNull CommandSender sender,

                             @Syntax("<property>")
                             @Description("Config option.")
                             @NotNull @Values("@MVConfigs") String property,

                             @Syntax("<value>")
                             @Description("New value for the given config option.")
                             @NotNull @Single String value) {

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
