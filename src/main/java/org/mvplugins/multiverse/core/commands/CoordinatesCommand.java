package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.teleportation.LocationManipulation;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

@Service
@CommandAlias("mv")
final class CoordinatesCommand extends CoreCommand {

    private final LocationManipulation locationManipulation;

    @Inject
    CoordinatesCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull LocationManipulation locationManipulation) {
        super(commandManager);
        this.locationManipulation = locationManipulation;
    }

    @CommandAlias("mvcoord|mvco")
    @Subcommand("coordinates|coords|coord|co")
    @CommandPermission("multiverse.core.coord")
    @Description("{@@mv-core.coordinates.description}")
    void onCoordinatesCommand(
            CommandIssuer issuer,

            @Flags("resolve=issuerOnly")
            Player player,

            @Flags("resolve=issuerOnly")
            MultiverseWorld world) {
        MVCorei18n.COORDINATES_INFO_TITLE.sendInfo(issuer);
        MVCorei18n.COORDINATES_INFO_WORLD.sendInfo(issuer, Replace.WORLD.with(world.getName()));
        MVCorei18n.COORDINATES_INFO_ALIAS.sendInfo(issuer, replace("{alias}").with(world.getAlias()));
        MVCorei18n.COORDINATES_INFO_WORLDSCALE.sendInfo(issuer,
                replace("{scale}").with(String.valueOf(world.getScale())));
        MVCorei18n.COORDINATES_INFO_COORDINATES.sendInfo(issuer,
                replace("{coordinates}").with(locationManipulation.strCoords(player.getLocation())));
        MVCorei18n.COORDINATES_INFO_DIRECTION.sendInfo(issuer,
                replace("{direction}").with(locationManipulation.getDirection(player.getLocation())));
    }
}
