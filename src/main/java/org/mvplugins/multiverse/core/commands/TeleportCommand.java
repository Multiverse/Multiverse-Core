package org.mvplugins.multiverse.core.commands;

import java.util.Arrays;
import java.util.EnumMap;
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
import org.mvplugins.multiverse.core.commandtools.flag.CommandFlag;
import org.mvplugins.multiverse.core.commandtools.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;
import org.mvplugins.multiverse.core.teleportation.TeleportFailureReason;

@Service
@CommandAlias("mv")
final class TeleportCommand extends CoreCommand {

    private final MVCoreConfig config;
    private final CorePermissionsChecker permissionsChecker;
    private final AsyncSafetyTeleporter safetyTeleporter;

    private final CommandFlag unsafeFlag = flag(CommandFlag.builder("--unsafe")
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
    @CommandCompletion(
            "@destinations:playerOnly|@playersarray:checkPermissions=@mvteleportother "
                    + "@destinations:othersOnly|@flags:groupName=mvteleportcommand,resolveUntil=arg2 "
                    + "@flags:groupName=mvteleportcommand")
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
        } else if (players.length > config.getConcurrentTeleportLimit()) {
            issuer.sendError(MVCorei18n.TELEPORT_TOOMANYPLAYERS,
                    Replace.COUNT.with(config.getConcurrentTeleportLimit()));
        } else {
            teleportMultiplePlayers(issuer, players, destination, parsedFlags);
        }
    }

    private void teleportSinglePlayer(MVCommandIssuer issuer, Player player,
                                      DestinationInstance<?, ?> destination,
                                      ParsedCommandFlags parsedFlags) {
        if (!permissionsChecker.checkTeleportPermissions(issuer.getIssuer(), player, destination)) {
            // TODO localize
            issuer.sendMessage(player == issuer.getPlayer()
                    ? "You do not have permission to teleport yourself!"
                    : "You do not have permission to teleport other players!");
            return;
        }

        safetyTeleporter.to(destination)
                .by(issuer)
                .checkSafety(!parsedFlags.hasFlag(unsafeFlag) && destination.checkTeleportSafety())
                .teleport(player)
                .onSuccess(() -> issuer.sendInfo(MVCorei18n.TELEPORT_SUCCESS,
                        Replace.PLAYER.with(getYouOrName(issuer, player)),
                        Replace.DESTINATION.with(destination.toString())))
                .onFailure(failure -> issuer.sendError(MVCorei18n.TELEPORT_FAILED,
                        Replace.PLAYER.with(getYouOrName(issuer, player)),
                        Replace.DESTINATION.with(destination.toString()),
                        Replace.REASON.with(failure.getFailureMessage())));
    }

    private Message getYouOrName(MVCommandIssuer issuer, Player player) {
        return player == issuer.getPlayer() ? Message.of(MVCorei18n.GENERIC_YOU) : Message.of(player.getName());
    }

    private void teleportMultiplePlayers(MVCommandIssuer issuer, Player[] players,
                                         DestinationInstance<?, ?> destination,
                                         ParsedCommandFlags parsedFlags) {
        var selfPlayer = Arrays.stream(players).filter(p -> p == issuer.getPlayer()).findFirst();
        var otherPlayer = Arrays.stream(players).filter(p -> p != issuer.getPlayer()).findFirst();
        if (selfPlayer.isPresent()
                && !permissionsChecker.checkTeleportPermissions(issuer.getIssuer(), selfPlayer.get(), destination)) {
            // TODO localize
            issuer.sendMessage("You do not have permission to teleport yourself!");
            return;
        }
        if (otherPlayer.isPresent()
                && !permissionsChecker.checkTeleportPermissions(issuer.getIssuer(), otherPlayer.get(), destination)) {
            // TODO localize
            issuer.sendMessage("You do not have permission to teleport other players!");
            return;
        }
        safetyTeleporter.to(destination)
                .by(issuer)
                .checkSafety(!parsedFlags.hasFlag(unsafeFlag) && destination.checkTeleportSafety())
                .teleport(List.of(players))
                .thenAccept(attempts -> {
                    int successCount = 0;
                    Map<TeleportFailureReason, Integer> failures = new EnumMap<>(TeleportFailureReason.class);
                    for (var attempt : attempts) {
                        if (attempt.isSuccess()) {
                            successCount++;
                        } else {
                            failures.compute(attempt.getFailureReason(),
                                    (reason, count) -> count == null ? 1 : count + 1);
                        }
                    }
                    if (successCount > 0) {
                        Logging.finer("Teleported %s players to %s", successCount, destination);
                        issuer.sendInfo(MVCorei18n.TELEPORT_SUCCESS,
                                // TODO should use {count} instead of {player} most likely
                                Replace.PLAYER.with(successCount + " players"),
                                Replace.DESTINATION.with(destination.toString()));
                    }
                    if (!failures.isEmpty()) {
                        for (var entry : failures.entrySet()) {
                            Logging.finer("Failed to teleport %s players to %s: %s",
                                    entry.getValue(), destination, entry.getKey());
                            issuer.sendError(MVCorei18n.TELEPORT_FAILED,
                                    // TODO should use {count} instead of {player} most likely
                                    Replace.PLAYER.with(entry.getValue() + " players"),
                                    Replace.DESTINATION.with(destination.toString()),
                                    Replace.REASON.with(Message.of(entry.getKey(), "")));
                        }
                    }
                });
    }
}
