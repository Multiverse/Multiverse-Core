package com.onarandombox.MultiverseCore.commandtools.flag;

import co.aikar.commands.InvalidCommandArgument;

public class FlagParseFailedException extends InvalidCommandArgument {

    public FlagParseFailedException() {
    }

    public FlagParseFailedException(String message, Object...replacements) {
        super(String.format(message, replacements), true);
    }
}
