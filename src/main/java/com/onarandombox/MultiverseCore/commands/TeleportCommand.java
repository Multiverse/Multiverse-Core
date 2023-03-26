package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
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
        for (Player player : players) {
            issuer.sendInfo(MVCorei18n.TELEPORT_SUCCESS,
                    "{player}", issuer.getPlayer() == player ? "you" : player.getName(),
                    "{destination}", destination.toString());
            this.destinationsProvider.playerTeleport(issuer, player, destination);
        }
    }

    @Override
    public boolean hasPermission(CommandIssuer issuer) {
        return this.destinationsProvider.hasAnyTeleportPermission(issuer);
    }
}
