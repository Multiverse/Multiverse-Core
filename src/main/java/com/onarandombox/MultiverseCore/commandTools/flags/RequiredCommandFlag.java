package com.onarandombox.MultiverseCore.commandtools.flags;

/**
 * Command Flag with {@link ValueRequirement#REQUIRED}.
 * This flag will always require a user input to parse value.
 *
 * @param <T>   The flag Type.
 */
public abstract class RequiredCommandFlag<T> extends CommandFlag<T> {

    public RequiredCommandFlag(String name, String identifier, Class<T> type) {
        super(name, identifier, type, ValueRequirement.REQUIRED);
    }

    /**
     * {@link RequiredCommandFlag} will always require a user input to parse value.
     * Thus, this operation is not allowed.
     */
    @Override
    public final T getValue() throws FlagParseFailedException {
        throw new FlagParseFailedException("%s flag '%s' requires a value input.", this.name, this.identifier);
    }
}
