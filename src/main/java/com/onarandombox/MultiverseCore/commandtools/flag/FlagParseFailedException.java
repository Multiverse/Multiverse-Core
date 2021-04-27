package com.onarandombox.MultiverseCore.commandtools.flag;

/**
 * Thrown when there is an issue with parsing flags from string arguments.
 */
//TODO: extend this from ACF CommandArgumentFailed exception class.
public class FlagParseFailedException extends Exception {

    public FlagParseFailedException() {
    }

    public FlagParseFailedException(String message, Object...replacements) {
        super(String.format(message, replacements));
    }

    public FlagParseFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlagParseFailedException(Throwable cause) {
        super(cause);
    }

    public FlagParseFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
