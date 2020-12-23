/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.data.type.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CommandQueueManager {

    private final MultiverseCore plugin;
    private final Map<CommandSender, QueuedCommand> queuedCommands;

    private static final MVCommandBlock COMMAND_BLOCK = new MVCommandBlock();
    private static final String DEFAULT_PROMPT_MESSAGE = "The command you are trying to run is deemed dangerous.";
    private static final int DEFAULT_VALID_TIME = 200;  // In ticks for now

    public CommandQueueManager(MultiverseCore plugin) {
        this.plugin = plugin;
        this.queuedCommands = new HashMap<>();
    }

    public void addToQueue(@NotNull CommandSender sender,
                           @NotNull Runnable runnable) {

        addToQueue(sender, runnable, DEFAULT_PROMPT_MESSAGE, DEFAULT_VALID_TIME);
    }

    public void addToQueue(@NotNull CommandSender sender,
                           @NotNull Runnable runnable,
                           @NotNull String prompt) {

        addToQueue(sender, runnable, prompt, DEFAULT_VALID_TIME);
    }

    public void addToQueue(@NotNull CommandSender sender,
                           @NotNull Runnable runnable,
                           int validPeriod) {

        addToQueue(sender, runnable, DEFAULT_PROMPT_MESSAGE, validPeriod);
    }

    public void addToQueue(@NotNull CommandSender sender,
                           @NotNull Runnable runnable,
                           @NotNull String prompt,
                           int validPeriod) {

        CommandSender targetSender = parseSender(sender);
        cancelPreviousInQueue(targetSender);

        Logging.finer("Adding command to queue for " + sender.getName());
        QueuedCommand queuedCommand = new QueuedCommand(targetSender, runnable);
        queuedCommands.put(targetSender, queuedCommand);
        queuedCommand.setExpireTask(runExpireLater(queuedCommand, validPeriod));

        sender.sendMessage(prompt);
        sender.sendMessage("Run " + ChatColor.GREEN + "/mv confirm" + ChatColor.WHITE + " to continue.");
    }

    private void cancelPreviousInQueue(@NotNull CommandSender sender) {
        QueuedCommand previousCommand = queuedCommands.get(sender);
        if (previousCommand == null) {
            return;
        }

        previousCommand.cancelExpiryTask();
        queuedCommands.remove(sender);
    }

    @NotNull
    private BukkitTask runExpireLater(@NotNull QueuedCommand queuedCommand, int validPeriod) {
        return Bukkit.getScheduler().runTaskLater(
                this.plugin,
                expireRunnable(queuedCommand),
                validPeriod
        );
    }

    @NotNull
    private Runnable expireRunnable(@NotNull QueuedCommand queuedCommand) {
        return () -> {
            QueuedCommand matchingQueuedCommand = this.queuedCommands.get(queuedCommand.getSender());
            if (!queuedCommand.equals(matchingQueuedCommand)) {
                Logging.finer("This is an old command already.");
                return;
            }

            Logging.finer("Command has expired, removing...");
            this.queuedCommands.remove(queuedCommand.getSender());
        };
    }

    public boolean runQueuedCommand(@NotNull CommandSender sender) {
        CommandSender targetSender = parseSender(sender);
        QueuedCommand queuedCommand = this.queuedCommands.get(targetSender);
        if (queuedCommand == null) {
            sender.sendMessage("You do not have any commands in queue.");
            return false;
        }

        Logging.finer("Running queued command...");
        queuedCommand.runCommand();
        queuedCommands.remove(targetSender);
        return true;
    }

    @NotNull
    private CommandSender parseSender(@NotNull CommandSender sender) {
        Logging.fine(sender.getClass().getName());
        if (isCommandBlock(sender)) {
            Logging.finer("Is command block.");
            return COMMAND_BLOCK;
        }
        return sender;
    }

    private boolean isCommandBlock(@NotNull CommandSender sender) {
        return sender instanceof BlockCommandSender && ((BlockCommandSender) sender).getBlock().getBlockData() instanceof CommandBlock;
    }
}
