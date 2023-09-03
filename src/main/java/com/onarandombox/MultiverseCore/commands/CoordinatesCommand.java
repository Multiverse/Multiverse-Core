package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import com.onarandombox.MultiverseCore.api.LocationManipulation;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.worldnew.MVWorld;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
@CommandAlias("mv")
public class CoordinatesCommand extends MultiverseCommand {

    private final LocationManipulation locationManipulation;

    @Inject
    public CoordinatesCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull LocationManipulation locationManipulation
    ) {
        super(commandManager);
        this.locationManipulation = locationManipulation;
    }

    @Subcommand("coordinates|coords|coord|co")
    @CommandPermission("multiverse.core.coord")
    @Description("{@@mv-core.coordinates.description}")
    public void onCoordinatesCommand(BukkitCommandIssuer issuer,

                                     @Flags("resolve=issuerOnly")
                                     Player player,

                                     @Flags("resolve=issuerOnly")
                                         MVWorld world
    ) {
        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_TITLE);
        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_WORLD, "{world}", world.getName());
        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_ALIAS, "{alias}", world.getAlias());
        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_WORLDSCALE, "{scale}", String.valueOf(world.getScale()));
        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_COORDINATES, "{coordinates}", locationManipulation.strCoords(player.getLocation()));
        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_DIRECTION, "{direction}", locationManipulation.getDirection(player.getLocation()));
    }
}
