package org.mvplugins.multiverse.core.api;

import java.util.Collection;

import co.aikar.commands.BukkitCommandIssuer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface Destination<T extends DestinationInstance> {
    /**
     * Returns the identifier or prefix that is required for this destination.
     *
     * <p>Portals have a prefix of "p" for example and OpenWarp (third party plugin) uses "ow". This is derived from a
     * hash and cannot have duplicate values. Read that as your plugin cannot use 'p' because it's already used.
     * Please check the wiki when adding a custom destination!</p>
     *
     * @return The identifier or prefix that is required for this destination.
     */
    @NotNull String getIdentifier();

    /**
     * Returns the destination instance for the given destination parameters.
     *
     * @param destinationParams The destination parameters. ex: p:MyPortal:nw
     * @return The destination instance, or null if the destination parameters are invalid.
     */
    @Nullable T getDestinationInstance(@Nullable String destinationParams);

    /**
     * Returns a list of possible destinations for the given destination parameters.
     *
     * @param issuer            The command issuer.
     * @param destinationParams The destination parameters. ex: p:MyPortal:nw
     * @return A list of possible destinations.
     */
    @NotNull Collection<String> suggestDestinations(@NotNull BukkitCommandIssuer issuer, @Nullable String destinationParams);

    /**
     * Should the Multiverse SafeTeleporter be used?
     *
     * <p>If not, MV will blindly take people to the location specified.</p>
     *
     * @return True if the SafeTeleporter will be used, false if not.
     */
    boolean checkTeleportSafety();

    /**
     * Returns the teleporter to use for this destination.
     *
     * <p>By default, Multiverse will automatically use SafeTeleporter. If you want to use a different teleporter, you can
     * override this method.</p>
     *
     * @return The custom teleporter to use for this destination. Return null to use the default teleporter.
     */
    @Nullable Teleporter getTeleporter();
}
