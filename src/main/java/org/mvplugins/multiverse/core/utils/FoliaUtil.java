package org.mvplugins.multiverse.core.utils;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public final class FoliaUtil {
    private FoliaUtil() {}

    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /** Wraps a Folia {@link ScheduledTask} so it can be used wherever {@link BukkitTask} is expected. */
    public static class TaskAdapter implements BukkitTask {
        private final ScheduledTask handle;

        public TaskAdapter(ScheduledTask handle) {
            this.handle = handle;
        }

        @Override public int getTaskId() { return -1; }
        @Override public Plugin getOwner() { return handle.getOwningPlugin(); }
        @Override public boolean isSync() { return false; }
        @Override public boolean isCancelled() { return handle.isCancelled(); }
        @Override public void cancel() { handle.cancel(); }
    }
}
