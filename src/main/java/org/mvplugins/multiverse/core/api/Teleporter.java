package org.mvplugins.multiverse.core.api;

import java.util.concurrent.CompletableFuture;

import co.aikar.commands.BukkitCommandIssuer;
import org.bukkit.entity.Entity;
import org.jvnet.hk2.annotations.Contract;

import org.mvplugins.multiverse.core.destination.ParsedDestination;
import org.mvplugins.multiverse.core.teleportation.TeleportResult;

@Deprecated
@Contract
public interface Teleporter {
    /**
     * Teleport the entity to the Multiverse Destination.
     *
     * @param teleporter    Person who performed the teleport command.
     * @param teleportee    Entity to teleport
     * @param destination   Destination to teleport them to
     * @return true for success, false for failure
     */
    @Deprecated
    TeleportResult teleport(BukkitCommandIssuer teleporter, Entity teleportee, ParsedDestination<?> destination);

    /**
     * Teleport the entity to the Multiverse Destination.
     *
     * @param teleporter    Person who performed the teleport command.
     * @param teleportee    Entity to teleport
     * @param destination   Destination to teleport them to
     * @return true for success, false for failure
     */
    CompletableFuture<TeleportResult> teleportAsync(BukkitCommandIssuer teleporter, Entity teleportee, ParsedDestination<?> destination);
}
