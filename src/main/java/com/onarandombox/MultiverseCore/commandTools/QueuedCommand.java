/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

public class QueuedCommand {
    private final CommandSender sender;
    private final Runnable runnable;
    private BukkitTask expireTask;

    public QueuedCommand(CommandSender sender, Runnable runnable) {
        this.sender = sender;
        this.runnable = runnable;
    }

    public void runCommand() {
        runnable.run();
        cancelExpiryTask();
    }

    public void cancelExpiryTask() {
        expireTask.cancel();
    }

    public void setExpireTask(BukkitTask expireTask) {
        this.expireTask = expireTask;
    }

    public CommandSender getSender() {
        return sender;
    }
}
