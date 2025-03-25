package org.mvplugins.multiverse.core.command.queue;

@FunctionalInterface
public interface QueuedCommandAction {
    void execute();
}
