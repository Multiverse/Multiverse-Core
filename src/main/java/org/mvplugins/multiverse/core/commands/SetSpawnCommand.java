package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.api.BlockSafety;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
@CommandAlias("mv")
public class SetSpawnCommand extends MultiverseCommand {

    @Inject
    SetSpawnCommand(@NotNull MVCommandManager commandManager) {
        super(commandManager);
    }

    @Subcommand("setspawn")
    @CommandPermission("multiverse.core.setspawn")
    @CommandCompletion("@nothing @mvworlds:scope=loaded ") // TODO: Use Brigadier to show <position> above in chat like the vanilla TP command
    @Syntax("[location] [world]")
    @Description("{@@mv-core.setspawn.description}")
    void onSetSpawnCommand(
            BukkitCommandIssuer issuer,

            @Optional
            @Flags("resolve=issuerAware")
            @Syntax("<location>")
            @Description("{@@mv-core.setspawn.location.description}")
            Location location,

            @Optional
            @Flags("resolve=issuerAware")
            @Syntax("<world>")
            @Description("{@@mv-core.setspawn.world.description}")
            LoadedMultiverseWorld world
    ) {

        // TODO: Use a flag to do this, no clue how to edit an inbuilt ACF flag though
        // Get the Location
        if (location == null) {
            if (issuer.isPlayer()) {
                location = issuer.getPlayer().getLocation();
            } else {
                issuer.sendMessage("The console must specify a location");
                return;
            }
        }

        issuer.sendMessage("Setting spawn in " + world.getName() + " to " + prettyLocation(location));

        world.setSpawnLocation(location);
    }

    private String prettyLocation(Location location) {
        return location.getX() + ", " + location.getY() + ", " + location.getZ();
    }
}
