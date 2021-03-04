package com.onarandombox.MultiverseCore.commandTools.flags;

public abstract class RequiredCommandFlag<T> extends CommandFlag<T> {

    public RequiredCommandFlag(String name, String identifier, Class<T> type) {
        super(name, identifier, type, ValueRequirement.REQUIRED);
    }

    @Override
    public final T getValue() throws FlagParseFailedException {
        throw new FlagParseFailedException("%s flag '%s' requires a value input.", this.name, this.identifier);
    }
}
