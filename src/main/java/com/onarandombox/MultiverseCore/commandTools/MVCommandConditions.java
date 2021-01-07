/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.BukkitConditionContext;
import co.aikar.commands.CommandConditions;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commandTools.contexts.PlayerWorld;
import com.onarandombox.MultiverseCore.enums.AddProperties;
import com.onarandombox.MultiverseCore.enums.WorldValidationResult;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Args input validation.
 */
public class MVCommandConditions {

    private final MultiverseCore plugin;
    private final MVWorldManager worldManager;

    public MVCommandConditions(@NotNull MultiverseCore plugin,
                               @NotNull CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> conditions) {

        this.plugin = plugin;
        this.worldManager = plugin.getMVWorldManager();

        conditions.addCondition(String.class, "isMVWorld", this::checkIsMVWorld);
        conditions.addCondition(String.class, "isUnloadedWorld", this::checkIsUnloadedWorld);
        conditions.addCondition(String.class, "isWorldInConfig", this::checkIsWorldInConfig);
        conditions.addCondition(String.class, "creatableWorldName", this::checkCreatableWorldName);
        conditions.addCondition(String.class, "importableWorldName", this::checkImportableWorldName);
        conditions.addCondition(String.class, "validWorldFolder", this::checkValidWorldFolder);
        conditions.addCondition(String.class, "validAddProperty", this::checkValidAddProperty);
        conditions.addCondition(MultiverseWorld.class, "hasWorldAccess", this::checkHasWorldAccess);
        conditions.addCondition(PlayerWorld.class, "selfOtherPerm", this::checkSelfOtherPerm);
    }

    private void checkIsMVWorld(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                @NotNull BukkitCommandExecutionContext executionContext,
                                @NotNull String worldName) {

        if (!this.worldManager.isMVWorld(worldName)) {
            throw new ConditionFailedException(String.format("World '%s' not found.", worldName));
        }
    }

    private void checkIsUnloadedWorld(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                      @NotNull BukkitCommandExecutionContext executionContext,
                                      @NotNull String worldName) {

        if (this.worldManager.isMVWorld(worldName)) {
            throw new ConditionFailedException(String.format("World '%s' is already loaded.", worldName));
        }

        if (!this.worldManager.getUnloadedWorlds().contains(worldName)) {
            if (this.worldManager.isValidWorld(worldName)) {
                CommandSender sender = executionContext.getSender();
                sender.sendMessage(String.format("%sMultiverse does not know about world '%s' yet. Please import it with %s/mv import %s <env>%s.",
                        ChatColor.RED, worldName, ChatColor.AQUA, worldName, ChatColor.RED));
                sender.sendMessage(String.format("See %s/mv help import %sfor more info.",
                        ChatColor.AQUA, ChatColor.WHITE));
                throw new ConditionFailedException();
            }
            throw new ConditionFailedException(String.format("World '%s' not found.", worldName));
        }
    }

    private void checkIsWorldInConfig(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                      @NotNull BukkitCommandExecutionContext executionContext,
                                      @NotNull String worldName) {

        if (!this.worldManager.hasUnloadedWorld(worldName, true)) {
            throw new ConditionFailedException(String.format("World '%s' not found.", worldName));
        }
    }

    private void checkCreatableWorldName(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                         @NotNull BukkitCommandExecutionContext executionContext,
                                         @NotNull String worldName) {

        if (this.worldManager.isMVWorld(worldName)) {
            throw new ConditionFailedException(String.format("%sMultiverse cannot create %sanother %s%sworld named '%s'.",
                    ChatColor.RED, ChatColor.GOLD, ChatColor.RESET, ChatColor.RED, worldName));
        }

        WorldValidationResult validationResult = this.worldManager.validateWorld(worldName);
        validateWorldNameResult(validationResult);

        if (validationResult != WorldValidationResult.DOES_NOT_EXIST && validationResult != WorldValidationResult.NOT_A_DIRECTORY) {
            CommandSender sender = executionContext.getSender();
            if (validationResult == WorldValidationResult.VALID) {
                sender.sendMessage(String.format("%sWorld Folder '%s' already look like a world to me! You can try importing it with %s/mv import%s.",
                        ChatColor.RED, worldName, ChatColor.AQUA, ChatColor.RED));
                sender.sendMessage(String.format("See %s/mv help import %sfor more info.",
                        ChatColor.AQUA, ChatColor.WHITE));
                throw new ConditionFailedException();
            }

            sender.sendMessage(String.format("%sA folder already exists with this name!", ChatColor.RED));
            throw new ConditionFailedException();
        }
    }

