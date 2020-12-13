package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CommandTools {
    private final MultiverseCore plugin;
    private final PaperCommandManager commandHandler;
    private final MVWorldManager worldManager;

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

    public void registerCommandContext() {
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

        commandHandler.getCommandContexts().registerIssuerAwareContext(
                PageOrWorld.class,
                context -> {
                    String arg = context.popFirstArg();
                    MultiverseWorld targetWorld = this.worldManager.getMVWorld(arg);
                    if (targetWorld != null) {
                        return new PageOrWorld(targetWorld);
                    }

                    targetWorld = GetPlayerMVWorld(context.getPlayer());
                    int page = ParsePageNumber(arg);

                    return new PageOrWorld(targetWorld, page);
                }
        );

        //TODO: Destination
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

    private int ParsePageNumber(String arg) {
        if (arg == null) {
            return 1;
        }

        try {
            return Integer.parseInt(arg);
        }
        catch (NumberFormatException ignored) {
            throw new InvalidCommandArgument("Invalid page number: " + arg);
        }
    }
}
