package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandAlias;
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
import org.mvplugins.multiverse.core.api.LocationManipulation;
import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;

@Service
@CommandAlias("mv")
class AnchorSetCommand extends MultiverseCommand {

    private final AnchorManager anchorManager;
    private final LocationManipulation locationManipulation;

    @Inject
    AnchorSetCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull AnchorManager anchorManager,
            @NotNull LocationManipulation locationManipulation) {
        super(commandManager);
        this.anchorManager = anchorManager;
        this.locationManipulation = locationManipulation;
    }

    @Subcommand("anchor set")
    @CommandPermission("multiverse.core.anchor.create")
    @CommandCompletion("@anchornames")
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
        anchorManager.saveAnchorLocation(anchorName, anchorLocation)
                .onSuccess(ignore -> issuer.sendMessage("&aAnchor &f" + anchorName + "&a set to &f"
                        + locationManipulation.locationToString(anchorLocation)))
                .onFailure(e -> issuer.sendMessage("&cFailed to set anchor &f" + anchorName + "&c." + e.getMessage()));
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
        anchorManager.saveAnchorLocation(anchorName, locationString)
                .onSuccess(anchorLocation -> issuer.sendMessage("&aAnchor &f" + anchorName + "&a set to &f"
                        + locationManipulation.locationToString(anchorLocation)))
                .onFailure(e -> issuer.sendMessage("&cFailed to set anchor '" + anchorName + "'&c. " + e.getMessage()));
    }
}
