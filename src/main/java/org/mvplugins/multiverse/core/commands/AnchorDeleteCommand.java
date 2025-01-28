package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.anchor.AnchorManager;
import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;

@Service
@CommandAlias("mv")
final class AnchorDeleteCommand extends CoreCommand {

    private final AnchorManager anchorManager;

    @Inject
    AnchorDeleteCommand(@NotNull MVCommandManager commandManager, @NotNull AnchorManager anchorManager) {
        super(commandManager);
        this.anchorManager = anchorManager;
    }

    @Subcommand("anchor delete")
    @CommandPermission("multiverse.core.anchor.delete")
    @CommandCompletion("")
    @Syntax("<name>")
    @Description("")
    void onAnchorDeleteCommand(
            MVCommandIssuer issuer,

            @Syntax("<name>")
            @Description("")
            String anchorName) {
        if (anchorManager.deleteAnchor(anchorName).isSuccess()) {
            issuer.sendMessage("&aAnchor &f" + anchorName + "&a deleted.");
        } else {
            issuer.sendMessage("&cFailed to delete anchor.");
        }
    }
}
