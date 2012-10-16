package com.onarandombox.MultiverseCore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

/**
 * A wrapper for the {@link PlayerChatEvent}.
 * @deprecated This is deprecated like the {@link PlayerChatEvent}.
 */
@Deprecated
public class NormalChatEvent implements ChatEvent {
    private final PlayerChatEvent event;

    public NormalChatEvent(PlayerChatEvent event) {
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
