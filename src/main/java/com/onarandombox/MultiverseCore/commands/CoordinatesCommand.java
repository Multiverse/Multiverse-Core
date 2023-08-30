package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.onarandombox.MultiverseCore.api.LocationManipulation;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
@CommandAlias("mv")
public class CoordinatesCommand extends MultiverseCommand {

    private final MVWorldManager worldManager;
    private final LocationManipulation locationManipulation;

    @Inject
    public CoordinatesCommand(@NotNull MVCommandManager commandManager, @NotNull MVWorldManager worldManager, @NotNull LocationManipulation locationManipulation) {
        super(commandManager);

        this.worldManager = worldManager;
        this.locationManipulation = locationManipulation;
    }

    @Subcommand("coordinates|coord|coords|co")
    @CommandPermission("multiverse.core.coordinates")
    @Description("{@@mv-core.coordinates.description}")
    public void onCoordinatesCommand(BukkitCommandIssuer issuer) {
        if (!issuer.isPlayer()) { // Players only
            issuer.sendInfo(MVCorei18n.COORDINATES_ERRORPLAYERSONLY);
            return;
        }

        Player player = issuer.getPlayer();

        if (!this.worldManager.isMVWorld(player.getWorld().getName())) { // MV Worlds only
            issuer.sendInfo(MVCorei18n.COORDINATES_ERROR_MULTIVERSEWORLDONLY);
            return;
        }
        World world = player.getWorld();
        MVWorld mvworld = this.worldManager.getMVWorld(world.getName());

        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_TITLE);
        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_WORLD, "{world}", world.getName());
        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_ALIAS, "{alias}", mvworld.getColoredWorldString());
        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_WORLDSCALE, "{scale}", String.valueOf(mvworld.getScaling()));
        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_COORDINATES, "{coordinates}", locationManipulation.strCoords(player.getLocation()));
        issuer.sendInfo(MVCorei18n.COORDINATES_INFO_DIRECTION, "{direction}", locationManipulation.getDirection(player.getLocation()));
    }
}
