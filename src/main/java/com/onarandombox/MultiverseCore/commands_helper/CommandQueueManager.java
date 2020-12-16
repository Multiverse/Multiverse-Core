package com.onarandombox.MultiverseCore.commands_helper;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CommandQueueManager {

    private final MultiverseCore plugin;
    private final Map<CommandSender, QueuedCommand> queuedCommands;

    private static final int DEFAULT_VALID_TIME = 200;  // In ticks for now

    public CommandQueueManager(MultiverseCore plugin) {
        this.plugin = plugin;
        this.queuedCommands = new HashMap<>();
    }

    public void addToQueue(@NotNull CommandSender sender,
                           @NotNull Runnable runnable) {

        addToQueue(sender, runnable, DEFAULT_VALID_TIME);
    }

    public void addToQueue(@NotNull CommandSender sender,
                           @NotNull Runnable runnable,
                           int validPeriod) {

        cancelPreviousInQueue(sender);

        QueuedCommand queuedCommand = new QueuedCommand(sender, runnable, validPeriod);
        queuedCommands.put(sender, queuedCommand);
        queuedCommand.setExpireTask(runExpireLater(queuedCommand));

        sender.sendMessage("The command you are trying to run is deemed dangerous.");
        sender.sendMessage("Run /mv confirm to continue.");
    }

    private void cancelPreviousInQueue(@NotNull CommandSender sender) {
        QueuedCommand previousCommand = queuedCommands.get(sender);
        if (previousCommand == null) {
            return;
        }
        previousCommand.cancelExpiryTask();
        queuedCommands.remove(sender);
    }

    private BukkitTask runExpireLater(@NotNull QueuedCommand queuedCommand) {
        return Bukkit.getScheduler().runTaskLater(
                this.plugin,
                expireRunnable(queuedCommand),
                queuedCommand.getValidInterval()
        );
    }

    private Runnable expireRunnable(@NotNull QueuedCommand queuedCommand) {
        return () -> {
            QueuedCommand matchingQueuedCommand = this.queuedCommands.get(queuedCommand.getSender());
            if (!queuedCommand.equals(matchingQueuedCommand)) {
                Logging.finer("This is an old command already.");
                return;
            }
            this.queuedCommands.remove(queuedCommand.getSender());
            Logging.finer("Command expired and is removed.");
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
