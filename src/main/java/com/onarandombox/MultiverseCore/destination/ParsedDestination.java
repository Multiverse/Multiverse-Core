package com.onarandombox.MultiverseCore.destination;

import com.onarandombox.MultiverseCore.api.Destination;
import com.onarandombox.MultiverseCore.api.DestinationInstance;

public class ParsedDestination<S extends DestinationInstance> {
    private final Destination<S> destination;
    private final DestinationInstance destinationInstance;

    public ParsedDestination(Destination<S> destination, DestinationInstance destinationInstance) {
        this.destination = destination;
        this.destinationInstance = destinationInstance;
    }

    public Destination<S> getDestination() {
        return destination;
    }

    public DestinationInstance getDestinationInstance() {
        return destinationInstance;
    }

    @Override
    public String toString() {
        return destination.getIdentifier() + ":" + destinationInstance.serialise();
    }
}
