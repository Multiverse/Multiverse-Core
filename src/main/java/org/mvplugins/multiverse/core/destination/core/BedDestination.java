package org.mvplugins.multiverse.core.destination.core;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import co.aikar.commands.BukkitCommandIssuer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.destination.Destination;
import org.mvplugins.multiverse.core.utils.PlayerFinder;

/**
 * {@link Destination} implementation for beds.
 */
@Service
public class BedDestination implements Destination<BedDestination, BedDestinationInstance> {
    static final String OWN_BED_STRING = "playerbed";

    BedDestination() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getIdentifier() {
        return "b";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable BedDestinationInstance getDestinationInstance(@Nullable String destinationParams) {
        Player player = PlayerFinder.get(destinationParams);
        if (player == null && !destinationParams.equals(OWN_BED_STRING)) {
            return null;
        }
        return new BedDestinationInstance(this, player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Collection<String> suggestDestinations(@NotNull BukkitCommandIssuer issuer, @Nullable String destinationParams) {
        List<String> collect = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        collect.add(OWN_BED_STRING);
        return collect;
    }
}
