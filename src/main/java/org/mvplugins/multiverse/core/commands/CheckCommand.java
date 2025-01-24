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
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;
import org.mvplugins.multiverse.core.teleportation.LocationManipulation;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

@Service
@CommandAlias("mv")
final class CheckCommand extends CoreCommand {

    private final CorePermissionsChecker corePermissionsChecker;
    private final LocationManipulation locationManipulation;

    @Inject
    CheckCommand(@NotNull MVCommandManager commandManager,
                 @NotNull CorePermissionsChecker corePermissionsChecker,
                 @NotNull LocationManipulation locationManipulation) {
        super(commandManager);
        this.corePermissionsChecker = corePermissionsChecker;
        this.locationManipulation = locationManipulation;
    }

    @CommandAlias("mvcheck")
    @Subcommand("check")
    @CommandPermission("multiverse.core.check")
    @CommandCompletion("@players @destinations")
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
        issuer.sendInfo(this.corePermissionsChecker.checkTeleportPermissions(player, player, destination)
                        ? MVCorei18n.CHECK_HASPERMISSION
                        : MVCorei18n.CHECK_NOPERMISSION,
                Replace.PLAYER.with(player.getName()),
                Replace.DESTINATION.with(destination));
        issuer.sendInfo(MVCorei18n.CHECK_LOCATION,
                replace("{location}").with(destination.getLocation(player)
                        .map(locationManipulation::locationToString)
                        .map(Message::of)
                        .getOrElse(() -> Message.of(MVCorei18n.GENERIC_NULL, "Null!"))));

        // TODO: Show permission required for this particular destination
    }
}
