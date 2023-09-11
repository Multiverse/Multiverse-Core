/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.commandtools.queue;

import co.aikar.commands.BukkitCommandIssuer;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a single command used in {@link CommandQueueManager} for confirming before running potentially
 * dangerous action.
 */
public class QueuedCommand {

    private static final String DEFAULT_PROMPT_MESSAGE = "The command you are trying to run is deemed dangerous.";
    private static final int DEFAULT_VALID_TIME = 10;

    private final CommandSender sender;
    private final Runnable action;
    private final String prompt;
    private final int validDuration;
    private BukkitTask expireTask;

    public QueuedCommand(BukkitCommandIssuer issuer, Runnable action) {
        this(issuer.getIssuer(), action, DEFAULT_PROMPT_MESSAGE, DEFAULT_VALID_TIME);
    }

    public QueuedCommand(BukkitCommandIssuer issuer, Runnable action, String prompt) {
        this(issuer.getIssuer(), action, prompt, DEFAULT_VALID_TIME);
    }

    public QueuedCommand(BukkitCommandIssuer issuer, Runnable action, int validDuration) {
        this(issuer.getIssuer(), action, DEFAULT_PROMPT_MESSAGE, validDuration);
    }

    public QueuedCommand(CommandSender sender, Runnable action) {
        this(sender, action, DEFAULT_PROMPT_MESSAGE, DEFAULT_VALID_TIME);
    }

    public QueuedCommand(CommandSender sender, Runnable action, String prompt) {
        this(sender, action, prompt, DEFAULT_VALID_TIME);
    }

    public QueuedCommand(CommandSender sender, Runnable action, int validDuration) {
        this(sender, action, DEFAULT_PROMPT_MESSAGE, validDuration);
    }

    /**
     * Creates a new queue command, to be registered at {@link CommandQueueManager#addToQueue(QueuedCommand)}.
     *
     * @param sender        The sender that ran the command needed for confirmation.
     * @param action        The logic to execute upon confirming.
     * @param prompt        Question to ask sender to confirm.
     * @param validDuration Duration in which the command is valid for confirm in seconds.
     */
    public QueuedCommand(CommandSender sender, Runnable action, String prompt, int validDuration) {
        this.sender = sender;
        this.action = action;
        this.prompt = prompt;
        this.validDuration = validDuration;
    }

    @NotNull
    CommandSender getSender() {
        return sender;
    }

    @NotNull
    String getPrompt() {
        return prompt;
    }

    int getValidDuration() {
        return validDuration;
    }

    @NotNull
    Runnable getAction() {
        return action;
    }

    @NotNull
    BukkitTask getExpireTask() {
        return expireTask;
    }

    void setExpireTask(@NotNull BukkitTask expireTask) {
        if (this.expireTask != null) {
            throw new IllegalStateException("This queue command already has an expire task. You can't register twice!");
        }
        this.expireTask = expireTask;
    }
}
