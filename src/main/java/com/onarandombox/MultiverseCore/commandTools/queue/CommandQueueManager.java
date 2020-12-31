/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools.queue;

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

/**
 * Queue dangerous commands, with the need to use '/mv confirm' before executing.
 */
public class CommandQueueManager {

    private final MultiverseCore plugin;
    private final Map<CommandSender, QueuedCommand> queuedCommandMap;

    private static final DummyCommandBlockSender COMMAND_BLOCK = new DummyCommandBlockSender();
    private static final String DEFAULT_PROMPT_MESSAGE = "The command you are trying to run is deemed dangerous.";
    private static final int DEFAULT_VALID_TIME = 200;  // In ticks, 20 ticks = 1s

    public CommandQueueManager(MultiverseCore plugin) {
        this.plugin = plugin;
        this.queuedCommandMap = new HashMap<>();
    }

    /**
     * Add a command to queue.
     *
     * @param sender    {@link CommandSender} that executed the command.
     * @param runnable  Action to do when the command is ran.
     */
    public void addToQueue(@NotNull CommandSender sender,
                           @NotNull Runnable runnable) {

        addToQueue(sender, runnable, DEFAULT_PROMPT_MESSAGE, DEFAULT_VALID_TIME);
    }

    /**
     * Add a command to queue.
     *
     * @param sender    {@link CommandSender} that executed the command.
     * @param runnable  Action to do when the command is ran.
     * @param prompt    Reason for needing to confirm the action before running.
     */
    public void addToQueue(@NotNull CommandSender sender,
                           @NotNull Runnable runnable,
                           @NotNull String prompt) {

        addToQueue(sender, runnable, prompt, DEFAULT_VALID_TIME);
    }

    /**
     * Add a command to queue.
     *
     * @param sender       {@link CommandSender} that executed the command.
     * @param runnable     Action to do when the command is ran.
     * @param validPeriod  How long before the command expires and is removed from queue without running.
     */
    public void addToQueue(@NotNull CommandSender sender,
                           @NotNull Runnable runnable,
                           int validPeriod) {

        addToQueue(sender, runnable, DEFAULT_PROMPT_MESSAGE, validPeriod);
    }

    /**
     * Add a command to queue.
     *
     * @param sender       {@link CommandSender} that executed the command.
     * @param runnable     Action to do when the command is ran.
     * @param prompt       Reason for needing to confirm the action before running.
     * @param validPeriod  How long before the command expires and is removed from queue without running.
     */
    public void addToQueue(@NotNull CommandSender sender,
                           @NotNull Runnable runnable,
                           @NotNull String prompt,
                           int validPeriod) {

        CommandSender targetSender = parseSender(sender);
        cancelPreviousInQueue(targetSender);

        Logging.finer("Adding command to queue for " + sender.getName());
        QueuedCommand queuedCommand = new QueuedCommand(targetSender, runnable);
        queuedCommand.setExpireTask(runExpireLater(queuedCommand, validPeriod));
        this.queuedCommandMap.put(targetSender, queuedCommand);

        sender.sendMessage(prompt);
        sender.sendMessage("Run " + ChatColor.GREEN + "/mv confirm" + ChatColor.WHITE + " to continue.");
    }

    /**
     * Since only one command is stored in queue per sender, we remove the old one.
     *
     * @param sender {@link CommandSender} that executed the command.
     */
    private void cancelPreviousInQueue(@NotNull CommandSender sender) {
        QueuedCommand previousCommand = queuedCommandMap.get(sender);
        if (previousCommand == null) {
            return;
        }

        previousCommand.getExpireTask().cancel();
        this.queuedCommandMap.remove(sender);
    }

    /**
     * Expire task that remove {@link QueuedCommand} from queue after expiry.
     *
     * @param queuedCommand  Command to run the expire task on.
     * @param validPeriod    How long before the command expires and is removed from queue without running.
     * @return The expire {@link BukkitTask}.
     */
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
            QueuedCommand matchingQueuedCommand = this.queuedCommandMap.get(queuedCommand.getSender());
            if (!queuedCommand.equals(matchingQueuedCommand)) {
                Logging.finer("This is an old command already.");
                return;
            }

            Logging.finer("Command has expired, removing...");
            this.queuedCommandMap.remove(queuedCommand.getSender());
        };
    }

    /**
     * Runs the command in queue for the given sender, if any.
     *
     * @param sender {@link CommandSender} that confirmed the command.
     * @return True of queued command ran successfully, false otherwise.
     */
    public boolean runQueuedCommand(@NotNull CommandSender sender) {
        CommandSender targetSender = parseSender(sender);
        QueuedCommand queuedCommand = this.queuedCommandMap.get(targetSender);
        if (queuedCommand == null) {
            sender.sendMessage("You do not have any commands in queue.");
            return false;
        }

        Logging.finer("Running queued command...");
        queuedCommand.runAction();
        queuedCommand.getExpireTask().cancel();
        this.queuedCommandMap.remove(targetSender);
        return true;
    }

    /**
     * To allow all CommandBlocks to be a common sender with use of {@link DummyCommandBlockSender}.
     * So confirm command can be used for a queue command on another command block.
     */
    @NotNull
    private CommandSender parseSender(@NotNull CommandSender sender) {
        Logging.fine(sender.getClass().getName());
        if (isCommandBlock(sender)) {
            Logging.finer("Is command block.");
            return COMMAND_BLOCK;
        }
        return sender;
    }

    /**
     * Check if sender is a command block.
     */
    private boolean isCommandBlock(@NotNull CommandSender sender) {
        return sender instanceof BlockCommandSender && ((BlockCommandSender) sender).getBlock().getBlockData() instanceof CommandBlock;
    }
}
