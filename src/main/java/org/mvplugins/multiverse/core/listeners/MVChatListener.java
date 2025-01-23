package org.mvplugins.multiverse.core.listeners;

import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.world.WorldManager;

/**
 * Multiverse's Listener for players.
 */
@Service
final class MVChatListener implements CoreListener {
    private final MVCoreConfig config;
    private final WorldManager worldManager;
    private final MVPlayerListener playerListener;

    @Inject
    MVChatListener(
            MVCoreConfig config,
            WorldManager worldManager,
            MVPlayerListener playerListener) {
        this.config = config;
        this.worldManager = worldManager;
        this.playerListener = playerListener;
    }

    /**
     * This handles a {@link AsyncPlayerChatEvent}.
     *
     * @param event The {@link AsyncPlayerChatEvent}.
     */
    @EventHandler
    public void asyncPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        // Check whether the Server is set to prefix the chat with the World name.
        // If not we do nothing, if so we need to check if the World has an Alias.
        if (!config.isEnablePrefixChat()) {
            return;
        }

        String world = playerListener.getPlayerWorld().get(event.getPlayer().getName());
        if (world == null) {
            world = event.getPlayer().getWorld().getName();
            playerListener.getPlayerWorld().put(event.getPlayer().getName(), world);
        }

        String prefix = this.worldManager.getLoadedWorld(world)
                .map(mvworld -> mvworld.isHidden() ? "" : mvworld.getAlias())
                .getOrElse("");
        String chat = event.getFormat();

        String prefixChatFormat = config.getPrefixChatFormat();
        prefixChatFormat = prefixChatFormat.replace("%world%", prefix).replace("%chat%", chat);
        prefixChatFormat = ChatColor.translateAlternateColorCodes('&', prefixChatFormat);

        event.setFormat(prefixChatFormat);
    }
}
