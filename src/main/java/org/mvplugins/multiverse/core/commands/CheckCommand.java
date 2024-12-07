package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.utils.MVCorei18n;

@Service
@CommandAlias("mv")
class CheckCommand extends CoreCommand {

    private final DestinationsProvider destinationsProvider;

    @Inject
    CheckCommand(@NotNull MVCommandManager commandManager, @NotNull DestinationsProvider destinationsProvider) {
        super(commandManager);
        this.destinationsProvider = destinationsProvider;
    }

    @CommandAlias("mvcheck")
    @Subcommand("check")
    @CommandPermission("multiverse.core.check")
    @CommandCompletion("@players @destinations|@mvworlds")
    @Syntax("<player> <destination>")
    @Description("{@@mv-core.check.description}")
    void onCheckCommand(
            MVCommandIssuer issuer,

            @Syntax("<player>")
            @Description("{@@mv-core.check.player.description}")
            Player player,

            @Syntax("<destination>")
            @Description("{@@mv-core.check.destination.description}")
            DestinationInstance<?, ?> destination) {
        issuer.sendInfo(MVCorei18n.CHECK_CHECKING,
                "{player}", player.getName(),
                "{destination}", destination.toString());
        // TODO: More detailed output on permissions required.
        // this.destinationsProvider.checkTeleportPermissions(issuer, player, destination);
    }
}
