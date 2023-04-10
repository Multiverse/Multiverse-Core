package com.onarandombox.MultiverseCore.listeners;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.config.MVCoreConfig;
import com.onarandombox.MultiverseCore.economy.MVEconomist;
import com.onarandombox.MultiverseCore.inject.InjectableListener;
import com.onarandombox.MultiverseCore.teleportation.TeleportQueue;
import com.onarandombox.MultiverseCore.utils.result.ResultGroup;
import com.onarandombox.MultiverseCore.world.entrycheck.EntryFeeResult;
import com.onarandombox.MultiverseCore.world.entrycheck.WorldEntryCheckerProvider;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
public class NewMVPlayerListener implements InjectableListener {
    private final @NotNull MVCoreConfig config;
    private final @NotNull Server server;
    private final @NotNull TeleportQueue teleportQueue;
    private final @NotNull MVWorldManager worldManager;
    private final @NotNull WorldEntryCheckerProvider worldEntryCheckerProvider;
    private final @NotNull MVEconomist economist;

    @Inject
    NewMVPlayerListener(
            @NotNull MVCoreConfig config,
            @NotNull Server server,
            @NotNull TeleportQueue teleportQueue,
            @NotNull MVWorldManager worldManager,
            @NotNull WorldEntryCheckerProvider worldEntryCheckerProvider,
            @NotNull MVEconomist economist
    ) {
        this.config = config;
        this.server = server;
        this.teleportQueue = teleportQueue;
        this.worldManager = worldManager;
        this.worldEntryCheckerProvider = worldEntryCheckerProvider;
        this.economist = economist;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) {
            // We don't want to do anything if the event is already cancelled.
            return;
        }

        Player teleportee = event.getPlayer();

        Option<String> teleporterName = teleportQueue.popFromQueue(teleportee.getName());
        if (teleporterName.isEmpty() && !config.getTeleportIntercept()) {
            // We don't want to do anything if teleport interception is disabled, and teleport is not by MV.
            return;
        }

        CommandSender teleporter = teleporterName.map(name -> name.equals("CONSOLE")
                        ? server.getConsoleSender()
                        : server.getPlayerExact(name))
                .getOrElse(teleportee);

        Option<MVWorld> fromWorld = Option.of(this.worldManager.getMVWorld(event.getFrom().getWorld()));
        MVWorld toWorld = Option.of(event.getTo()).map(to -> this.worldManager.getMVWorld(to.getWorld())).getOrNull();

        if (toWorld == null) {
            // We don't want to do anything if the destination world is not managed by MV.
            Logging.fine("Player '%s' is teleporting to world '%s' which is not managed by Multiverse-Core. No further actions will be taken by Multiverse-Core.",
                    teleportee.getName(), event.getTo().getWorld());
            return;
        }
        if (fromWorld.filter(world -> world.equals(toWorld)).isDefined()) {
            // We don't want to do anything if the destination world is the same as the origin world.
            Logging.fine("Player '%s' is teleporting to the same world.", teleportee.getName());
            return;
        }

        ResultGroup worldEntryResult = worldEntryCheckerProvider.forWorld(teleportee, fromWorld.getOrNull(), toWorld).canEnterWorld()
                .success(() -> Logging.fine("MV-Core is allowing '%s' to go to '%s'.", teleportee.getName(), toWorld.getName()))
                .successWithReason(EntryFeeResult.Success.ENOUGH_MONEY, () -> {
                    economist.payEntryFee((Player) teleporter, toWorld);
                    //TODO send payment message
                })
                .failure(() -> {
                    event.setCancelled(true);
                    Logging.fine("MV-Core is denying '%s' from going to '%s'.", teleportee.getName(), toWorld.getName());
                    //TODO send player reason for failure
                });

        Logging.fine("World entry result for player '%s', from '%s' to '%s': %s",
                teleportee.getName(), fromWorld.map(MVWorld::getName).getOrNull(), toWorld.getName(), worldEntryResult);
    }
}
