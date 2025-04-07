package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
class SetSpawnCommand extends CoreCommand {

    private final WorldManager worldManager;

    @Inject
    SetSpawnCommand(@NotNull WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @CommandAlias("mvsetspawn")
    @Subcommand("setspawn")
    @CommandPermission("multiverse.core.spawn.set")
    @Syntax("[x],[y],[z],[pitch],[yaw]")
    @Description("{@@mv-core.setspawn.description}")
    void onSetSpawnCommand(
            BukkitCommandIssuer issuer,

            @Optional
            @Syntax("<location>")
            @Description("{@@mv-core.setspawn.location.description}")
            Location location) {
        Option.of(location).orElse(() -> {
                    if (issuer.isPlayer()) {
                        return Option.of(issuer.getPlayer().getLocation());
                    }
                    return Option.none();
                }).peek(finalLocation ->
                        worldManager.getLoadedWorld(finalLocation.getWorld())
                                .peek(mvWorld -> mvWorld.setSpawnLocation(finalLocation)
                                        .onSuccess(ignore -> issuer.sendMessage(
                                                "Successfully set spawn in " + mvWorld.getName() + " to "
                                                        + prettyLocation(mvWorld.getSpawnLocation())))
                                        .onFailure(e -> issuer.sendMessage(e.getLocalizedMessage())))
                                .onEmpty(() -> issuer.sendMessage("That world is not loaded or does not exist!")))
                .onEmpty(() -> issuer.sendMessage("You must specify a location in the format: worldname:x,y,z"));
    }

    private String prettyLocation(Location location) {
        return location.getX() + ", " + location.getY() + ", " + location.getZ() + ". pitch:" + location.getPitch()
                + ", yaw:" + location.getYaw();
    }

    @Service
    private static final class LegacyAlias extends SetSpawnCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(@NotNull WorldManager worldManager) {
            super(worldManager);
        }

        @Override
        @CommandAlias("mvss")
        void onSetSpawnCommand(BukkitCommandIssuer issuer, Location location) {
            super.onSetSpawnCommand(issuer, location);
        }
    }
}
