package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.destination.ParsedDestination;
import org.bukkit.entity.Player;

@CommandAlias("mv")
public class TeleportCommand extends MultiverseCoreCommand {
    public TeleportCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("teleport|tp")
    @CommandCompletion("@players|@mvworlds:playerOnly|@destinations:playerOnly @mvworlds|@destinations")
    @Syntax("[player] <destination>")
    @Description("Allows you to the teleport to a location on your server!")
    public void onTeleportCommand(BukkitCommandIssuer issuer,

                                  @Flags("resolve=issuerAware")
                                  @Syntax("[player]")
                                  @Description("Target player to teleport.")
                                  Player[] players,

                                  @Syntax("<destination>")
                                  @Description("Location, can be a world name.")
                                  ParsedDestination<?> destination
    ) {
        // TODO Add warning if teleporting too many players at once.
        for (Player player : players) {
            issuer.sendMessage("Teleporting "
                    + (issuer.getPlayer() == player ? "you" : player.getName())
                    + " to " + destination + "...");
            this.plugin.getDestinationsProvider().playerTeleport(issuer, player, destination);
        }
    }

    @Override
    public boolean hasPermission(CommandIssuer issuer) {
        return this.plugin.getDestinationsProvider().hasAnyTeleportPermission(issuer);
    }
}
