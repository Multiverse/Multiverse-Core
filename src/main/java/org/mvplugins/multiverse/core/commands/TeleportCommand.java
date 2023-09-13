package org.mvplugins.multiverse.core.commands;

import java.util.Arrays;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.destination.ParsedDestination;
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;
import org.mvplugins.multiverse.core.utils.MVCorei18n;
import org.mvplugins.multiverse.core.utils.result.AsyncResult;

@Service
@CommandAlias("mv")
class TeleportCommand extends MultiverseCommand {

    private final CorePermissionsChecker permissionsChecker;
    private final AsyncSafetyTeleporter safetyTeleporter;

    @Inject
    TeleportCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull CorePermissionsChecker permissionsChecker,
            @NotNull AsyncSafetyTeleporter safetyTeleporter) {
        super(commandManager);
        this.permissionsChecker = permissionsChecker;
        this.safetyTeleporter = safetyTeleporter;
    }

    @CommandAlias("mvtp")
    @Subcommand("teleport|tp")
    @CommandCompletion("@players|@mvworlds:playerOnly|@destinations:playerOnly @mvworlds|@destinations")
    @Syntax("[player] <destination>")
    @Description("{@@mv-core.teleport.description}")
    void onTeleportCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("[player]")
            @Description("{@@mv-core.teleport.player.description}")
            Player[] players,

            @Syntax("<destination>")
            @Description("{@@mv-core.teleport.destination.description}")
            ParsedDestination<?> destination) {
        // TODO: Add warning if teleporting too many players at once.

        String playerName = players.length == 1
                ? issuer.getPlayer() == players[0] ? "you" : players[0].getName()
                : players.length + " players";

        // TODO: Multi player permission checking
        if (!permissionsChecker.checkTeleportPermissions(issuer.getIssuer(), players[0], destination)) {
            issuer.sendMessage("You do not have teleport permissions");
            return;
        }

        issuer.sendInfo(MVCorei18n.TELEPORT_SUCCESS,
                "{player}", playerName, "{destination}", destination.toString());

        AsyncResult.allOf(Arrays.stream(players)
                        .map(player -> safetyTeleporter.teleportSafely(issuer.getIssuer(), player, destination))
                        .toList())
                .thenRun(() -> Logging.fine("Async teleport result: %s"))
                .exceptionally(throwable -> {
                    Logging.severe("Error while teleporting %s to %s: %s",
                            playerName, destination, throwable.getMessage());
                });
    }

    @Override
    public boolean hasPermission(CommandIssuer issuer) {
        return permissionsChecker.hasAnyTeleportPermission(issuer.getIssuer());
    }
}
