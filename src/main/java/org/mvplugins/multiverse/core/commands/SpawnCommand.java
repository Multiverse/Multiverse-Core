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
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;
import org.mvplugins.multiverse.core.utils.MVCorei18n;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
@CommandAlias("mv")
class SpawnCommand extends CoreCommand {
    private final WorldManager worldManager;
    private final AsyncSafetyTeleporter safetyTeleporter;
    private final CorePermissionsChecker permissionsChecker;

    @Inject
    SpawnCommand(@NotNull MVCommandManager commandManager,
                 @NotNull WorldManager worldManager,
                 @NotNull AsyncSafetyTeleporter safetyTeleporter,
                 @NotNull CorePermissionsChecker permissionsChecker) {
        super(commandManager);
        this.worldManager = worldManager;
        this.safetyTeleporter = safetyTeleporter;
        this.permissionsChecker = permissionsChecker;
    }

    @CommandAlias("mvspawn")
    @Subcommand("spawn")
    @CommandPermission("@mvspawn")
    @CommandCompletion("@players")
    @Syntax("[player]")
    @Description("{@@mv-core.spawn.description}")
    void onSpawnTpCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("[player]")
            @Description("{@@mv-core.spawn.player.description}")
            Player player) {
        LoadedMultiverseWorld world = worldManager.getLoadedWorld(player.getWorld()).getOrNull();
        if (world == null) {
            issuer.sendMessage("The world the player you are trying to teleport is in, is not a multiverse world");
            return;
        }

        if (!permissionsChecker.hasSpawnPermission(issuer.getIssuer(), player, world)) {
            issuer.sendMessage("You do not have permission to use this command in this world!");
            return;
        }

        // Teleport the player to spawn
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
}
