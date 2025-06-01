package org.mvplugins.multiverse.core.listeners;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import jakarta.inject.Inject;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.dynamiclistener.EventRunnable;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.EventClass;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.EventMethod;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.IgnoreIfCancelled;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.SkipIfEventExist;
import org.mvplugins.multiverse.core.world.WorldManager;

/**
 * Multiverse's Listener for players.
 */
@Service
final class MVChatListener implements CoreListener {
    private final CoreConfig config;
    private final WorldManager worldManager;
    private final MVPlayerListener playerListener;

    @Inject
    MVChatListener(
            CoreConfig config,
            WorldManager worldManager,
            MVPlayerListener playerListener) {
        this.config = config;
        this.worldManager = worldManager;
        this.playerListener = playerListener;
    }

    @EventClass("io.papermc.paper.event.player.AsyncChatEvent")
    @IgnoreIfCancelled
    EventRunnable asyncChat() {
        return new EventRunnable<AsyncChatEvent>() {
            @Override
            public void onEvent(AsyncChatEvent event) {
                if (!config.isEnablePrefixChat()) {
                    return;
                }
                ChatRenderer currentRenderer = event.renderer();
                ChatRenderer chatRenderer = getPrefixedChatRenderer(currentRenderer);
                event.renderer(chatRenderer);
            }

            private ChatRenderer getPrefixedChatRenderer(ChatRenderer currentRenderer) {
                return (source, sourceDisplayName, message, viewer) ->
                        LegacyComponentSerializer.legacyAmpersand()
                                .deserialize(config.getPrefixChatFormat())
                                .replaceText(TextReplacementConfig.builder()
                                        .matchLiteral("%world%")
                                        .replacement(LegacyComponentSerializer.legacyAmpersand()
                                                .deserialize(getWorldName(source)))
                                        .build())
                                .replaceText(TextReplacementConfig.builder()
                                        .matchLiteral("%chat%")
                                        .replacement(currentRenderer.render(source, sourceDisplayName, message, viewer))
                                        .build());
            }
        };
    }

    /**
     * This handles a {@link AsyncPlayerChatEvent}.
     *
     * @param event The {@link AsyncPlayerChatEvent}.
     */
    @EventMethod
    @SkipIfEventExist("io.papermc.paper.event.player.AsyncChatEvent")
    @IgnoreIfCancelled
    void asyncPlayerChat(AsyncPlayerChatEvent event) {
        // Check whether the Server is set to prefix the chat with the World name.
        // If not we do nothing, if so we need to check if the World has an Alias.
        if (!config.isEnablePrefixChat()) {
            return;
        }

        String worldName = getWorldName(event.getPlayer());
        String chat = event.getFormat();

        String prefixChatFormat = config.getPrefixChatFormat();
        prefixChatFormat = prefixChatFormat.replace("%world%", worldName).replace("%chat%", chat);
        prefixChatFormat = ChatColor.translateAlternateColorCodes('&', prefixChatFormat);

        event.setFormat(prefixChatFormat);
    }

    private String getWorldName(Player player) {
        String world = playerListener.getPlayerWorld().get(player.getName());
        if (world == null) {
            world = player.getWorld().getName();
            playerListener.getPlayerWorld().put(player.getName(), world);
        }
        return this.worldManager.getLoadedWorld(world)
                .map(mvworld -> mvworld.isHidden() ? "" : mvworld.getAliasOrName())
                .getOrElse("");
    }
}
