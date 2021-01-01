/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.enums.AddProperties;
import com.onarandombox.MultiverseCore.enums.EnglishChatColor;
import com.onarandombox.MultiverseCore.exceptions.PropertyDoesNotExistException;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@CommandAlias("mv")
@Subcommand("modify")
public class ModifyCommand extends MultiverseCommand {

    public ModifyCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("set")
    @CommandPermission("multiverse.core.modify.set")
    @Syntax("<property> <value> [world]")
    @CommandCompletion("@setProperties @empty @MVWorlds")
    @Description("Modify various aspects of worlds by setting a property. For more info; https://tinyurl.com/nehhzp6")
    public void onModifySetCommand(@NotNull CommandSender sender,

                                   @Syntax("<property>")
                                   @Description("Property option key.")
                                   @NotNull @Flags("type=property") @Conditions("validAddProperty:set") String property,

                                   @Syntax("<value>")
                                   @Description("New property value.")
                                   @NotNull @Flags("type=property value") String value,

                                   @Syntax("[world]")
                                   @Description("World that you want property change to apply.")
                                   @NotNull @Flags("other,defaultself") MultiverseWorld world) {

        doModifySet(sender, property, value, world);
    }

    @Subcommand("add")
    @CommandPermission("multiverse.core.modify.add")
    @Syntax("<property> <value> [world]")
    @CommandCompletion("@addProperties @empty @MVWorlds")
    @Description("Modify various aspects of worlds by adding a property. For more info: https://tinyurl.com/nehhzp6")
    public void onModifyAddCommand(@NotNull CommandSender sender,

                                   @Syntax("<property>")
                                   @Description("Property option key.")
                                   @NotNull @Flags("type=property") @Conditions("validAddProperty:add") String property,

                                   @Syntax("<value>")
                                   @Description("Property value to add.")
                                   @NotNull @Flags("type=property value") String value,

                                   @Syntax("[world]")
                                   @Description("World that you want property change to apply.")
                                   @NotNull @Flags("other,defaultself") MultiverseWorld world) {

        doModifyAdd(sender, property, value, world);
    }

    @Subcommand("remove")
    @CommandPermission("multiverse.core.modify.remove")
    @Syntax("<property> <value> [world]")
    @CommandCompletion("@addProperties @empty @MVWorlds")
    @Description("Modify various aspects of worlds by removing a property. For more info: https://tinyurl.com/nehhzp6")
    public void onModifyRemoveCommand(@NotNull CommandSender sender,

                                      @Syntax("<property>")
                                      @Description("Property option key.")
                                      @NotNull @Flags("type=property") @Conditions("validAddProperty:remove") String property,

                                      @Syntax("<value>")
                                      @Description("Property value to remove.")
                                      @NotNull @Flags("type=property value") String value,

                                      @Syntax("[world]")
                                      @Description("World that you want property change to apply.")
                                      @NotNull @Flags("other,defaultself") MultiverseWorld world) {

        doModifyRemove(sender, property, value, world);
    }

    @Subcommand("clear")
    @CommandPermission("multiverse.core.modify.clear")
    @Syntax("<property> <value> [world]")
    @CommandCompletion("@addProperties @empty @MVWorlds")
    @Description("Modify various aspects of worlds by clearing a property. For more info: https://tinyurl.com/nehhzp6")
    public void onModifyClearCommand(@NotNull CommandSender sender,

                                     @Syntax("<property>")
                                     @Description("Property option key.")
                                     @NotNull @Flags("type=property") @Conditions("validAddProperty:clear") String property,

                                     @Syntax("[world]")
                                     @Description("World that you want property be cleared.")
                                     @NotNull @Flags("other,defaultself") MultiverseWorld world) {

        doModifyClear(sender, property, world);
    }

    @Subcommand("list")
    @CommandPermission("multiverse.core.modify.list")
    @Syntax("[world]")
    @CommandCompletion("@MVWorlds")
    @Description("Show properties available to set.")
    public void onModifyClearCommand(@NotNull CommandSender sender,

                                     @Syntax("[world]")
                                     @Description("World that you want to see current property values set.")
                                     @NotNull @Flags("other,defaultself") MultiverseWorld world) {

        doModifyList(sender, world);
    }

