package org.mvplugins.multiverse.core.listeners;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;

import java.util.Locale;

@Service
final class MVLocalesListener implements CoreListener {

    private final MVCommandManager commandManager;

    @Inject
    MVLocalesListener(@NotNull MVCommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Logging.finer(player.getName() + " joined with locale " + player.getLocale());
        commandManager.setPlayerLocale(player, serializeLocale(player.getLocale()));
    }

    @EventHandler
    void onLocaleChange(PlayerLocaleChangeEvent event) {
        Player player = event.getPlayer();
        Logging.finer(player.getName() + " changed locale from " + player.getLocale() + " to " + event.getLocale());
        commandManager.setPlayerLocale(player, serializeLocale(event.getLocale()));
    }

    private Locale serializeLocale(String locale) {
        String[] split = locale.split("_");
        return split.length > 1 ? new Locale(split[0], split[1]) : new Locale(split[0]);
    }
}
