package com.onarandombox.MultiverseCore.listeners;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

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
            String world = playerListener.getPlayerWorld().get(event.getPlayer().getName());
            if (world == null) {
                world = event.getPlayer().getWorld().getName();
                playerListener.getPlayerWorld().put(event.getPlayer().getName(), world);
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
            String chat = event.getFormat();
            
            String prefixChatFormat = plugin.getMVConfig().getPrefixChatFormat();
            prefixChatFormat = prefixChatFormat.replace("%world%", prefix).replace("%chat%", chat);
            prefixChatFormat = ChatColor.translateAlternateColorCodes('&', prefixChatFormat);
            
            event.setFormat(prefixChatFormat);
        }
    }
}
