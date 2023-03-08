package com.onarandombox.MultiverseCore.api;

import co.aikar.commands.BukkitCommandIssuer;
import com.onarandombox.MultiverseCore.destination.ParsedDestination;
import com.onarandombox.MultiverseCore.teleportation.TeleportResult;
import org.bukkit.entity.Entity;
import org.jvnet.hk2.annotations.Contract;

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
    TeleportResult teleport(BukkitCommandIssuer teleporter, Entity teleportee, ParsedDestination<?> destination);
}
