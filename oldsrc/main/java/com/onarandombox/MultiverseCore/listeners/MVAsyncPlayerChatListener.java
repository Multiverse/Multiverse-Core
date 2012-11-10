/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.onarandombox.MultiverseCore.MultiverseCore;

/**
 * Multiverse's {@link org.bukkit.event.Listener} for players.
 */
public class MVAsyncPlayerChatListener extends MVChatListener {
    public MVAsyncPlayerChatListener(MultiverseCore plugin, MVPlayerListener playerListener) {
        super(plugin, playerListener);
        plugin.log(Level.FINE, "Created AsyncPlayerChatEvent listener.");
    }

    /**
     * This method is called when a player wants to chat.
     * @param event The Event that was fired.
     */
    @EventHandler
    public void playerChat(AsyncPlayerChatEvent event) {
        this.playerChat(new AsyncChatEvent(event));
    }
}
