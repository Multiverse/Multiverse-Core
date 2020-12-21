package com.onarandombox.MultiverseCore.commandTools;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CommandQueueManager {

    private final MultiverseCore plugin;
    private final Map<CommandSender, QueuedCommand> queuedCommands;

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

        cancelPreviousInQueue(sender);

        QueuedCommand queuedCommand = new QueuedCommand(sender, runnable);
        queuedCommands.put(sender, queuedCommand);
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

    private BukkitTask runExpireLater(@NotNull QueuedCommand queuedCommand, int validPeriod) {
        return Bukkit.getScheduler().runTaskLater(
                this.plugin,
                expireRunnable(queuedCommand),
                validPeriod
        );
    }

    private Runnable expireRunnable(@NotNull QueuedCommand queuedCommand) {
        return () -> {
            QueuedCommand matchingQueuedCommand = this.queuedCommands.get(queuedCommand.getSender());
            if (!queuedCommand.equals(matchingQueuedCommand)) {
                Logging.finer("This is an old command already.");
                return;
            }

            Logging.finer("Command is expired, removing...");
            this.queuedCommands.remove(queuedCommand.getSender());
        };
    }

    public boolean runQueuedCommand(@NotNull CommandSender sender) {
        QueuedCommand queuedCommand = this.queuedCommands.get(sender);
        if (queuedCommand == null) {
            sender.sendMessage("You do not have any commands in queue.");
            return false;
        }

        Logging.fine("Running queued command.");
        queuedCommand.runCommand();
        queuedCommands.remove(sender);
        return true;
    }
}
