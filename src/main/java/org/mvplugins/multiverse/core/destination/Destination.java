package org.mvplugins.multiverse.core.destination;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Contract;
import org.mvplugins.multiverse.core.utils.result.Attempt;
import org.mvplugins.multiverse.core.utils.result.FailureReason;

/**
 * A destination is a location that can be teleported to.
 *
 * @param <D>   The type of the destination
 * @param <T>   The type of the destination instance
 */
@Contract
public interface Destination<D extends Destination<D, T, F>, T extends DestinationInstance<T, D>, F extends FailureReason> {
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
    @NotNull Attempt<T, F> getDestinationInstance(@NotNull String destinationParams);

    /**
     * Returns a list of possible destinations for the given destination parameters. This packet's destination
     * should be this instance and not other destinations.
     *
     * @param commandSender     The command sender
     * @param destinationParams The destination parameters. ex: p:MyPortal:nw
     * @return A list of possible destinations
     */
    @NotNull
     Collection<DestinationSuggestionPacket> suggestDestinations(
            @NotNull CommandSender commandSender, @Nullable String destinationParams);
}
