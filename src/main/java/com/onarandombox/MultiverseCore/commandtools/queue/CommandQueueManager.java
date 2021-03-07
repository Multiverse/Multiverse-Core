/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandtools.queue;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Queue dangerous commands, with the need to use '/mv confirm' before executing.
 */
public class CommandQueueManager {

    private final MultiverseCore plugin;
    private final Map<CommandSender, QueuedCommand> queuedCommandMap;

    public CommandQueueManager(@NotNull MultiverseCore plugin) {
        this.plugin = plugin;
        this.queuedCommandMap = new WeakHashMap<>();
    }

    public void addToQueue(QueuedCommand queuedCommand) {
        CommandSender sender = queuedCommand.getSender();
        this.cancelPreviousInQueue(sender);
        this.queuedCommandMap.put(sender, queuedCommand);

        queuedCommand.setExpireTask(runExpireLater(queuedCommand));

        sender.sendMessage(queuedCommand.getPrompt());
        sender.sendMessage(String.format("Run %s/mv confirm %sto continue.", ChatColor.GREEN, ChatColor.WHITE));
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
     * @return The expire {@link BukkitTask}.
     */
    @NotNull
    private BukkitTask runExpireLater(@NotNull QueuedCommand queuedCommand) {
        return Bukkit.getScheduler().runTaskLater(
                this.plugin,
                expireRunnable(queuedCommand),
                queuedCommand.getValidDuration()
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
        CommandSender targetSender = QueuedCommand.parseSender(sender);
        QueuedCommand queuedCommand = this.queuedCommandMap.get(targetSender);
        if (queuedCommand == null) {
            sender.sendMessage("You do not have any commands in queue.");
            return false;
        }

        Logging.finer("Running queued command...");
        queuedCommand.getAction().run();
        queuedCommand.getExpireTask().cancel();
        this.queuedCommandMap.remove(targetSender);
        return true;
    }
}
