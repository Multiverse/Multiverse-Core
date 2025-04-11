package org.mvplugins.multiverse.core.exceptions.command;

import co.aikar.commands.InvalidCommandArgument;
import org.mvplugins.multiverse.core.locale.message.LocalizedMessage;
import org.mvplugins.multiverse.core.locale.message.Message;

/**
 * ACF's InvalidCommandArgument with added support for MV's LocalizedMessage class.
 */
public class MVInvalidCommandArgument extends InvalidCommandArgument {

    public static MVInvalidCommandArgument of(Message message) {
        return of(message, true);
    }

    public static MVInvalidCommandArgument of(Message message, boolean showSyntax) {
        return message instanceof LocalizedMessage
                ? new MVInvalidCommandArgument((LocalizedMessage) message, showSyntax)
                : new MVInvalidCommandArgument(message, showSyntax);
    }

    private MVInvalidCommandArgument(Message message, boolean showSyntax) {
        super(message.formatted(), showSyntax);
    }

    private MVInvalidCommandArgument(LocalizedMessage message, boolean showSyntax) {
        super(message.getMessageKey(), showSyntax, message.getReplacements());
    }
}
