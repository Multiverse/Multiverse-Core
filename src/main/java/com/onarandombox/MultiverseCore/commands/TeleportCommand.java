package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.destination.ParsedDestination;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class TeleportCommand extends MultiverseCommand {
    public TeleportCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("tp|teleport")
    @Syntax("[player] <destination>")
    @CommandCompletion("@players") //TODO
    @Description("Allows you to the teleport to a location on your server!")
    public void doTeleportCommand(@NotNull CommandIssuer issuer,

                                  @Syntax("[player]")
                                  @Description("Target player to teleport.")
                                  Player player,

                                  @Syntax("<destination>")
                                  @Description("Location, can be a world name.")
                                  String destinationString
    ) {
        ParsedDestination<?> parsedDestination = this.plugin.getDestinationsManager().parseDestination(destinationString);
        if (parsedDestination == null) {
            issuer.sendMessage("Invalid destination: " + destinationString);
            return;
        }

        issuer.sendMessage("Teleporting " + ((BukkitCommandIssuer) issuer).getPlayer() + " to " + parsedDestination + "...");
        this.plugin.getDestinationsManager().playerTeleport((BukkitCommandIssuer) issuer, player, parsedDestination);
    }
}