    private void checkImportableWorldName(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                          @NotNull BukkitCommandExecutionContext executionContext,
                                          @NotNull String worldName) {

        if (this.worldManager.isMVWorld(worldName)) {
            executionContext.getSender().sendMessage(String.format("%sMultiverse %s already knows about %s%s%s!",
                    ChatColor.GREEN, ChatColor.WHITE, ChatColor.AQUA, worldName, ChatColor.WHITE));
            throw new ConditionFailedException();
        }

        checkValidWorldFolder(context, executionContext, worldName);
    }

    private void checkValidWorldFolder(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                       @NotNull BukkitCommandExecutionContext executionContext,
                                       @NotNull String worldName) {

        WorldValidationResult validationResult = this.worldManager.validateWorld(worldName);
        validateWorldNameResult(validationResult);
        validateWorldFolderResult(validationResult, worldName);
    }

    private void validateWorldNameResult(WorldValidationResult validationResult) {
        switch (validationResult) {
            case NAME_BLACKLISTED:
                throw new ConditionFailedException("World should not be in reserved server folders.");
            case NAME_CONTAINS_DAT:
                throw new ConditionFailedException("Multiverse cannot have a world name that contains '.dat'.");
            case NAME_INVALID:
                throw new ConditionFailedException("World name should not contain spaces or special characters! Please rename your world folder.");
        }
    }

    private void validateWorldFolderResult(@NotNull WorldValidationResult validationResult,
                                           @NotNull String worldName) {
        switch (validationResult) {
            case DOES_NOT_EXIST:
            case NOT_A_DIRECTORY:
                throw new ConditionFailedException(String.format("World folder '%s' does not exist.", worldName));
            case FOLDER_LACK_DAT:
                throw new ConditionFailedException(String.format("'%s' does not appear to be a world! It is lacking .dat file.", worldName));
        }
    }

    private void checkValidAddProperty (@NotNull ConditionContext<BukkitCommandIssuer> context,
                                        @NotNull BukkitCommandExecutionContext executionContext,
                                        @NotNull String property) {

        String actionType = context.getConfig();
        if (actionType.equalsIgnoreCase("set")) {
            return;
        }

        try {
            AddProperties.valueOf(property);
        }
        catch (IllegalArgumentException e) {
            CommandSender sender = executionContext.getSender();
            sender.sendMessage(String.format("%sSorry, you can't use %s with '%s'.",
                    ChatColor.RED, actionType, property));
            sender.sendMessage(String.format("%sPlease visit our Github Wiki for more information: https://goo.gl/q1h01S",
                    ChatColor.AQUA));
            throw new ConditionFailedException();
        }
    }

    private void checkHasWorldAccess(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                     @NotNull BukkitCommandExecutionContext executionContext,
                                     @NotNull MultiverseWorld world) {

        Player player = executionContext.getPlayer();
        if (player == null) {
            return;
        }

        if (!this.plugin.getMVPerms().canEnterWorld(player, world)) {
            throw new ConditionFailedException(String.format("You aren't allowed to access to world '%s%s'!",
                    world.getColoredWorldString(), ChatColor.RED));
        }
    }

    private void checkSelfOtherPerm(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                    @NotNull BukkitCommandExecutionContext executionContext,
                                    @NotNull PlayerWorld player) {

        String permNode = context.getConfig() + (player.isSender(executionContext.getSender()) ? ".self" : ".other");
        if (!executionContext.getSender().hasPermission(permNode)) {
            throw new ConditionFailedException("You do not have permission to run this command.");
        }
    }
}
