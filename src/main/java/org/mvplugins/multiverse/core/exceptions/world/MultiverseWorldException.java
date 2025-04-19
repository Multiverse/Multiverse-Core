package org.mvplugins.multiverse.core.exceptions.world;

import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.exceptions.MultiverseException;
import org.mvplugins.multiverse.core.locale.message.Message;

public class MultiverseWorldException extends MultiverseException {
    public MultiverseWorldException(String message) {
        super(message);
    }

    public MultiverseWorldException(@Nullable Message message) {
        super(message);
    }

    public MultiverseWorldException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public MultiverseWorldException(@Nullable Message message, @Nullable Throwable cause) {
        super(message, cause);
    }
}
