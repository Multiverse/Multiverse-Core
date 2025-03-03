package org.mvplugins.multiverse.core.utils.webpaste;

/**
 * Thrown when pasting fails.
 */
public class PasteFailedException extends Exception {
    public PasteFailedException() {
        super();
    }

    public PasteFailedException(Throwable cause) {
        super(cause);
    }
}
