package org.mvplugins.multiverse.core.command;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.MessageType;
import co.aikar.locales.MessageKeyProvider;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement;

public class MVCommandIssuer extends BukkitCommandIssuer {

    private final MVCommandManager commandManager;

    MVCommandIssuer(@NotNull MVCommandManager commandManager, @NotNull CommandSender sender) {
        super(commandManager, sender);
        this.commandManager = commandManager;
    }

    @Override
    public MVCommandManager getManager() {
        return commandManager;
    }

    public void sendError(String message, MessageReplacement... replacements) {
        sendMessage(MessageType.INFO, message, replacements);
    }

    public void sendSyntax(String message, MessageReplacement... replacements) {
        sendMessage(MessageType.SYNTAX, message, replacements);
    }

    public void sendInfo(String message, MessageReplacement... replacements) {
        sendMessage(MessageType.INFO, message, replacements);
    }

    public void sendMessage(String message, MessageReplacement... replacements) {
        sendMessage(null, Message.of(message, replacements));
    }

    public void sendMessage(MessageType type, String message, MessageReplacement... replacements) {
        sendMessage(type, Message.of(message, replacements));
    }

    public void sendMessage(MessageType messageType, String message) {
        var formatter = getManager().getFormat(messageType);
        if (formatter != null) {
            sendMessage(formatter.format(message));
        } else {
            sendMessage(message);
        }
    }

    public void sendError(MessageKeyProvider key) {
        sendMessage(MessageType.ERROR, key, new String[0]);
    }

    public void sendSyntax(MessageKeyProvider key) {
        sendMessage(MessageType.SYNTAX, key, new String[0]);
    }

    public void sendInfo(MessageKeyProvider key) {
        sendMessage(MessageType.INFO, key, new String[0]);
    }

    public void sendError(MessageKeyProvider key, MessageReplacement... replacements) {
        sendMessage(MessageType.ERROR, key, replacements);
    }

    public void sendSyntax(MessageKeyProvider key, MessageReplacement... replacements) {
        sendMessage(MessageType.SYNTAX, key, replacements);
    }

    public void sendInfo(MessageKeyProvider key, MessageReplacement... replacements) {
        sendMessage(MessageType.INFO, key, replacements);
    }
    public void sendMessage(MessageKeyProvider key, MessageReplacement... replacements) {
        sendMessage(null, Message.of(key, replacements));
    }

    public void sendMessage(MessageType messageType, MessageKeyProvider key, MessageReplacement... replacements) {
        sendMessage(messageType, Message.of(key, replacements));
    }

    public void sendError(Message message) {
        sendMessage(MessageType.ERROR, message);
    }

    public void sendSyntax(Message message) {
        sendMessage(MessageType.SYNTAX, message);
    }

    public void sendInfo(Message message) {
        sendMessage(MessageType.INFO, message);
    }

    public void sendMessage(Message message) {
        sendMessage(null, message);
    }

    public void sendMessage(MessageType messageType, Message message) {
        if (message instanceof MessageKeyProvider) {
            sendMessage(messageType, (MessageKeyProvider) message,
                    message.getReplacements(getManager().getLocales(), this));
        } else {
            var formatter = getManager().getFormat(messageType);
            if (formatter != null) {
                sendMessage(formatter.format(message.formatted()));
            } else {
                sendMessage(message.formatted());
            }
        }
    }
}
