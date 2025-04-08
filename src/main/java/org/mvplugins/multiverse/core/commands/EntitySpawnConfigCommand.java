package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.bukkit.entity.SpawnCategory;
import org.jvnet.hk2.annotations.Optional;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.config.handle.PropertyModifyAction;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
@Subcommand("entity-spawn-config")
final class EntitySpawnConfigCommand extends CoreCommand {

    private final WorldManager worldManager;

    @Inject
    EntitySpawnConfigCommand(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @Subcommand("info")
    @CommandPermission("multiverse.core.mobsspawnconfig")
    @CommandCompletion("@mvworlds:scope=both")
    @Syntax("[world]")
    @Description("")
    void onInfoCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("[world]")
            MultiverseWorld world
    ) {
        issuer.sendMessage(world.getMobsSpawnConfig().toString());
    }

    @Subcommand("modify")
    @CommandPermission("multiverse.core.mobsspawnconfig")
    @CommandCompletion("@mvworlds:scope=both @spawncategories @propsmodifyaction @spawncategorypropsname @spawncategorypropsvalue")
    @Syntax("[world(s)] <spawn-category> <set|add|reset|remove> <property> [value]")
    @Description("")
    void onModifyCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("[world(s)]")
            MultiverseWorld world,

            @Syntax("<spawn-category>")
            SpawnCategory spawnCategory,

            @Syntax("<set|add|reset|remove>")
            PropertyModifyAction action,

            @Syntax("<property")
            String property,

            @Single
            @Optional
            @Syntax("[value]")
            String value
    ) {
        world.getMobsSpawnConfig()
                .getSpawnCategoryConfig(spawnCategory)
                .getStringPropertyHandle()
                .modifyPropertyString(property, value, action)
                .onSuccess(ignore -> issuer.sendMessage("Successfully set " + property + " to " + value
                        + " for " + spawnCategory.name() + " in " + world.getName()))
                .onFailure(e -> issuer.sendMessage("Unable to set " + property + " to " + value
                        + " for " + spawnCategory.name() + " in " + world.getName() + ": " + e.getMessage()));

        worldManager.saveWorldsConfig();
    }
}
