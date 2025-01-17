package org.mvplugins.multiverse.core.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;
import org.mvplugins.multiverse.core.teleportation.TeleportFailureReason;
import org.mvplugins.multiverse.core.utils.MVCorei18n;
import org.mvplugins.multiverse.core.utils.message.Message;

import static org.mvplugins.multiverse.core.utils.message.MessageReplacement.replace;

@Service
@CommandAlias("mv")
class TeleportCommand extends CoreCommand {

    private final MVCoreConfig config;
    private final CorePermissionsChecker permissionsChecker;
    private final AsyncSafetyTeleporter safetyTeleporter;

    private final CommandFlag UNSAFE_FLAG = flag(CommandFlag.builder("--unsafe")
            .addAlias("-u")
            .build());

    @Inject
    TeleportCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull MVCoreConfig config,
            @NotNull CorePermissionsChecker permissionsChecker,
            @NotNull AsyncSafetyTeleporter safetyTeleporter) {
        super(commandManager);
        this.config = config;
        this.permissionsChecker = permissionsChecker;
        this.safetyTeleporter = safetyTeleporter;
    }

    @CommandAlias("mvtp")
    @Subcommand("teleport|tp")
    @CommandPermission("@mvteleport")
    @CommandCompletion("@players|@destinations:playerOnly @destinations:othersOnly|@flags:groupName=mvteleportcommand @flags:groupName=mvteleportcommand")
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

        if (players.length == 1) {
            teleportSinglePlayer(issuer, players[0], destination, parsedFlags);
        }
        else if (players.length > config.getConcurrentTeleportLimit()) {
            issuer.sendError(MVCorei18n.TELEPORT_TOOMANYPLAYERS,
                    replace("{count}").with(config.getConcurrentTeleportLimit()));
        } else {
            teleportMultiplePlayers(issuer, players, destination, parsedFlags);
        }
    }

    private void teleportSinglePlayer(MVCommandIssuer issuer, Player player, DestinationInstance<?, ?> destination, ParsedCommandFlags parsedFlags) {
        if (!permissionsChecker.checkTeleportPermissions(issuer.getIssuer(), player, destination)) {
            issuer.sendMessage(player == issuer.getPlayer()
                    ? "You do not have permission to teleport yourself!"
                    : "You do not have permission to teleport other players!");
            return;
        }

        safetyTeleporter.to(destination)
                .by(issuer)
                .checkSafety(!parsedFlags.hasFlag(UNSAFE_FLAG) && destination.checkTeleportSafety())
                .teleport(player)
                .onSuccess(() -> issuer.sendInfo(MVCorei18n.TELEPORT_SUCCESS,
                        replace("{player}").with(getYouOrName(issuer, player)),
                        replace("{destination}").with(destination.toString())))
                .onFailure(failure -> issuer.sendError(MVCorei18n.TELEPORT_FAILED,
                        replace("{player}").with(getYouOrName(issuer, player)),
                        replace("{destination}").with(destination.toString()),
                        replace("{reason}").with(failure.getFailureMessage())));
    }

    private String getYouOrName(MVCommandIssuer issuer, Player player) {
        return player == issuer.getPlayer() ? "you" : player.getName();
    }

    private void teleportMultiplePlayers(MVCommandIssuer issuer, Player[] players, DestinationInstance<?, ?> destination, ParsedCommandFlags parsedFlags) {
        var selfPlayer = Arrays.stream(players).filter(p -> p == issuer.getPlayer()).findFirst();
        var otherPlayer = Arrays.stream(players).filter(p -> p != issuer.getPlayer()).findFirst();
        if (selfPlayer.isPresent() && !permissionsChecker.checkTeleportPermissions(issuer.getIssuer(), selfPlayer.get(), destination)) {
            issuer.sendMessage("You do not have permission to teleport yourself!");
            return;
        }
        if (otherPlayer.isPresent() && !permissionsChecker.checkTeleportPermissions(issuer.getIssuer(), otherPlayer.get(), destination)) {
            issuer.sendMessage("You do not have permission to teleport other players!");
            return;
        }
        safetyTeleporter.to(destination)
                .by(issuer)
                .checkSafety(!parsedFlags.hasFlag(UNSAFE_FLAG) && destination.checkTeleportSafety())
                .teleport(List.of(players))
                .thenAccept(attempts -> {
                    int successCount = 0;
                    Map<TeleportFailureReason, Integer> failures = new HashMap<>();
                    for (var attempt : attempts) {
                        if (attempt.isSuccess()) {
                            successCount++;
                        } else {
                            failures.compute(attempt.getFailureReason(), (reason, count) -> count == null ? 1 : count + 1);
                        }
                    }
                    if (successCount > 0) {
                        Logging.finer("Teleported %s players to %s", successCount, destination);
                        issuer.sendInfo(MVCorei18n.TELEPORT_SUCCESS,
                                replace("{player}").with(successCount + " players"),
                                replace("{destination}").with(destination.toString()));
                    }
                    if (!failures.isEmpty()) {
                        for (var entry : failures.entrySet()) {
                            Logging.finer("Failed to teleport %s players to %s: %s", entry.getValue(), destination, entry.getKey());
                            issuer.sendError(MVCorei18n.TELEPORT_FAILED,
                                    replace("{player}").with(entry.getValue() + " players"),
                                    replace("{destination}").with(destination.toString()),
                                    replace("{reason}").with(Message.of(entry.getKey(), "")));
                        }
                    }
                });
    }
}
