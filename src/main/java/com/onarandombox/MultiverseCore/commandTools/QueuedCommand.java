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
