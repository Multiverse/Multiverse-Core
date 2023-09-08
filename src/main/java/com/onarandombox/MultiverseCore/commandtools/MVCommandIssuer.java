package com.onarandombox.MultiverseCore.commandtools;

import co.aikar.commands.MessageType;
import co.aikar.commands.OpenBukkitCommandIssuer;
import co.aikar.locales.MessageKeyProvider;
import com.onarandombox.MultiverseCore.utils.message.Message;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MVCommandIssuer extends OpenBukkitCommandIssuer {

    private final MVCommandManager commandManager;

    MVCommandIssuer(@NotNull MVCommandManager commandManager, @NotNull CommandSender sender) {
        super(commandManager, sender);
        this.commandManager = commandManager;
    }

    @Override
    public MVCommandManager getManager() {
        return commandManager;
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

    private void sendMessage(MessageType messageType, Message message) {
        if (message instanceof MessageKeyProvider) {
            sendMessage(messageType, (MessageKeyProvider) message, message.getReplacements());
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