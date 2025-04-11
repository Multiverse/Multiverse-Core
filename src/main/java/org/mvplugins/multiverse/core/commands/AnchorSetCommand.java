package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.anchor.AnchorManager;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.teleportation.LocationManipulation;

@Service
final class AnchorSetCommand extends CoreCommand {

    private final AnchorManager anchorManager;
    private final LocationManipulation locationManipulation;

    @Inject
    AnchorSetCommand(
            @NotNull AnchorManager anchorManager,
            @NotNull LocationManipulation locationManipulation
    ) {
        this.anchorManager = anchorManager;
        this.locationManipulation = locationManipulation;
    }

    @Subcommand("anchor set")
    @CommandPermission("multiverse.core.anchor.create")
    @CommandCompletion("@empty @locations")
    @Syntax("<name> [location]")
    @Description("")
    void onAnchorSetCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerOnly")
            Player player,

            @Single
            @Syntax("<name>")
            @Description("")
            //TODO: Check if anchor name is valid or exists already.
            String anchorName) {
        Location anchorLocation = player.getLocation();
        if (anchorManager.setAnchor(anchorName, anchorLocation).isSuccess()) {
            sendAnchorSetSuccessMessage(issuer, anchorName, locationManipulation.locationToString(anchorLocation));
        } else {
            sendAnchorSetFailedMessage(issuer, anchorName);
        }
    }

    @Subcommand("anchor set")
    @CommandPermission("multiverse.core.anchor.create")
    @CommandCompletion("@anchornames @locations")
    @Syntax("<name> [location]")
    @Description("")
    void onAnchorSetCommand(
            MVCommandIssuer issuer,

            @Syntax("<name>")
            @Description("")
            String anchorName,

            @Single
            @Syntax("[location]")
            @Description("")
            String locationString) {
        if (anchorManager.setAnchor(anchorName, locationString).isSuccess()) {
            sendAnchorSetSuccessMessage(issuer, anchorName, locationString);
        } else {
            sendAnchorSetFailedMessage(issuer, anchorName);
        }
    }

    private void sendAnchorSetSuccessMessage(MVCommandIssuer issuer, String anchorName, String locationString) {
        issuer.sendMessage("&aAnchor &f" + anchorName + "&a set to &f" + locationString);
    }

    private void sendAnchorSetFailedMessage(MVCommandIssuer issuer, String anchorName) {
        issuer.sendMessage("&cFailed to set anchor &f" + anchorName + ".");
    }
}
