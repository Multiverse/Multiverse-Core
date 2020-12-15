package com.onarandombox.MultiverseCore.commands_helper;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

public class QueuedCommand {
    private final CommandSender sender;
    private final Runnable runnable;
    private final int validInterval;
    private BukkitTask expireTask;

    public QueuedCommand(CommandSender sender, Runnable runnable, int validPeriod) {
        this.sender = sender;
        this.runnable = runnable;
        this.validInterval = validPeriod;
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

    public int getValidInterval() {
        return validInterval;
    }
}
