package org.mvplugins.multiverse.core.commands;

import java.util.List;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.flags.CommandFlag;
import org.mvplugins.multiverse.core.commandtools.flags.ParsedCommandFlags;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;
import org.mvplugins.multiverse.core.utils.MVCorei18n;

@Service
@CommandAlias("mv")
class TeleportCommand extends CoreCommand {

    private final CorePermissionsChecker permissionsChecker;
    private final AsyncSafetyTeleporter safetyTeleporter;

    private final CommandFlag UNSAFE_FLAG = flag(CommandFlag.builder("--unsafe")
            .addAlias("-u")
            .build());

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
    @CommandPermission("@mvteleport")
    @CommandCompletion("@players|@mvworlds:playerOnly|@destinations:playerOnly @mvworlds|@destinations|@flags:groupName=mvteleportcommand @flags:groupName=mvteleportcommand")
    @Syntax("[player] <destination> [--unsafe]")
    @Description("{@@mv-core.teleport.description}")
    void onTeleportCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("[player]")
            @Description("{@@mv-core.teleport.player.description}")
            Player[] players,

            @Syntax("<destination>")
            @Description("{@@mv-core.teleport.destination.description}")
            DestinationInstance<?, ?> destination,

            @Optional
            @Syntax("[--unsafe]")
            @Description("")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        // TODO: Add warning if teleporting too many players at once.
        String playerName = players.length == 1
                ? issuer.getPlayer() == players[0] ? "you" : players[0].getName()
                : players.length + " players";

        // TODO: Multi player permission checking
        if (!permissionsChecker.checkTeleportPermissions(issuer.getIssuer(), players[0], destination)) {
            issuer.sendMessage("You do not have teleport permissions");
            return;
        }

        (parsedFlags.hasFlag(UNSAFE_FLAG)
                ? safetyTeleporter.teleport(issuer.getIssuer(), List.of(players), destination)
                : safetyTeleporter.teleportSafely(issuer.getIssuer(), List.of(players), destination))
                .thenAccept(attempts -> {
                    Logging.fine("Async teleport completed: %s", attempts);
                    issuer.sendInfo(MVCorei18n.TELEPORT_SUCCESS,
                            "{player}", playerName, "{destination}", destination.toString());
                })
                .exceptionally(throwable -> {
                    Logging.severe("Error while teleporting %s to %s: %s",
                            playerName, destination, throwable.getMessage());
                });
    }
}
