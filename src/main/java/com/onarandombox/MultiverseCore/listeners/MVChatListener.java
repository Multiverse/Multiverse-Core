package com.onarandombox.MultiverseCore.listeners;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MVWorld;

import com.onarandombox.MultiverseCore.config.MVCoreConfig;
import com.onarandombox.MultiverseCore.inject.InjectableListener;
import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jvnet.hk2.annotations.Service;

/**
 * Multiverse's Listener for players.
 */
@Service
public class MVChatListener implements InjectableListener {
    private final MVCoreConfig config;
    private final MVWorldManager worldManager;
    private final MVPlayerListener playerListener;

    @Inject
    public MVChatListener(
            MVCoreConfig config,
            MVWorldManager worldManager,
            MVPlayerListener playerListener
    ) {
        this.config = config;
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
        if (config.isEnablePrefixChat()) {
            String world = event.getPlayer().getWorld().getName();
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
            
            String prefixChatFormat = config.getPrefixChatFormat();
            prefixChatFormat = prefixChatFormat.replace("%world%", prefix).replace("%chat%", chat);
            prefixChatFormat = ChatColor.translateAlternateColorCodes('&', prefixChatFormat);
            
            event.setFormat(prefixChatFormat);
        }
    }
}
