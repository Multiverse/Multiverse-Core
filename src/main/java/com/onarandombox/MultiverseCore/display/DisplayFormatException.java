package com.onarandombox.MultiverseCore.display;

/**
 * Thrown when an issue occur while formatting content.
 */
public class DisplayFormatException extends Exception {
    public DisplayFormatException() {
    }

    public DisplayFormatException(String message) {
        super(message);
    }

    public DisplayFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public DisplayFormatException(Throwable cause) {
        super(cause);
    }

    public DisplayFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
