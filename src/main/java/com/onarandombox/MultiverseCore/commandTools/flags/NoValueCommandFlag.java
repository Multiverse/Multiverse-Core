package com.onarandombox.MultiverseCore.commandtools.flags;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public abstract class NoValueCommandFlag<T> extends CommandFlag<T> {

    public NoValueCommandFlag(String name, String identifier, Class<T> type) {
        super(name, identifier, type, ValueRequirement.NONE);
    }

    @Override
    public final Collection<String> suggestValue() {
        return Collections.emptyList();
    }

    @Override
    public final T getValue(@NotNull String input) throws FlagParseFailedException {
        throw new FlagParseFailedException("%s flag '%s' does not require a value.", this.name, this.identifier);
    }
}
