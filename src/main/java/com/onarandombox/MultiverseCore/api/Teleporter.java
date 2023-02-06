package com.onarandombox.MultiverseCore.api;

import co.aikar.commands.BukkitCommandIssuer;
import com.onarandombox.MultiverseCore.destination.ParsedDestination;
import com.onarandombox.MultiverseCore.enums.TeleportResult;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface Teleporter {
    TeleportResult teleport(CommandSender teleporter, Player teleportee, MVDestination destination);

    TeleportResult teleport(BukkitCommandIssuer teleporter, Entity teleportee, ParsedDestination<?> destination);
}
