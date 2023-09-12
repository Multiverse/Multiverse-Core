package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.api.LocationManipulation;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.utils.MVCorei18n;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;

@Service
@CommandAlias("mv")
class CoordinatesCommand extends MultiverseCommand {

    private final LocationManipulation locationManipulation;

    @Inject
    CoordinatesCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull LocationManipulation locationManipulation) {
        super(commandManager);
        this.locationManipulation = locationManipulation;
    }

    @Subcommand("coordinates|coords|coord|co")
    @CommandPermission("multiverse.core.coord")
    @Description("{@@mv-core.coordinates.description}")
    void onCoordinatesCommand(
            BukkitCommandIssuer issuer,

            @Flags("resolve=issuerOnly")
            Player player,

            @Flags("resolve=issuerOnly")
            LoadedMultiverseWorld world) {
        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_TITLE);
        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_WORLD, "{world}", world.getName());
        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_ALIAS, "{alias}", world.getAlias());
        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_WORLDSCALE, "{scale}", String.valueOf(world.getScale()));
        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_COORDINATES, "{coordinates}", locationManipulation.strCoords(player.getLocation()));
        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_DIRECTION, "{direction}", locationManipulation.getDirection(player.getLocation()));
    }
}
