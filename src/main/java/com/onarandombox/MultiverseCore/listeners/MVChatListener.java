package com.onarandombox.MultiverseCore.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * This interface is implemented by {@link MVPlayerChatListener} and {@link MVAsyncPlayerChatListener}.
 * @param <E> The chat event-type.
 */
public interface MVChatListener<E extends Event> extends Listener {
    /**
     * This method is called when a player wants to chat.
     * @param event The Event that was fired.
     */
    @EventHandler
    void playerChat(E event);
}
