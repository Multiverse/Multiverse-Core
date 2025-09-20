package org.mvplugins.multiverse.core.exceptions.utils.position;

import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.exceptions.MultiverseException;
import org.mvplugins.multiverse.core.locale.message.Message;

public class PositionParseException extends MultiverseException {
    public PositionParseException(String message) {
        super(message);
    }

    public PositionParseException(@Nullable Message message) {
        super(message);
    }

    public PositionParseException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public PositionParseException(@Nullable Message message, @Nullable Throwable cause) {
        super(message, cause);
    }
}
