package org.mvplugins.multiverse.core.command.queue;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.result.FailureReason;

public enum RunQueuedFailedReason implements FailureReason {
    NO_COMMAND_IN_QUEUE(MVCorei18n.QUEUECOMMAND_NOCOMMANDINQUEUE),
    INVALID_OTP(MVCorei18n.QUEUECOMMAND_INVALIDOTP),
    COMMAND_EXECUTION_ERROR(MVCorei18n.QUEUECOMMAND_COMMANDEXECUTIONERROR),
    ;

    private final MessageKeyProvider message;

    RunQueuedFailedReason(MessageKeyProvider message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageKey getMessageKey() {
        return message.getMessageKey();
    }
}
