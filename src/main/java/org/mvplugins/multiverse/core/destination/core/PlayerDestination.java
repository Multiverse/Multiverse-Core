package org.mvplugins.multiverse.core.destination.core;

import java.util.Collection;

import co.aikar.commands.BukkitCommandIssuer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.destination.Destination;
import org.mvplugins.multiverse.core.destination.DestinationSuggestionPacket;
import org.mvplugins.multiverse.core.utils.PlayerFinder;

/**
 * {@link Destination} implementation for players.s
 */
@Service
public class PlayerDestination implements Destination<PlayerDestination, PlayerDestinationInstance> {
    /**
     * Creates a new instance of the PlayerDestination.
     */
    PlayerDestination() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getIdentifier() {
        return "pl";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable PlayerDestinationInstance getDestinationInstance(@Nullable String destinationParams) {
        Player player = PlayerFinder.get(destinationParams);
        if (player == null) {
            return null;
        }
        return new PlayerDestinationInstance(this, player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Collection<DestinationSuggestionPacket> suggestDestinations(
            @NotNull CommandSender sender, @Nullable String destinationParams) {
        return Bukkit.getOnlinePlayers().stream()
                .map(p -> new DestinationSuggestionPacket(p.getName(), p.getName()))
                .toList();
    }
}
