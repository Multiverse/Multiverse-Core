package com.onarandombox.MultiverseCore.listeners;

import java.util.logging.Level;

import org.bukkit.event.Listener;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

/**
 * Multiverse's {@link org.bukkit.event.Listener} for players.
 */
public abstract class MVChatListener implements Listener {
    private final MultiverseCore plugin;
    private final MVWorldManager worldManager;
    private final MVPlayerListener playerListener;

    public MVChatListener(MultiverseCore plugin, MVPlayerListener playerListener) {
        this.plugin = plugin;
        this.worldManager = plugin.getMVWorldManager();
        this.playerListener = playerListener;
    }

    /**
     * This handles a {@link ChatEvent}.
     * @param event The {@link ChatEvent}.
     */
    public void playerChat(ChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        // Check whether the Server is set to prefix the chat with the World name.
        // If not we do nothing, if so we need to check if the World has an Alias.
        if (plugin.getMVConfig().getPrefixChat()) {
            String world;
            Thread thread = Thread.currentThread();
            if (playerListener.getWorldsLock().isLocked()) {
                plugin.log(Level.FINEST, "worldsLock is locked when attempting to handle player chat on thread: " + thread);
            }
            playerListener.getWorldsLock().lock();
            try {
                plugin.log(Level.FINEST, "Handling player chat on thread: " + thread);
                world = playerListener.getPlayerWorld().get(event.getPlayer().getName());
                if (world == null) {
                    world = event.getPlayer().getWorld().getName();
                    playerListener.getPlayerWorld().put(event.getPlayer().getName(), world);
                }
            } finally {
                playerListener.getWorldsLock().unlock();
            }
            String prefix = "";
            // If we're not a MV world, don't do anything
            if (!this.worldManager.isMVWorld(world)) {
                return;
            }
            MultiverseWorld mvworld = this.worldManager.getMVWorld(world);
            if (mvworld.isHidden()) {
                return;
            }
            prefix = mvworld.getColoredWorldString();
            String format = event.getFormat();
            event.setFormat("[" + prefix + "]" + format);
        }
    }
}
