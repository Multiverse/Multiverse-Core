package com.onarandombox.MultiverseCore.destination.core;

import java.util.Collection;

import co.aikar.commands.BukkitCommandIssuer;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.Destination;
import com.onarandombox.MultiverseCore.api.Teleporter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnchorDestination implements Destination<AnchorDestinationInstance> {
    private final MultiverseCore plugin;

    public AnchorDestination(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "a";
    }

    @Nullable
    @Override
    public AnchorDestinationInstance getDestinationInstance(String destinationParams) {
        Location anchorLocation = this.plugin.getAnchorManager().getAnchorLocation(destinationParams);
        if (anchorLocation == null) {
            return null;
        }
        return new AnchorDestinationInstance(destinationParams, anchorLocation);
    }

    @Override
    public @NotNull Collection<String> suggestDestinations(@NotNull BukkitCommandIssuer issuer, @Nullable String destinationParams) {
        return this.plugin.getAnchorManager().getAnchors(issuer.getPlayer());
    }

    @Override
    public boolean checkTeleportSafety() {
        return true;
    }

    @Override
    public @Nullable Teleporter getTeleporter() {
        return null;
    }
}
