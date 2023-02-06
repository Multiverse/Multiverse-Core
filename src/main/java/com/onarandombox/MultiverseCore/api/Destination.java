package com.onarandombox.MultiverseCore.api;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Destination<T extends DestinationInstance> {
    @NotNull String getIdentifier();

    @Nullable T getDestinationInstance(String destParams);

    @NotNull Collection<String> suggestDestinations(@Nullable String destParams);

    boolean checkTeleportSafety();

    @Nullable Teleporter getTeleporter();
}
