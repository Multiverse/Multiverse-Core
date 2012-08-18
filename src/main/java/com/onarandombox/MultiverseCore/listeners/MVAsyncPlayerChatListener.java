/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.logging.Level;

/**
 * Multiverse's {@link org.bukkit.event.Listener} for players.
 */
public class MVAsyncPlayerChatListener implements MVChatListener<AsyncPlayerChatEvent> {

    private final MultiverseCore plugin;
    private final MVWorldManager worldManager;
    private final MVPlayerListener playerListener;

    public MVAsyncPlayerChatListener(MultiverseCore plugin, MVPlayerListener playerListener) {
        this.plugin = plugin;
        this.worldManager = plugin.getMVWorldManager();
        this.playerListener = playerListener;
        plugin.log(Level.FINE, "Registered AsyncPlayerChatEvent listener.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @EventHandler
    public void playerChat(AsyncPlayerChatEvent event) {
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
