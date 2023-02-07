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
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class TeleportCommand extends MultiverseCommand {
    public TeleportCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("teleport|tp")
    @Syntax("[player] <destination>")
    @CommandCompletion("@players|@mvworlds:playeronly|@destinations:playeronly @mvworlds|@destinations")
    @Description("Allows you to the teleport to a location on your server!")
    public void onTeleportCommand(@NotNull CommandIssuer issuer,

                                  @Flags("resolve=issueraware")
                                  @Syntax("[player]")
                                  @Description("Target player to teleport.")
                                  Player player,

                                  @Syntax("<destination>")
                                  @Description("Location, can be a world name.")
                                  ParsedDestination<?> destination
    ) {
        issuer.sendMessage("Teleporting "
                + (((BukkitCommandIssuer) issuer).getPlayer() == player ? "you" : player.getName())
                + " to " + destination + "...");
        this.plugin.getDestinationsManager().playerTeleport((BukkitCommandIssuer) issuer, player, destination);
    }

    @Override
    public boolean hasPermission(CommandIssuer issuer) {
        return this.plugin.getDestinationsManager().hasAnyTeleportPermission(issuer);
    }
}
