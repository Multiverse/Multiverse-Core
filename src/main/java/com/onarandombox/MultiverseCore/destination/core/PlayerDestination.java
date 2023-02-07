package com.onarandombox.MultiverseCore.destination.core;

import java.util.Collection;
import java.util.stream.Collectors;

import co.aikar.commands.BukkitCommandIssuer;
import com.onarandombox.MultiverseCore.api.Destination;
import com.onarandombox.MultiverseCore.api.Teleporter;
import com.onarandombox.MultiverseCore.utils.PlayerFinder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerDestination implements Destination<PlayerDestinationInstance> {
    @Override
    public @NotNull String getIdentifier() {
        return "pl";
    }

    @Override
    public @Nullable PlayerDestinationInstance getDestinationInstance(String destParams) {
        Player player = PlayerFinder.get(destParams);
        if (player == null) {
            return null;
        }
        return new PlayerDestinationInstance(player);
    }

    @Override
    public @NotNull Collection<String> suggestDestinations(@NotNull BukkitCommandIssuer issuer, @Nullable String destParams) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
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
