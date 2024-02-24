package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
@CommandAlias("mv")
class SpawnCommand extends MultiverseCommand {

    private final WorldManager worldManager;

    @Inject
    SpawnCommand(@NotNull MVCommandManager commandManager, WorldManager worldManager) {
        super(commandManager);
        this.worldManager = worldManager;
    }

    @Subcommand("spawn")
    @CommandPermission("multiverse.core.spawn")
    @CommandCompletion("@players")
    @Syntax("[player]")
    @Description("{@@mv-core.spawn.description}")
    void onSpawnCommand(
            BukkitCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("[player]")
            @Description("{@@mv-core.spawn.player.description}")
            Player player
           ) {
        // The player is in the world, so it must be loaded
        LoadedMultiverseWorld world = worldManager.getLoadedWorld(player.getWorld().getName()).getOrNull();

        player.teleport(world.getSpawnLocation()); // TODO: use safety teleporter

        // Make the message make sense
        String teleporterName;
        if (issuer.getIssuer().getName().equals("CONSOLE")) {
            teleporterName = "The console";
        } else if (issuer.getIssuer().getName().equals(player.getName())) {
            teleporterName = "You";
        } else {
            teleporterName = issuer.getIssuer().getName();
        }

        player.sendMessage(teleporterName + " just sent you to spawn!"); // TODO: i18n


    }


}
