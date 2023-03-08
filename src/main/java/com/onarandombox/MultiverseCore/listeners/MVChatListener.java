package com.onarandombox.MultiverseCore.listeners;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MVWorld;

import com.onarandombox.MultiverseCore.config.MVCoreConfigProvider;
import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jvnet.hk2.annotations.Service;

/**
 * Multiverse's {@link org.bukkit.event.Listener} for players.
 */
@Service
public class MVChatListener implements Listener {
    private final MVCoreConfigProvider configProvider;
    private final MVWorldManager worldManager;
    private final MVPlayerListener playerListener;

    @Inject
    public MVChatListener(
            MVCoreConfigProvider configProvider,
            MVWorldManager worldManager,
            MVPlayerListener playerListener
    ) {
        this.configProvider = configProvider;
        this.worldManager = worldManager;
        this.playerListener = playerListener;
    }

    /**
     * This handles a {@link AsyncPlayerChatEvent}.
     * @param event The {@link AsyncPlayerChatEvent}.
     */
    @EventHandler
    public void playerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        // Check whether the Server is set to prefix the chat with the World name.
        // If not we do nothing, if so we need to check if the World has an Alias.
        if (configProvider.getConfigUnsafe().getPrefixChat()) {
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
            MVWorld mvworld = this.worldManager.getMVWorld(world);
            if (mvworld.isHidden()) {
                return;
            }
            prefix = mvworld.getColoredWorldString();
            String chat = event.getFormat();
            
            String prefixChatFormat = configProvider.getConfigUnsafe().getPrefixChatFormat();
            prefixChatFormat = prefixChatFormat.replace("%world%", prefix).replace("%chat%", chat);
            prefixChatFormat = ChatColor.translateAlternateColorCodes('&', prefixChatFormat);
            
            event.setFormat(prefixChatFormat);
        }
    }
}
