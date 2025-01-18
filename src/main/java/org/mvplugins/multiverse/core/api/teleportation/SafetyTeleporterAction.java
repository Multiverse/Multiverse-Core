package org.mvplugins.multiverse.core.api.teleportation;

import co.aikar.commands.BukkitCommandIssuer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Contract;
import org.mvplugins.multiverse.core.api.result.Async;
import org.mvplugins.multiverse.core.api.result.AsyncAttempt;
import org.mvplugins.multiverse.core.api.result.Attempt;

import java.util.List;

/**
 * Teleports one or more entity safely to a location.
 *
 * @since 5.0
 */
@Contract
public interface SafetyTeleporterAction {
    /**
     * Sets whether to check for safe location before teleport.
     *
     * @param checkSafety Whether to check for safe location
     * @return A new {@link SafetyTeleporterAction} to be chained
     * @since 5.0
     */
    SafetyTeleporterAction checkSafety(boolean checkSafety);

    /**
     * Sets the teleporter.
     *
     * @param issuer The issuer
     * @return A new {@link SafetyTeleporterAction} to be chained
     * @since 5.0
     */
    SafetyTeleporterAction by(@NotNull BukkitCommandIssuer issuer);

    /**
     * Sets the teleporter.
     *
     * @param teleporter The teleporter
     * @return A new {@link SafetyTeleporterAction} to be chained
     * @since 5.0
     */
    SafetyTeleporterAction by(@NotNull CommandSender teleporter);

    /**
     * Teleport multiple entities
     *
     * @param teleportees The entities to teleport
     * @param <T>
     * @return A list of async futures that represent the teleportation result of each entity
     * @since 5.0
     */
    <T extends Entity> Async<List<Attempt<Void, TeleportFailureReason>>> teleport(@NotNull List<T> teleportees);

    /**
     * Teleports one entity
     *
     * @param teleportee The entity to teleport
     * @return An async future that represents the teleportation result
     * @since 5.0
     */
    AsyncAttempt<Void, TeleportFailureReason> teleport(@NotNull Entity teleportee);
}
