package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.*;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;
import org.mvplugins.multiverse.core.utils.MVCorei18n;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
@CommandAlias("mv")
class SpawnCommand extends CoreCommand {
    private final WorldManager worldManager;
    private final AsyncSafetyTeleporter safetyTeleporter;

    @Inject
    SpawnCommand(@NotNull MVCommandManager commandManager,
                 @NotNull WorldManager worldManager,
                 @NotNull AsyncSafetyTeleporter safetyTeleporter) {
        super(commandManager);
        this.worldManager = worldManager;
        this.safetyTeleporter = safetyTeleporter;
    }

    @CommandAlias("mvspawn")
    @Subcommand("spawn")
    @CommandCompletion("@players")
    @Syntax("[player]")
    @Description("{@@mv-core.spawn.description}")
    void onSpawnTpCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("[player]")
            @Description("{@@mv-core.spawn.player.description}")
            Player player) {
        // TODO: Better handling of permission checking with CorePermissionsChecker
        String permission = player.equals(issuer.getPlayer()) ? "multiverse.core.spawn.self" : "multiverse.core.spawn.other";
        if (!issuer.hasPermission(permission)) {
            issuer.sendMessage("You do not have permission to use this command!");
            return;
        }

        LoadedMultiverseWorld world = worldManager.getLoadedWorld(player.getWorld()).getOrNull();
        if (world == null) {
            issuer.sendMessage("The world the player you are trying to teleport is in, is not a multiverse world");
            return;
        }

        // Teleport the player
        // TODO: Different message for teleporting self vs others
        safetyTeleporter.teleportSafely(issuer.getIssuer(), player, world.getSpawnLocation())
                .onSuccess(() -> player.sendMessage(commandManager.formatMessage(
                        issuer,
                        MessageType.INFO,
                        MVCorei18n.SPAWN_SUCCESS,
                        "{teleporter}",
                        getTeleporterName(issuer, player)
                )))
                .onFailure(failure -> {
                    issuer.sendError(
                            MVCorei18n.SPAWN_FAILED,
                            "{teleporter}",
                            getTeleporterName(issuer, player)
                    );
                    issuer.sendError(failure.getFailureMessage());
                });

        Logging.fine("Teleported " + player.getName() + " to " + world.getSpawnLocation().getX() + ", " + world.getSpawnLocation().getY() + ", " + world.getSpawnLocation().getZ());
    }

    private String getTeleporterName(BukkitCommandIssuer issuer, Player teleportTo) {
        if (issuer.getIssuer().getName().equals("CONSOLE")) {
            return commandManager.formatMessage(issuer, MessageType.INFO, MVCorei18n.SPAWN_CONSOLENAME);
        }
        if (issuer.getIssuer().getName().equals(teleportTo.getName())) {
            return commandManager.formatMessage(issuer, MessageType.INFO, MVCorei18n.SPAWN_YOU);
        }
        return issuer.getIssuer().getName();
    }

    @Override
    public boolean hasPermission(CommandIssuer issuer) {
        // TODO: Fix autocomplete showing even if the player doesn't have permission
        return issuer.hasPermission("multiverse.core.spawn.self") || issuer.hasPermission("multiverse.core.spawn.other");
    }
}
