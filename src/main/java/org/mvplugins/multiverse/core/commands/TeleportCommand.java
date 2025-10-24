package org.mvplugins.multiverse.core.commands;

import java.util.Arrays;
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

import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.context.issueraware.PlayerArrayValue;
import org.mvplugins.multiverse.core.command.flag.CommandFlag;
import org.mvplugins.multiverse.core.command.flag.CommandFlagsManager;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.command.flags.PageFilterFlags;
import org.mvplugins.multiverse.core.command.flags.UnsafeFlags;
import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;

@Service
final class TeleportCommand extends CoreCommand {

    private final CoreConfig config;
    private final CorePermissionsChecker permissionsChecker;
    private final AsyncSafetyTeleporter safetyTeleporter;
    private final Flags flags;

    @Inject
    TeleportCommand(
            @NotNull CoreConfig config,
            @NotNull CorePermissionsChecker permissionsChecker,
            @NotNull AsyncSafetyTeleporter safetyTeleporter,
            @NotNull Flags flags
    ) {
        this.config = config;
        this.permissionsChecker = permissionsChecker;
        this.safetyTeleporter = safetyTeleporter;
        this.flags = flags;
    }

    @CommandAlias("mvtp")
    @Subcommand("teleport|tp")
    @CommandPermission("@mvteleport")
    @CommandCompletion("@playersarray:checkPermissions=@mvteleportother|@destinations:byIssuerForArg=arg1 "
            + "@destinations:notByIssuerForArg=arg1|@flags:byIssuerForArg=arg1,groupName=" + Flags.NAME + " "
            + "@flags:notByIssuerForArg=arg1,groupName=" + Flags.NAME)
    @Syntax("[player] <destination> [--unsafe]")
    @Description("{@@mv-core.teleport.description}")
    void onTeleportCommand(
            MVCommandIssuer issuer,

            @co.aikar.commands.annotation.Flags("resolve=issuerAware")
            @Syntax("[player]")
            @Description("{@@mv-core.teleport.player.description}")
            PlayerArrayValue playersValue,

            @Syntax("<destination>")
            @Description("{@@mv-core.teleport.destination.description}")
            DestinationInstance<?, ?> destination,

            @Optional
            @Syntax("[--unsafe]")
            @Description("")
            String[] flagArray) {
        Player[] players = playersValue.value();
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
                .passengerMode(config.getPassengerMode())
                .teleportSingle(player)
                .onSuccess(() -> {
                    if (parsedFlags.hasFlag(flags.silent)) {
                        return;
                    }
                    issuer.sendInfo(MVCorei18n.TELEPORT_SUCCESS,
                            Replace.PLAYER.with(getYouOrName(issuer, player)),
                            Replace.DESTINATION.with(destination.getDisplayMessage()));
                })
                .onFailureCount(reasonsCountMap -> {
                    for (var entry : reasonsCountMap.entrySet()) {
                        Logging.finer("Failed to teleport %s players to %s: %s",
                                entry.getValue(), destination, entry.getKey());
                        issuer.sendError(MVCorei18n.TELEPORT_FAILED,
                                Replace.PLAYER.with(player.getName()),
                                Replace.DESTINATION.with(destination.getDisplayMessage()),
                                Replace.REASON.with(Message.of(entry.getKey())));
                    }
                });
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
                .passengerMode(config.getPassengerMode())
                .teleport(List.of(players))
                .onSuccessCount(successCount -> {
                    if (parsedFlags.hasFlag(flags.silent)) {
                        return;
                    }
                    issuer.sendInfo(MVCorei18n.TELEPORT_SUCCESS,
                            Replace.PLAYER.with(successCount + " players"),
                            Replace.DESTINATION.with(destination.getDisplayMessage()));
                })
                .onFailureCount(reasonsCountMap -> {
                    for (var entry : reasonsCountMap.entrySet()) {
                        Logging.finer("Failed to teleport %s players to %s: %s",
                                entry.getValue(), destination, entry.getKey());
                        issuer.sendError(MVCorei18n.TELEPORT_FAILED,
                                Replace.PLAYER.with(entry.getValue() + " players"),
                                Replace.DESTINATION.with(destination.getDisplayMessage()),
                                Replace.REASON.with(Message.of(entry.getKey())));
                    }
                });
    }

    @Service
    private static final class Flags extends UnsafeFlags {

        private static final String NAME = "mvteleport";

        @Inject
        private Flags(@NotNull CommandFlagsManager flagsManager) {
            super(NAME, flagsManager);
        }

        private final CommandFlag silent = flag(CommandFlag.builder("--silent")
                .addAlias("-s")
                .build());
    }
}
