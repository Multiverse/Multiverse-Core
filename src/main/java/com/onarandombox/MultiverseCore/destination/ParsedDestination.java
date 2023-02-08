package com.onarandombox.MultiverseCore.destination;

import com.onarandombox.MultiverseCore.api.Destination;
import com.onarandombox.MultiverseCore.api.DestinationInstance;

/**
 * A parsed destination.
 *
 * @param <S> The destination instance type.
 */
public class ParsedDestination<S extends DestinationInstance> {
    private final Destination<S> destination;
    private final DestinationInstance destinationInstance;

    /**
     * Creates a new parsed destination.
     *
     * @param destination         The destination.
     * @param destinationInstance The destination instance.
     */
    public ParsedDestination(Destination<S> destination, DestinationInstance destinationInstance) {
        this.destination = destination;
        this.destinationInstance = destinationInstance;
    }

    /**
     * Gets the destination.
     *
     * @return The destination.
     */
    public Destination<S> getDestination() {
        return destination;
    }

    /**
     * Gets the destination instance.
     *
     * @return The destination instance.
     */
    public DestinationInstance getDestinationInstance() {
        return destinationInstance;
    }

    /**
     * Converts to saveable string representation of this destination.
     *
     * @return Serialized string.
     */
    @Override
    public String toString() {
        return destination.getIdentifier() + ":" + destinationInstance.serialise();
    }
}
