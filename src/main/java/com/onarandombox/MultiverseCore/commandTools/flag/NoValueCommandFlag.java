package com.onarandombox.MultiverseCore.commandtools.flag;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

/**
 * Command Flag with {@link ValueRequirement#NONE}.
 * This flag will always not require a user input to parse value.
 *
 * @param <T>   The flag Type.
 */
public abstract class NoValueCommandFlag<T> extends CommandFlag<T> {

    public NoValueCommandFlag(String name, String identifier, Class<T> type) {
        super(name, identifier, type, ValueRequirement.NONE);
    }

    /**
     * {@link NoValueCommandFlag} will always not require a user input to parse value.
     * Thus, no value suggestion needed.
     */
    @Override
    public final Collection<String> suggestValue() {
        return Collections.emptyList();
    }

    /**
     * {@link NoValueCommandFlag} will always not require a user input to parse value.
     * Thus, this operation is not allowed.
     */
    @Override
    public final T getValue(@NotNull String input) throws FlagParseFailedException {
        throw new FlagParseFailedException("%s flag '%s' does not require a value.", this.name, this.identifier);
    }
}
