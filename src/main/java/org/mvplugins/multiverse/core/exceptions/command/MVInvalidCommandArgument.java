package org.mvplugins.multiverse.core.exceptions.command;

import co.aikar.commands.InvalidCommandArgument;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.locale.message.LocalizableMessage;
import org.mvplugins.multiverse.core.locale.message.LocalizedMessage;
import org.mvplugins.multiverse.core.locale.message.Message;

/**
 * ACF's InvalidCommandArgument with added support for MV's LocalizedMessage class.
 */
public class MVInvalidCommandArgument extends InvalidCommandArgument {

    @ApiStatus.AvailableSince("5.7")
    public static MVInvalidCommandArgument causeBy(Throwable throwable) {
        return causeBy(throwable, true);
    }

    @ApiStatus.AvailableSince("5.7")
    public static MVInvalidCommandArgument causeBy(Throwable throwable, boolean showSyntax) {
        if (throwable instanceof LocalizableMessage localizableMessage) {
            Message message = localizableMessage.getLocalizableMessage();
            return message == null
                    ? new MVInvalidCommandArgument(throwable.getLocalizedMessage(), showSyntax)
                    : of(message, showSyntax);
        }
        return new MVInvalidCommandArgument(throwable.getLocalizedMessage(), showSyntax);
    }

    public static MVInvalidCommandArgument of(@NotNull Message message) {
        return of(message, true);
    }

    public static MVInvalidCommandArgument of(@NotNull Message message, boolean showSyntax) {
        return message instanceof LocalizedMessage
                ? new MVInvalidCommandArgument((LocalizedMessage) message, showSyntax)
                : new MVInvalidCommandArgument(message, showSyntax);
    }

    private MVInvalidCommandArgument(String message, boolean showSyntax) {
        super(message, showSyntax);
    }

    private MVInvalidCommandArgument(Message message, boolean showSyntax) {
        super(message.formatted(), showSyntax);
    }

    private MVInvalidCommandArgument(LocalizedMessage message, boolean showSyntax) {
        super(message.getMessageKey(), showSyntax, message.getRawReplacements());
    }
}
