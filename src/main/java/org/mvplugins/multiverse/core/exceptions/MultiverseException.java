package org.mvplugins.multiverse.core.exceptions;

import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.locale.message.Message;

/**
 * A base exception for Multiverse.
 * <br/>
 * {@link #getMVMessage()} provides access to a {@link Message} which can be used to provide a localized message.
 */
public class MultiverseException extends Exception {

    private final transient @Nullable Message message;

    /**
     * Creates a new exception with the given message.
     * <br/>
     * If the message is not null, this exception will also contain a {@link Message} which can be accessed via
     * {@link #getMVMessage()}. This message will just be the given message wrapped in a {@link Message}.
     *
     * @param message   The message for the exception
     */
    public MultiverseException(@Nullable String message) {
        super(message);
        this.message = message != null ? Message.of(message) : null;
    }

    /**
     * Creates a new exception with the given message.
     * <br/>
     * If the message is not null, this exception will also contain a String message which can be accessed via
     * {@link #getMessage()}. This message will just be the given message formatted without locale support.
     *
     * @param message   The message for the exception
     */
    public MultiverseException(@Nullable Message message) {
        super(message != null ? message.formatted() : null);
        this.message = message;
    }

    /**
     * Creates a new exception with the given message and cause.
     * <br/>
     * If the message is not null, this exception will also contain a {@link Message} which can be accessed via
     * {@link #getMVMessage()}. This message will just be the given message wrapped in a {@link Message}.
     *
     * @param message The message for the exception
     * @param cause The cause of the exception
     */
    public MultiverseException(@Nullable String message, @Nullable Throwable cause) {
        this(message != null ? Message.of(message) : null, cause);
    }

    /**
     * Creates a new exception with the given message and cause.
     * <br/>
     * If the message is not null, this exception will also contain a String message which can be accessed via
     * {@link #getMessage()}. This message will just be the given message formatted without locale support.
     *
     * @param message The message for the exception
     * @param cause The cause of the exception
     */
    public MultiverseException(@Nullable Message message, @Nullable Throwable cause) {
        super(message != null ? message.formatted() : null, cause);
        this.message = message;
    }

    /**
     * Gets the {@link Message} for this exception.
     *
     * @return The message, or null if none was provided
     */
    public final @Nullable Message getMVMessage() {
        return message;
    }
}
