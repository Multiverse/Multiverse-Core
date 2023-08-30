package com.onarandombox.MultiverseCore.commands;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.destination.DestinationsProvider;
import com.onarandombox.MultiverseCore.destination.ParsedDestination;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.jvnet.hk2.annotations.Service;

@Service
@CommandAlias("mv")
public class TeleportCommand extends MultiverseCommand {

    private final DestinationsProvider destinationsProvider;

    @Inject
    public TeleportCommand(MVCommandManager commandManager, DestinationsProvider destinationsProvider) {
        super(commandManager);
        this.destinationsProvider = destinationsProvider;
    }

    @Subcommand("teleport|tp")
    @CommandCompletion("@players|@mvworlds:playerOnly|@destinations:playerOnly @mvworlds|@destinations")
    @Syntax("[player] <destination>")
    @Description("{@@mv-core.teleport.description}")
    public void onTeleportCommand(BukkitCommandIssuer issuer,

                                  @Flags("resolve=issuerAware")
                                  @Syntax("[player]")
                                  @Description("{@@mv-core.teleport.player.description}")
                                  Player[] players,

                                  @Syntax("<destination>")
                                  @Description("{@@mv-core.teleport.destination.description}")
                                  ParsedDestination<?> destination
    ) {
        // TODO Add warning if teleporting too many players at once.

        String playerName = players.length == 1
                ? issuer.getPlayer() == players[0] ? "you" : players[0].getName()
                : players.length + " players";

        issuer.sendInfo(MVCorei18n.TELEPORT_SUCCESS,
                "{player}", playerName, "{destination}", destination.toString());

        CompletableFuture.allOf(Arrays.stream(players)
                        .map(player -> this.destinationsProvider.playerTeleportAsync(issuer, player, destination))
                        .toArray(CompletableFuture[]::new))
                .thenRun(() -> Logging.finer("Async teleport completed."));
    }

    @Override
    public boolean hasPermission(CommandIssuer issuer) {
        return this.destinationsProvider.hasAnyTeleportPermission(issuer);
    }
}
