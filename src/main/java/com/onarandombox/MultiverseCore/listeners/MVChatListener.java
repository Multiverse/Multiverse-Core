package com.onarandombox.MultiverseCore.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public interface MVChatListener<E extends Event> extends Listener {

    @EventHandler
    public void playerChat(E event);
}
