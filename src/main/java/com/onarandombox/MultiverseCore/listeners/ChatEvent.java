package com.onarandombox.MultiverseCore.listeners;

import org.bukkit.entity.Player;

/**
 * A wrapper for the two chat-events in Bukkit.
 */
public interface ChatEvent {
    /**
     * @return Whether this event is cancelled.
     */
    boolean isCancelled();

    /**
     * @return The format.
     */
    String getFormat();

    /**
     * Sets the format.
     * @param s The new format.
     */
    void setFormat(String s);

    /**
     * @return The player.
     */
    Player getPlayer();
}
