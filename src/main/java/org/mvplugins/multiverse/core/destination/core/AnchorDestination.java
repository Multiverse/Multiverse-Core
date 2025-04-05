package org.mvplugins.multiverse.core.destination.core;

import java.util.Collection;

import jakarta.inject.Inject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.anchor.AnchorManager;
import org.mvplugins.multiverse.core.destination.Destination;
import org.mvplugins.multiverse.core.destination.DestinationSuggestionPacket;

/**
 * {@link Destination} implementation for anchors.
 */
@Service
public final class AnchorDestination implements Destination<AnchorDestination, AnchorDestinationInstance> {

    private final AnchorManager anchorManager;

    @Inject
    AnchorDestination(AnchorManager anchorManager) {
        this.anchorManager = anchorManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getIdentifier() {
        return "a";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable AnchorDestinationInstance getDestinationInstance(@Nullable String destinationParams) {
        if (destinationParams == null) {
            return null;
        }
        return this.anchorManager.getAnchor(destinationParams)
                .map(anchor -> new AnchorDestinationInstance(
                        this, destinationParams, anchor.getLocation()))
                .getOrNull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Collection<DestinationSuggestionPacket> suggestDestinations(
            @NotNull CommandSender sender, @Nullable String destinationParams) {
        return this.anchorManager.getAnchors(sender instanceof Player ? (Player)sender : null)
                .stream()
                .map(anchor -> new DestinationSuggestionPacket(this, anchor.getName(), anchor.getName()))
                .toList();
    }
}
