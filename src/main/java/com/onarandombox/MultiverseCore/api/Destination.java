package com.onarandombox.MultiverseCore.api;

import java.util.Collection;

import co.aikar.commands.BukkitCommandIssuer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Destination<T extends DestinationInstance> {
    @NotNull String getIdentifier();

    @Nullable T getDestinationInstance(String destinationParams);

    @NotNull Collection<String> suggestDestinations(@NotNull BukkitCommandIssuer issuer, @Nullable String destinationParams);

    boolean checkTeleportSafety();

    @Nullable Teleporter getTeleporter();
}
