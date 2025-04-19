package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.bukkit.entity.SpawnCategory;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.utils.StringFormatter;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.entity.EntityPurger;

import java.util.List;

@Service
final class PurgeAllEntitiesCommand extends CoreCommand {

    private final EntityPurger entityPurger;

    @Inject
    PurgeAllEntitiesCommand(EntityPurger entityPurger) {
        this.entityPurger = entityPurger;
    }

    @Subcommand("purge-all-entities")
    @CommandPermission("multiverse.core.purgeall")
    @CommandCompletion("@mvworlds:scope=loaded|@spawncategories:multiple,resolveUntil=arg1 @spawncategories:multiple")
    @Syntax("[world] [spawn-categories]")
    void onPurgeAllEntitiesCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("[world]")
            LoadedMultiverseWorld world,

            @Optional
            @Syntax("[spawn-categories]")
            SpawnCategory[] spawnCategories
    ) {
        if (spawnCategories == null || spawnCategories.length == 0) {
            int purgeCount = entityPurger.purgeAllEntities(world);
            issuer.sendMessage("Successfully purged " + purgeCount + " entities in world " + world.getName() + ".");
            return;
        }

        int purgeCount = entityPurger.purgeEntities(world, spawnCategories);
        issuer.sendMessage("Successfully purged " + purgeCount + " entities in world " + world.getName() +
                " for spawn categories " + StringFormatter.join(List.of(spawnCategories), ", ") + ".");
    }
}
