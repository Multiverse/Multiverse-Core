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
import com.onarandombox.MultiverseCore.displaytools.ContentDisplay;
import com.onarandombox.MultiverseCore.displaytools.ContentFilter;
import com.onarandombox.MultiverseCore.displaytools.DisplayHandlers;
import com.onarandombox.MultiverseCore.enums.EnglishChatColor;
import com.onarandombox.MultiverseCore.exceptions.PropertyDoesNotExistException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@CommandAlias("mv")
@Subcommand("modify")
//TODO API: Think why properties method for MultiverseWorld is deprecated.
public class ModifyCommand extends MultiverseCoreCommand {

    public ModifyCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("set")
    @CommandPermission("multiverse.core.modify.set")
    @Syntax("<property> <value> [world]")
    @CommandCompletion("@setProperties @empty @MVWorlds")
    @Description("Modify various aspects of worlds by setting a property. For more info; https://tinyurl.com/nehhzp6")
    public void onModifySetCommand(@NotNull CommandSender sender,

                                   @NotNull
                                   @Syntax("<property>")
                                   @Description("Property option key.")
                                   @Flags("type=property")
                                   @Conditions("validAddProperty:set") String property,

                                   @NotNull
                                   @Syntax("<value>")
                                   @Description("New property value.")
                                   @Flags("type=property value") String value,

                                   @NotNull
                                   @Syntax("[world]")
                                   @Description("World that you want property change to apply.")
                                   @Flags("other,defaultself") MultiverseWorld world) {

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
        saveWorldConfig();
    }

    @Subcommand("add")
    @CommandPermission("multiverse.core.modify.add")
    @Syntax("<property> <value> [world]")
    @CommandCompletion("@addProperties @empty @MVWorlds")
    @Description("Modify various aspects of worlds by adding a property. For more info: https://tinyurl.com/nehhzp6")
    public void onModifyAddCommand(@NotNull CommandSender sender,

                                   @NotNull
                                   @Syntax("<property>")
                                   @Description("Property option key.")
                                   @Flags("type=property")
                                   @Conditions("validAddProperty:add") String property,

                                   @NotNull
                                   @Syntax("<value>")
                                   @Description("Property value to add.")
                                   @Flags("type=property value") String value,

                                   @NotNull
                                   @Syntax("[world]")
                                   @Description("World that you want property change to apply.")
                                   @Flags("other,defaultself") MultiverseWorld world) {

        if (!world.addToVariable(property, value)) {
            sender.sendMessage(String.format("%s %scould not be added to %s%s%s.",
                    value, ChatColor.RED, ChatColor.AQUA, property, ChatColor.RED));
            return;
        }

        sender.sendMessage(String.format("%sSuccess! %s%s%s was added to %s%s%s.",
                ChatColor.GREEN, ChatColor.AQUA, value, ChatColor.WHITE, ChatColor.GREEN, property, ChatColor.WHITE));

        saveWorldConfig();
    }

    @Subcommand("remove")
    @CommandPermission("multiverse.core.modify.remove")
    @Syntax("<property> <value> [world]")
    @CommandCompletion("@addProperties @empty @MVWorlds")
    @Description("Modify various aspects of worlds by removing a property. For more info: https://tinyurl.com/nehhzp6")
    public void onModifyRemoveCommand(@NotNull CommandSender sender,

                                      @NotNull
                                      @Syntax("<property>")
                                      @Description("Property option key.")
                                      @Flags("type=property")
                                      @Conditions("validAddProperty:remove") String property,

                                      @NotNull
                                      @Syntax("<value>")
                                      @Description("Property value to remove.")
                                      @Flags("type=property value") String value,

                                      @NotNull
                                      @Syntax("[world]")
                                      @Description("World that you want property change to apply.")
                                      @Flags("other,defaultself") MultiverseWorld world) {

        if (!world.removeFromVariable(property, value)) {
            sender.sendMessage(String.format("%sThere was an error removing %s%s%s from %s%s%s!",
                    ChatColor.RED, ChatColor.GRAY, value, ChatColor.RED, ChatColor.GRAY, property, ChatColor.RED));
            return;
        }

        sender.sendMessage(String.format("%sSuccess! %s%s%s was %sremoved %sfrom %s%s%s.",
                ChatColor.GREEN, ChatColor.AQUA, value, ChatColor.WHITE, ChatColor.RED, ChatColor.WHITE, ChatColor.GREEN, property, ChatColor.WHITE));

        saveWorldConfig();
    }

    @Subcommand("clear")
    @CommandPermission("multiverse.core.modify.clear")
    @Syntax("<property> <value> [world]")
    @CommandCompletion("@addProperties @empty @MVWorlds")
    @Description("Modify various aspects of worlds by clearing a property. For more info: https://tinyurl.com/nehhzp6")
    public void onModifyClearCommand(@NotNull CommandSender sender,

                                     @NotNull
                                     @Syntax("<property>")
                                     @Description("Property option key.")
                                     @Flags("type=property") @Conditions("validAddProperty:clear") String property,

                                     @NotNull
                                     @Syntax("[world]")
                                     @Description("World that you want property be cleared.")
                                     @Flags("other,defaultself") MultiverseWorld world) {

        if (!world.clearList(property)) {
            sender.sendMessage(String.format("%sThere was an error clearing %s%s%s.",
                    ChatColor.RED, ChatColor.GOLD, property, ChatColor.RED));
        }

        sender.sendMessage(String.format("%s%s%s was cleared. It contains 0 values now.",
                ChatColor.GOLD, property, ChatColor.WHITE));

        saveWorldConfig();
    }

    @Subcommand("list")
    @CommandPermission("multiverse.core.modify.list")
    @Syntax("[world] [filter]")
    @CommandCompletion("@MVWorlds")
    @Description("Show properties available to set.")
    public void onModifyListCommand(@NotNull CommandSender sender,

                                     @NotNull
                                     @Syntax("[world]")
                                     @Description("World that you want to see current property values set.")
                                     @Flags("other,defaultself,fallbackself") MultiverseWorld world,

                                     @NotNull ContentFilter filter) {

        new ContentDisplay.Builder<Map<String, Object>>()
                .sender(sender)
                .header("%s===[ Property Values for %s%s ]===", ChatColor.GOLD, world.getColoredWorldString(), ChatColor.GOLD)
                .contents(generateModifyList(world))
                .emptyMessage("No properties found.")
                .displayHandler(DisplayHandlers.INLINE_MAP)
                .filter(filter)
                .display();
    }

    private Map<String, Object> generateModifyList(MultiverseWorld world) {
        Collection<String> properties = world.getAllPropertyTypes();
        Map<String, Object> propMap = new HashMap<>(properties.size());
        for (String property : properties) {
            String value;
            try {
                value = world.getPropertyValue(property);
            } catch (PropertyDoesNotExistException ignored) {
                value = String.format("%s!!INAVLID!!", ChatColor.RED);
            }
            propMap.put(property, value);
        }
        return propMap;
    }

    private void saveWorldConfig() {
        if (!this.plugin.saveWorldConfig()) {
            throw new InvalidCommandArgument("There was an issue saving worlds.yml! Your changes will only be temporary.", false);
        }
    }
}
