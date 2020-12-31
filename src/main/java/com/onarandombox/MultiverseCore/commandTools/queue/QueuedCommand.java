/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools.queue;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a single command in {@link CommandQueueManager}.
 */
class QueuedCommand {
    private final CommandSender sender;
    private final Runnable runnable;
    private BukkitTask expireTask;

    /**
     *
     * @param sender    {@link CommandSender} that executed the command.
     * @param runnable  Action to do when the command is ran.
     */
    public QueuedCommand(@NotNull CommandSender sender,
                         @NotNull Runnable runnable) {

        this.sender = sender;
        this.runnable = runnable;
    }

    /**
     * Do the command actions.
     */
    public void runAction() {
        runnable.run();
    }

    public void setExpireTask(@NotNull BukkitTask expireTask) {
        this.expireTask = expireTask;
    }

    @NotNull
    public CommandSender getSender() {
        return sender;
    }

    @NotNull
    public BukkitTask getExpireTask() {
        return expireTask;
    }
}
