package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandTools {
    private final MultiverseCore plugin;
    private final PaperCommandManager commandHandler;
    private final MVWorldManager worldManager;

    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}"
            ;

    public CommandTools(MultiverseCore plugin) {
        this.plugin = plugin;
        this.commandHandler = this.plugin.getCommandHandler();
        this.worldManager = this.plugin.getMVWorldManager();
    }

    public void registerCommandCompletions() {
        commandHandler.getCommandCompletions().registerAsyncCompletion(
                "mvworlds",
                context -> worldManager.getMVWorlds()
                        .stream()
                        .map(MultiverseWorld::getName)
                        .collect(Collectors.toList())
        );

        commandHandler.getCommandCompletions().registerAsyncCompletion(
                "unloadedmvworlds",
                context -> new ArrayList<>(this.worldManager.getUnloadedWorlds())
        );

        commandHandler.getCommandCompletions().registerAsyncCompletion(
                "mvconfig",
                context -> this.plugin.getMVConfig().serialize().keySet()
        );

        //TODO: set properties

        //TODO: add properties

        //TODO: remove properties

        //TODO: destination

        //TODO: version

        //TODO: environment

        //TODO: world types
    }

    public void registerCommandContexts() {
        commandHandler.getCommandContexts().registerIssuerAwareContext(
                MultiverseWorld.class,
                context -> {
                    String worldName = context.popFirstArg();
                    if (worldName != null) {
                        MultiverseWorld targetWorld = this.worldManager.getMVWorld(worldName);
                        if (targetWorld == null) {
                            throw new InvalidCommandArgument("World '" + worldName + "' not found.");
                        }
                        return targetWorld;
                    }

                    return GetPlayerMVWorld(context.getPlayer());
                }
        );

        commandHandler.getCommandContexts().registerContext(
                Player.class,
                context -> {
                    String playerIdentifier = context.popFirstArg();
                    Player targetPlayer = Bukkit.getPlayerExact(playerIdentifier);
                    if (targetPlayer == null) {
                        return tryGetPlayerByUUID(playerIdentifier);
                    }
                    return targetPlayer;
                }
        );

        //TODO: Destination
    }

    private Player tryGetPlayerByUUID(String playerIdentifier) {
        if (!playerIdentifier.matches(UUID_REGEX)) {
            return null;
        }
        UUID playerUUID;
        try {
            playerUUID = UUID.fromString(playerIdentifier);
        }
        catch (Exception e) {
            return null;
        }
        return Bukkit.getPlayer(playerUUID);
    }

    @NotNull
    private MultiverseWorld GetPlayerMVWorld(Player player) {
        if (player == null) {
            throw new InvalidCommandArgument("You need to specific a world from console.");
        }

        MultiverseWorld targetWorld = this.worldManager.getMVWorld(player.getWorld());
        if (targetWorld == null) {
            throw new InvalidCommandArgument("Player is not in a multiverse world.");
        }

        return targetWorld;
    }
}