    //TODO API: Think why properties method for MultiverseWorld is deprecated.
    private void doModifySet(@NotNull CommandSender sender,
                             @NotNull String property,
                             @NotNull String value,
                             @NotNull MultiverseWorld world) {

        if ((property.equalsIgnoreCase("aliascolor")
                || property.equalsIgnoreCase("color"))
                && !EnglishChatColor.isValidAliasColor(value)) {
            sender.sendMessage(String.format("%s'%s' is not a valid color. Please pick one of the following:",
                    ChatColor.RED, value));
            sender.sendMessage(EnglishChatColor.getAllColors());
            return;
        }

        try {
            if (!world.setPropertyValue(property, value)) {
                sender.sendMessage(ChatColor.RED + world.getPropertyHelp(property));
                return;
            }
        }
        catch (PropertyDoesNotExistException e) {
            sender.sendMessage(String.format("%sSorry, You can't set '%s%s%s'!", ChatColor.RED, ChatColor.GRAY, property, ChatColor.RED));
            sender.sendMessage(String.format("Valid world-properties: %s", world.getAllPropertyNames()));
            return;
        }

        sender.sendMessage(String.format("%sSuccess! %sProperty %s%s %swas set to %s%s%s.",
                ChatColor.GREEN, ChatColor.WHITE, ChatColor.AQUA, property, ChatColor.WHITE, ChatColor.GREEN, value, ChatColor.WHITE));
        saveWorldConfig(sender);
    }

    private void doModifyAdd(@NotNull CommandSender sender,
                             @NotNull String property,
                             @NotNull String value,
                             @NotNull MultiverseWorld world) {

        if (!world.addToVariable(property, value)) {
            sender.sendMessage(String.format("%s %scould not be added to %s%s%s.",
                    value, ChatColor.RED, ChatColor.AQUA, property, ChatColor.RED));
            return;
        }

        sender.sendMessage(String.format("%sSuccess! %s%s%s was added to %s%s%s.",
                ChatColor.GREEN, ChatColor.AQUA, value, ChatColor.WHITE, ChatColor.GREEN, property, ChatColor.WHITE));

        saveWorldConfig(sender);
    }

    private void doModifyRemove(@NotNull CommandSender sender,
                                @NotNull String property,
                                @NotNull String value,
                                @NotNull MultiverseWorld world) {

        if (!world.removeFromVariable(property, value)) {
            sender.sendMessage(String.format("%sThere was an error removing %s%s%s from %s%s%s!",
                    ChatColor.RED, ChatColor.GRAY, value, ChatColor.RED, ChatColor.GRAY, property, ChatColor.RED));
            return;
        }

        sender.sendMessage(String.format("%sSuccess! %s%s%s was %sremoved %sfrom %s%s%s.",
                ChatColor.GREEN, ChatColor.AQUA, value, ChatColor.WHITE, ChatColor.RED, ChatColor.WHITE, ChatColor.GREEN, property, ChatColor.WHITE));

        saveWorldConfig(sender);
    }

    private void doModifyClear(@NotNull CommandSender sender,
                               @NotNull String property,
                               @NotNull MultiverseWorld world) {

        if (!world.clearList(property)) {
            sender.sendMessage(String.format("%sThere was an error clearing %s%s%s.",
                    ChatColor.RED, ChatColor.GOLD, property, ChatColor.RED));
        }

        sender.sendMessage(String.format("%s%s%s was cleared. It contains 0 values now.",
                ChatColor.GOLD, property, ChatColor.WHITE));

        saveWorldConfig(sender);
    }

    private void doModifyList(@NotNull CommandSender sender,
                              @NotNull MultiverseWorld world) {

        //TODO ACF: Use KayValueDisplay
        Collection<String> properties = world.getAllPropertyTypes();
        List<String> propValues = new ArrayList<>(properties.size());

        for (String property : properties) {
            String value;
            try {
                value = world.getPropertyValue(property);
            }
            catch (PropertyDoesNotExistException ignored) {
                value = String.format("%s!!INAVLID!!", ChatColor.RED);
            }

            propValues.add(ChatColor.GREEN + property
                    + ChatColor.WHITE + " = "
                    + ChatColor.GOLD + value
                    + ChatColor.WHITE);
        }

        sender.sendMessage(String.format("%s===[ Property Values for %s ]===", ChatColor.GOLD, world.getColoredWorldString()));
        sender.sendMessage(String.join(", ", propValues));
    }

    private void saveWorldConfig(@NotNull CommandSender sender) {
        if (!this.plugin.saveWorldConfig()) {
            throw new InvalidCommandArgument("There was an issue saving worlds.yml! Your changes will only be temporary.", false);
        }
    }
}
