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

import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.flag.CommandFlag;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.command.flags.UnsafeFlags;
import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;
import org.mvplugins.multiverse.core.teleportation.TeleportFailureReason;

@Service
final class TeleportCommand extends CoreCommand {

    private final CoreConfig config;
    private final CorePermissionsChecker permissionsChecker;
    private final AsyncSafetyTeleporter safetyTeleporter;
    private final UnsafeFlags flags;

    @Inject
    TeleportCommand(
            @NotNull CoreConfig config,
            @NotNull CorePermissionsChecker permissionsChecker,
            @NotNull AsyncSafetyTeleporter safetyTeleporter,
            @NotNull UnsafeFlags flags
    ) {
        this.config = config;
        this.permissionsChecker = permissionsChecker;
        this.safetyTeleporter = safetyTeleporter;
        this.flags = flags;
    }

    @CommandAlias("mvtp")
    @Subcommand("teleport|tp")
    @CommandPermission("@mvteleport")
    @CommandCompletion(
            "@destinations:playerOnly|@playersarray:checkPermissions=@mvteleportother "
                    + "@destinations:othersOnly|@flags:groupName=" + UnsafeFlags.NAME + ",resolveUntil=arg2 "
                    + "@flags:groupName=" + UnsafeFlags.NAME)
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
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);

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
        if (!permissionsChecker.checkTeleportPermission(issuer.getIssuer(), player, destination)) {
            // TODO localize
            issuer.sendMessage(player == issuer.getPlayer()
                    ? "You do not have permission to teleport yourself!"
                    : "You do not have permission to teleport other players!");
            return;
        }

        safetyTeleporter.to(destination)
                .by(issuer)
                .checkSafety(!parsedFlags.hasFlag(flags.unsafe) && destination.checkTeleportSafety())
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
        if (!permissionsChecker.checkTeleportPermission(issuer.getIssuer(), Arrays.asList(players), destination)) {
            // TODO localize
            issuer.sendMessage("You do not have permission to teleport all these players!");
            return;
        }

        safetyTeleporter.to(destination)
                .by(issuer)
                .checkSafety(!parsedFlags.hasFlag(flags.unsafe) && destination.checkTeleportSafety())
                .teleport(List.of(players))
                .onSuccessCount(successCount -> issuer.sendInfo(MVCorei18n.TELEPORT_SUCCESS,
                        Replace.PLAYER.with(successCount + " players"),
                        Replace.DESTINATION.with(destination.toString())))
                .onFailureCount(reasonsCountMap -> {
                    for (var entry : reasonsCountMap.entrySet()) {
                        Logging.finer("Failed to teleport %s players to %s: %s",
                                entry.getValue(), destination, entry.getKey());
                        issuer.sendError(MVCorei18n.TELEPORT_FAILED,
                                Replace.PLAYER.with(entry.getValue() + " players"),
                                Replace.DESTINATION.with(destination.toString()),
                                Replace.REASON.with(Message.of(entry.getKey())));
                    }
                });
    }
}
