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
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
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
    @Description("{@@mv-core.check.description}")
    public void onCheckCommand(BukkitCommandIssuer issuer,

                               @Syntax("<player>")
                               @Description("{@@mv-core.check.player.description}")
                               Player player,

                               @Syntax("<destination>")
                               @Description("{@@mv-core.check.destination.description}")
                               ParsedDestination<?> destination
    ) {
        issuer.sendInfo(MVCorei18n.CHECK_CHECKING,
                "{player}", player.getName(),
                "{destination}", destination.toString());
        //TODO More detailed output on permissions required.
        this.plugin.getDestinationsProvider().checkTeleportPermissions(issuer, player, destination);
    }
}
