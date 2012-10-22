package com.onarandombox.MultiverseCore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * A wrapper for the {@link AsyncPlayerChatEvent}.
 */
public class AsyncChatEvent implements ChatEvent {
    private final AsyncPlayerChatEvent event;

    public AsyncChatEvent(AsyncPlayerChatEvent event) {
        this.event = event;
    }

    @Override
    public boolean isCancelled() {
        return event.isCancelled();
    }

    @Override
    public String getFormat() {
        return event.getFormat();
    }

    @Override
    public void setFormat(String s) {
        event.setFormat(s);
    }

    @Override
    public Player getPlayer() {
        return event.getPlayer();
    }
}
