/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.commandtools.queue;

import java.util.Map;
import java.util.WeakHashMap;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.data.type.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;

/**
 * <p>Manages the queuing of dangerous commands that require {@code /mv confirm} before executing.</p>
 *
 * <p>Each sender can only have one command in queue at any given time. When a queued command is added
 * for a sender that already has a command in queue, it will replace the old queued command.</p>
 */
@Service
public class CommandQueueManager {

    private static final long TICKS_PER_SECOND = 20;
    private static final DummyCommandBlockSender COMMAND_BLOCK = new DummyCommandBlockSender();

    private final Plugin plugin;
    private final Map<CommandSender, QueuedCommand> queuedCommandMap;

    @Inject
    public CommandQueueManager(@NotNull MultiverseCore plugin) {
        this.plugin = plugin;
        this.queuedCommandMap = new WeakHashMap<>();
    }

    /**
     * Adds a {@link QueuedCommand} into queue.
     *
     * @param queuedCommand The queued command to add.
     */
    public void addToQueue(QueuedCommand queuedCommand) {
        CommandSender targetSender = parseSender(queuedCommand.getSender());

        // Since only one command is stored in queue per sender, we remove the old one.
        this.removeFromQueue(targetSender);

        Logging.finer("Add new command to queue for sender %s.", targetSender.getName());
        this.queuedCommandMap.put(targetSender, queuedCommand);
        queuedCommand.setExpireTask(runExpireLater(queuedCommand));

        queuedCommand.getSender().sendMessage(queuedCommand.getPrompt());
        queuedCommand.getSender().sendMessage(String.format("Run %s/mv confirm %sto continue. This will expire in %s seconds.",
                ChatColor.GREEN, ChatColor.WHITE, queuedCommand.getValidDuration()));
    }

    /**
     * Expire task that removes a {@link QueuedCommand} from queue after valid duration defined.
     *
     * @param queuedCommand Command to run the expire task on.
     * @return The expire {@link BukkitTask}.
     */
    @NotNull
    private BukkitTask runExpireLater(@NotNull QueuedCommand queuedCommand) {
        return Bukkit.getScheduler().runTaskLater(
                this.plugin,
                expireRunnable(queuedCommand),
                queuedCommand.getValidDuration() * TICKS_PER_SECOND
        );
    }

    /**
     * Runnable responsible for expiring the queued command.
     *
     * @param queuedCommand Command to create the expire task on.
     * @return The expire runnable.
     */
    @NotNull
    private Runnable expireRunnable(@NotNull QueuedCommand queuedCommand) {
        return () -> {
            CommandSender targetSender = parseSender(queuedCommand.getSender());
            QueuedCommand matchingQueuedCommand = this.queuedCommandMap.get(targetSender);
            if (!queuedCommand.equals(matchingQueuedCommand) || queuedCommand.getExpireTask().isCancelled()) {
                // To be safe, but this shouldn't happen since we cancel old commands before add new once.
                Logging.finer("This is an old queue command already.");
                return;
            }
            queuedCommand.getSender().sendMessage("Your queued command has expired.");
            this.queuedCommandMap.remove(queuedCommand.getSender());
        };
    }

    /**
     * Runs the command in queue for the given sender, if any.
     *
     * @param sender    {@link CommandSender} that confirmed the command.
     * @return True if queued command ran successfully, else false.
     */
    public boolean runQueuedCommand(@NotNull CommandSender sender) {
        CommandSender targetSender = parseSender(sender);
        QueuedCommand queuedCommand = this.queuedCommandMap.get(targetSender);
        if (queuedCommand == null) {
            sender.sendMessage(ChatColor.RED + "You do not have any commands in queue.");
            return false;
        }
        Logging.finer("Running queued command...");
        queuedCommand.getAction().run();
        return removeFromQueue(targetSender);
    }

    /**
     * Since only one command is stored in queue per sender, we remove the old one.
     *
     * @param sender    The {@link CommandSender} that executed the command.
     * @return True if queue command is removed from sender successfully, else false.
     */
    public boolean removeFromQueue(@NotNull CommandSender sender) {
        CommandSender targetSender = parseSender(sender);
        QueuedCommand previousCommand = this.queuedCommandMap.remove(targetSender);
        if (previousCommand == null) {
            Logging.finer("No queue command to remove for sender %s.", targetSender.getName());
            return false;
        }
        previousCommand.getExpireTask().cancel();
        Logging.finer("Removed queue command for sender %s.", targetSender.getName());
        return true;
    }

    /**
     * To allow all CommandBlocks to be a common sender with use of {@link DummyCommandBlockSender}.
     * So confirm command can be used for a queued command on another command block.
     *
     * @param sender    The sender to parse.
     * @return The sender, or if its a command block, a {@link DummyCommandBlockSender}.
     */
    @NotNull
    private CommandSender parseSender(@NotNull CommandSender sender) {
        Logging.finer("Sender is of class %s.", sender.getClass().getName());
        if (isCommandBlock(sender)) {
            Logging.finer("Is command block.");
            return COMMAND_BLOCK;
        }
        return sender;
    }

    /**
     * Checks if the sender is a command block.
     *
     * @param sender    The sender to check.
     * @return True if sender is a command block, else false.
     */
    private boolean isCommandBlock(@NotNull CommandSender sender) {
        return sender instanceof BlockCommandSender
                && ((BlockCommandSender) sender).getBlock().getBlockData() instanceof CommandBlock;
    }
}
