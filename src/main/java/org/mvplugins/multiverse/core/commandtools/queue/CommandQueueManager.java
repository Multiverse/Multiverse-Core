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
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.data.type.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.config.MVCoreConfig;

/**
 * <p>Manages the queuing of dangerous commands that require {@code /mv confirm} before executing.</p>
 *
 * <p>Each sender can only have one command in queue at any given time. When a queued command is added
 * for a sender that already has a command in queue, it will replace the old queued command.</p>
 */
@Service
public class CommandQueueManager {

    private static final String CONSOLE_NAME = "@console";
    private static final String COMMAND_BLOCK_NAME = "@commandblock";
    private static final long TICKS_PER_SECOND = 20;

    private final Plugin plugin;
    private final MVCoreConfig config;
    private final Map<String, CommandQueuePayload> queuedCommandMap;

    @Inject
    CommandQueueManager(@NotNull MultiverseCore plugin, @NotNull MVCoreConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.queuedCommandMap = new WeakHashMap<>();
    }

    /**
     * Adds a {@link CommandQueuePayload} into queue.
     *
     * @param payload The command queue payload to add.
     */
    public void addToQueue(CommandQueuePayload payload) {
        String senderName = parseSenderName(payload.issuer());
        if (canRunImmediately(senderName)) {
            payload.action().run();
            return;
        }

        this.removeFromQueue(senderName);

        Logging.finer("Add new command to queue for sender %s.", senderName);
        this.queuedCommandMap.put(senderName, payload);
        payload.expireTask(runExpireLater(senderName, config.getConfirmTimeout()));

        payload.issuer().sendInfo(payload.prompt());
        var confirmCommand = "/mv confirm";
        if (config.getUseConfirmOtp()) {
            confirmCommand += " " + payload.otp();
        }
        payload.issuer().sendMessage(String.format("Run %s%s %sto continue. This will expire in %s seconds.",
                ChatColor.GREEN, confirmCommand, ChatColor.WHITE, config.getConfirmTimeout()));
    }

    /**
     * Check if sender does not need to queue before running.
     *
     * @param senderName    The name of sender.
     * @return True if sender does not need to queue before running.
     */
    private boolean canRunImmediately(@NotNull String senderName) {
        return switch (config.getConfirmMode()) {
            case ENABLE -> false;
            case PLAYER_ONLY -> senderName.equals(CONSOLE_NAME) || senderName.equals(COMMAND_BLOCK_NAME);
            case DISABLE_COMMAND_BLOCKS -> senderName.equals(COMMAND_BLOCK_NAME);
            case DISABLE_CONSOLE -> senderName.equals(CONSOLE_NAME);
            case DISABLE -> true;
        };
    }

    /**
     * Expire task that removes a {@link CommandQueuePayload} from queue after valid duration defined.
     *
     * @param senderName    The name of the sender.
     * @return The expire {@link BukkitTask}.
     */
    @NotNull
    private BukkitTask runExpireLater(@NotNull String senderName, int validDuration) {
        return Bukkit.getScheduler().runTaskLater(
                this.plugin,
                expireRunnable(senderName),
                validDuration * TICKS_PER_SECOND);
    }

    /**
     * Runnable responsible for expiring the queued command.
     *
     * @param senderName    The name of the sender.
     * @return The expire runnable.
     */
    @NotNull
    private Runnable expireRunnable(@NotNull String senderName) {
        return () -> {
            CommandQueuePayload payload = this.queuedCommandMap.remove(senderName);
            if (payload == null) {
                // Payload already removed
                return;
            }
            payload.issuer().sendMessage("Your queued command has expired.");
        };
    }

    /**
     * Runs the command in queue for the given sender, if any.
     *
     * @param issuer    Sender that confirmed the command.
     * @return True if queued command ran successfully, else false.
     */
    public boolean runQueuedCommand(@NotNull MVCommandIssuer issuer, int otp) {
        String senderName = parseSenderName(issuer);
        CommandQueuePayload payload = this.queuedCommandMap.get(senderName);
        if (payload == null) {
            issuer.sendMessage(ChatColor.RED + "You do not have any commands in queue.");
            return false;
        }
        if (config.getUseConfirmOtp() && payload.otp() != otp) {
            issuer.sendMessage(ChatColor.RED + "Invalid OTP number. Please try again...");
            return false;
        }
        Logging.finer("Running queued command...");
        payload.action().run();
        return removeFromQueue(senderName);
    }

    /**
     * Since only one command is stored in queue per sender, we remove the old one.
     *
     * @param senderName    The sender that executed the command.
     * @return True if queue command is removed from sender successfully, else false.
     */
    public boolean removeFromQueue(@NotNull String senderName) {
        CommandQueuePayload payload = this.queuedCommandMap.remove(senderName);
        if (payload == null) {
            Logging.finer("No queue command to remove for sender %s.", senderName);
            return false;
        }
        Option.of(payload.expireTask()).peek(BukkitTask::cancel);
        Logging.finer("Removed queue command for sender %s.", senderName);
        return true;
    }

    private String parseSenderName(MVCommandIssuer issuer) {
        CommandSender sender = issuer.getIssuer();
        if (isCommandBlock(sender)) {
            return COMMAND_BLOCK_NAME;
        } else if (sender instanceof ConsoleCommandSender) {
            return CONSOLE_NAME;
        }
        return sender.getName();
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
