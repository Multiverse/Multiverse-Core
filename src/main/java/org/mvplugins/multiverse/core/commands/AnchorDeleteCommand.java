package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.anchor.AnchorManager;
import org.mvplugins.multiverse.core.anchor.MultiverseAnchor;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;

@Service
final class AnchorDeleteCommand extends CoreCommand {

    private final AnchorManager anchorManager;

    @Inject
    AnchorDeleteCommand(@NotNull AnchorManager anchorManager) {
        this.anchorManager = anchorManager;
    }

    @Subcommand("anchor delete")
    @CommandPermission("multiverse.core.anchor.delete")
    @CommandCompletion("@anchornames")
    @Syntax("<name>")
    @Description("")
    void onAnchorDeleteCommand(
            MVCommandIssuer issuer,

            @Syntax("<name>")
            @Description("")
            MultiverseAnchor anchor) {
        if (anchorManager.deleteAnchor(anchor).isSuccess()) {
            issuer.sendMessage("&aAnchor &f" + anchor.getName() + "&a deleted.");
        } else {
            issuer.sendMessage("&cFailed to delete anchor.");
        }
    }
}
