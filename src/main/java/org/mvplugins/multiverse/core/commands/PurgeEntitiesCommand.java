package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.entity.EntityPurger;

@Service
final class PurgeEntitiesCommand extends CoreCommand {

    private final EntityPurger entityPurger;

    @Inject
    PurgeEntitiesCommand(EntityPurger entityPurger) {
        this.entityPurger = entityPurger;
    }

    @Subcommand("purge-entities")
    @CommandPermission("multiverse.core.purge")
    @CommandCompletion("@mvworlds:scope=loaded")
    @Syntax("[world]")
    void onPurgeEntityCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("[world]")
            LoadedMultiverseWorld world
    ) {
        int purgeCount = entityPurger.purgeEntities(world);
        issuer.sendMessage("Successfully purged " + purgeCount + " entities in world " + world.getName() + ".");
    }
}
