package com.onarandombox.MultiverseCore.listeners;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
        if (event.isCancelled() || !plugin.getMVConfig().getPrefixChat()) {
            return;
        }

        MultiverseWorld mvworld = getPlayerWorld(event.getPlayer());
        if (mvworld == null || mvworld.isHidden()) {
            return;
        }

        String prefixChatFormat = plugin.getMVConfig().getPrefixChatFormat()
                .replace("%world%", mvworld.getColoredWorldString())
                .replace("%chat%", event.getFormat());
        
        prefixChatFormat = ChatColor.translateAlternateColorCodes('&', prefixChatFormat);

        event.setFormat(prefixChatFormat);
    }

    private MultiverseWorld getPlayerWorld(Player player) {
        String world = playerListener.getPlayerWorld().get(player.getName());
        if (world == null) {
            world = player.getWorld().getName();
            playerListener.getPlayerWorld().put(player.getName(), world);
        }
        return this.worldManager.getMVWorld(world);
    }
}
