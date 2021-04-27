package com.onarandombox.MultiverseCore.commandtools.flag;

/**
 * Command Flag that optionally allows a user input value.
 *
 * @param <T>   The flag Type.
 */
public abstract class OptionalCommandFlag<T> extends CommandFlag<T> {

    /**
     * {@inheritDoc}
     */
    public OptionalCommandFlag(String name, String identifier, Class<T> type) {
        super(name, identifier, type);
    }
}
