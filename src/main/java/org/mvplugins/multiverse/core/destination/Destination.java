package org.mvplugins.multiverse.core.destination;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Contract;
import org.mvplugins.multiverse.core.utils.result.Attempt;
import org.mvplugins.multiverse.core.utils.result.FailureReason;

/**
 * A destination is a location that can be teleported to.
 * <br />
 * Please ensure you implement at least one of {@link #getDestinationInstance(CommandSender, String)} or
 * {@link #getDestinationInstance(String)} to prevent a stack overflow.
 *
 * @param <D>   The type of the destination
 * @param <T>   The type of the destination instance
 */
@Contract
public interface Destination<D extends Destination<D, T, F>, T extends DestinationInstance<T, D>, F extends FailureReason> {
    /**
     * Returns the identifier or prefix required for this destination.
     *
     * <p>Portals have a prefix of "p" for example and OpenWarp (third party plugin) uses "ow". This is derived from a
     * hash and cannot have duplicate values. Means that your plugin cannot use 'p' because it's already used.
     * Please check the wiki when adding a custom destination!</p>
     *
     * @return The identifier or prefix required for this destination.
     */
    @NotNull String getIdentifier();

    /**
     * Returns the destination instance for the given destination parameters.
     *
     * @param destinationParams The destination parameters. ex: p:MyPortal:nw
     * @return The destination instance, or null if the destination parameters are invalid.
     *
     * @deprecated Use {@link #getDestinationInstance(CommandSender, String)} instead. This method will no longer be
     *             called by {@link DestinationsProvider}.
     */
    @Deprecated(forRemoval = true, since = "5.1")
    @ApiStatus.ScheduledForRemoval(inVersion = "6.0")
    default @NotNull Attempt<T, F> getDestinationInstance(@NotNull String destinationParams) {
        return getDestinationInstance(Bukkit.getConsoleSender(), destinationParams);
    }

    /**
     * Returns the destination instance for the given destination parameters with sender context. This allows
     * for shorthands such as getting location or name from sender. If no sender context is available, use
     * {@link Bukkit#getConsoleSender()} will be defaulted by {@link DestinationsProvider}.
     * <br />
     * Note that the resulting {@link DestinationInstance} should be (de)serializable without the original sender context.
     * <br />
     * For example, the parsable string with sender context `e:@here` should return a {@link DestinationInstance} that
     * is serialized to `e:world:x,y,z:p:y`, which can be deserialized without the original sender context.
     *
     * @param sender            The sender context.
     * @param destinationParams The destination parameters. ex: p:MyPortal:nw
     * @return The destination instance, or null if the destination parameters are invalid.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    default Attempt<T, F> getDestinationInstance(@NotNull CommandSender sender, @NotNull String destinationParams) {
        return getDestinationInstance(destinationParams);
    }

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
