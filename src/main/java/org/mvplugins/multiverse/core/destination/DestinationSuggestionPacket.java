package org.mvplugins.multiverse.core.destination;

import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.destination.core.WorldDestination;

/**
 * Data of a possible destination for tab completion and permission checking
 *
 * @param destination           The destination
 * @param destinationString     The destination string
 * @param finerPermissionSuffix The finer permission suffix
 */
public record DestinationSuggestionPacket(Destination<?, ?, ?> destination, String destinationString, String finerPermissionSuffix) {

    /**
     * Gets a parsable string representation of the destination that is most likely valid for
     * {@link DestinationsProvider#parseDestination(String)}.
     *
     * @return The parsable string
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public String parsableString() {
        return destination instanceof WorldDestination
                ? destinationString
                : destination.getIdentifier() + ":" + destinationString;
    }
}
