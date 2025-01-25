package org.mvplugins.multiverse.core.commandtools.flag;

import co.aikar.commands.InvalidCommandArgument;

/**
 * Parses flags from a string array. Each parser should only be used once.
 */
public class CommandFlagsParser {
    private final CommandFlagGroup flagGroup;
    private final String[] flags;

    private ParsedCommandFlags parsedFlags;
    private boolean nextArgMayBeKey;
    private boolean nextArgMayBeValue;
    private CommandFlag currentFlag;

    /**
     * Creates a new CommandFlagsParser.
     *
     * @param flagGroup The flag group to parse flags for.
     * @param flags The flags to parse.
     */
    public CommandFlagsParser(CommandFlagGroup flagGroup, String[] flags) {
        this.flagGroup = flagGroup;
        this.flags = flags;
    }

    /**
     * Parses the flags.
     *
     * @return The parsed flags.
     */
    public ParsedCommandFlags parse() {
        parsedFlags = new ParsedCommandFlags();

        // First argument is always a key
        this.nextArgMayBeKey = true;
        this.nextArgMayBeValue = false;

        for (String flag : flags) {
            if (this.nextArgMayBeKey) {
                if (parseKey(flag)) continue;
            }
            if (this.nextArgMayBeValue) {
                if (parseValue(flag)) continue;
            }
            throw new InvalidCommandArgument(flag + " is not a valid flag.");
        }

        if (!this.nextArgMayBeKey && this.nextArgMayBeValue) {
            throw new InvalidCommandArgument(currentFlag.getKey() + " requires a value!");
        }

        return parsedFlags;
    }

    /**
     * Parses a key.
     *
     * @param flag The flag to parse.
     * @return True if the flag was parsed as a key, false otherwise.
     */
    private boolean parseKey(String flag) {
        CommandFlag potentialFlag = flagGroup.getFlagByKey(flag);
        if (potentialFlag == null) {
            return false;
        }

        this.currentFlag = potentialFlag;

        if (this.currentFlag instanceof CommandValueFlag) {
            CommandValueFlag<?> valueFlag = (CommandValueFlag<?>) this.currentFlag;

            if (valueFlag.isOptional()) {
                parsedFlags.addFlagResult(valueFlag.getKey(), valueFlag.getDefaultValue());
                this.nextArgMayBeKey = true;
                this.nextArgMayBeValue = true;
                return true;
            }

            this.nextArgMayBeKey = false;
            this.nextArgMayBeValue = true;
            return true;
        }

        parsedFlags.addFlagResult(this.currentFlag.getKey(), null);
        this.nextArgMayBeKey = true;
        this.nextArgMayBeValue = false;

        return true;
    }

    /**
     * Parses a value.
     *
     * @param flag The flag to parse.
     * @return True if the flag was parsed as a value, false otherwise.
     */
    private boolean parseValue(String flag) {
        if (this.currentFlag == null) {
            throw new InvalidCommandArgument("Some flag logic error occurred at " + flag + "");
        }
        if (flagGroup.hasKey(flag)) {
            throw new InvalidCommandArgument(currentFlag.getKey() + " requires a value!");
        }

        Object flagValue;
        CommandValueFlag<?> valueFlag = (CommandValueFlag<?>) this.currentFlag;
        flagValue = valueFlag.getContext() != null ? valueFlag.getContext().apply(flag) : flag;
        parsedFlags.addFlagResult(valueFlag.getKey(), flagValue);

        // After a value, the next argument must be a key
        this.nextArgMayBeKey = true;
        this.nextArgMayBeValue = false;
        this.currentFlag = null;
        return true;
    }
}
