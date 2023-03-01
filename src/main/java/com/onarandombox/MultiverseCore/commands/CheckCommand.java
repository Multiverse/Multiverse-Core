package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.destination.ParsedDestination;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class CheckCommand extends MultiverseCoreCommand {
    public CheckCommand(@NotNull MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("check")
    @CommandPermission("multiverse.core.check")
    @CommandCompletion("@players @destinations|@mvworlds")
    @Syntax("<player> <destination>")
    @Description("Checks if a player can teleport to a destination.")
    public void onCheckCommand(BukkitCommandIssuer issuer,

                               @Syntax("<player>")
                               @Description("Player to check destination on.")
                               Player player,

                               @Syntax("<destination>")
                               @Description("A destination location, e.g. a world name.")
                               ParsedDestination<?> destination
    ) {
        issuer.sendMessage("Checking " + player + " to " + destination + "...");
        //TODO More detailed output on permissions required.
        if (!this.plugin.getPlayerActionChecker().canUseDestinationToTeleport(issuer.getIssuer(), player, destination).asBoolean()) {
            issuer.sendMessage("You don't have permission to use this destination.");
        }
        if (!this.plugin.getPlayerActionChecker().canGoToDestination(issuer.getIssuer(), player, destination).asBoolean()) {
            issuer.sendMessage("You don't have permission to teleport to this destination.");
        }
    }
}
