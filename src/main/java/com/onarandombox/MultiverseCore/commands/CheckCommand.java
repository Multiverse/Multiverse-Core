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
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
@CommandAlias("mv")
public class CheckCommand extends MultiverseCoreCommand {

    @Inject
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
        this.plugin.getDestinationsProvider().checkTeleportPermissions(issuer, player, destination);
    }
}
