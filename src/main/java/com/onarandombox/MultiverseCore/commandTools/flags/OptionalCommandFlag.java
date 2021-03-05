package com.onarandombox.MultiverseCore.commandtools.flags;

/**
 * Command Flag with {@link ValueRequirement#OPTIONAL}.
 *
 * @param <T>   The flag Type.
 */
public abstract class OptionalCommandFlag<T> extends CommandFlag<T> {

    public OptionalCommandFlag(String name, String identifier, Class<T> type) {
        super(name, identifier, type, ValueRequirement.OPTIONAL);
    }
}
