/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandtools.queue;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.block.data.type.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a single command in {@link CommandQueueManager}.
 */
public class QueuedCommand {

    private static final DummyCommandBlockSender COMMAND_BLOCK = new DummyCommandBlockSender();
    private static final String DEFAULT_PROMPT_MESSAGE = "The command you are trying to run is deemed dangerous.";
    private static final int DEFAULT_VALID_TIME = 200;  // In ticks, 20 ticks = 1s

    /**
     * To allow all CommandBlocks to be a common sender with use of {@link DummyCommandBlockSender}.
     * So confirm command can be used for a queue command on another command block.
     */
    @NotNull
    static CommandSender parseSender(@NotNull CommandSender sender) {
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
    static boolean isCommandBlock(@NotNull CommandSender sender) {
        return sender instanceof BlockCommandSender
                && ((BlockCommandSender) sender).getBlock().getBlockData() instanceof CommandBlock;
    }

    private CommandSender sender;
    private String prompt = DEFAULT_PROMPT_MESSAGE;
    private int validDuration = DEFAULT_VALID_TIME;
    private Runnable action;
    private BukkitTask expireTask;

    private QueuedCommand() { }

    @NotNull
    public CommandSender getSender() {
        return sender;
    }

    @NotNull
    public String getPrompt() {
        return prompt;
    }

    public int getValidDuration() {
        return validDuration;
    }

    @NotNull
    public Runnable getAction() {
        return action;
    }

    @NotNull
    public BukkitTask getExpireTask() {
        return expireTask;
    }

    public void setExpireTask(@NotNull BukkitTask expireTask) {
        this.expireTask = expireTask;
    }

    public static class Builder {

        private final QueuedCommand command;

        @NotNull
        public Builder() {
            this.command = new QueuedCommand();
        }

        @NotNull
        public Builder sender(@NotNull CommandSender sender) {
            this.command.sender = sender;
            return this;
        }

        @NotNull
        public Builder action(@NotNull Runnable action) {
            this.command.action = action;
            return this;
        }

        @NotNull
        public Builder prompt(@NotNull String prompt, Object...replacements) {
            this.command.prompt = String.format(prompt, replacements);
            return this;
        }

        @NotNull
        public Builder validDuration(int validDuration) {
            this.command.validDuration = validDuration;
            return this;
        }

        @NotNull
        public QueuedCommand build() {
            return this.command;
        }
    }
}
