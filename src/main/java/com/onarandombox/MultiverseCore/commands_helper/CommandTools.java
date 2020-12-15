package com.onarandombox.MultiverseCore.commands_helper;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.security.pkcs.PKCS9Attribute;

import java.util.UUID;
import java.util.stream.Collectors;

public class CommandTools {
    private final MultiverseCore plugin;
    private final PaperCommandManager commandHandler;
    private final MVWorldManager worldManager;

    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}";

    public CommandTools(MultiverseCore plugin) {
        this.plugin = plugin;
        this.commandHandler = this.plugin.getCommandHandler();
        this.worldManager = this.plugin.getMVWorldManager();
    }

    public void registerCommandCompletions() {
        this.commandHandler.getCommandCompletions().registerAsyncCompletion(
                "mvworlds",
                context -> worldManager.getMVWorlds()
                        .stream()
                        .map(MultiverseWorld::getName)
                        .collect(Collectors.toList())
        );

        this.commandHandler.getCommandCompletions().registerAsyncCompletion(
                "unloadedworlds",
                context -> this.worldManager.getUnloadedWorlds()
        );

        this.commandHandler.getCommandCompletions().registerAsyncCompletion(
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
        this.commandHandler.getCommandContexts().registerIssuerAwareContext(
                WorldAndPage.class,
                this::deriveWorldAndPage
        );

        this.commandHandler.getCommandContexts().registerIssuerAwareContext(
                MultiverseWorld.class,
                this::deriveMultiverseWorld
        );

        this.commandHandler.getCommandContexts().registerIssuerAwareContext(
                Player.class,
                this::derivePlayer
        );

        this.commandHandler.getCommandContexts().registerContext(
                World.Environment.class,
                this::deriveEnvironment
        );

        //TODO: Destination
    }

    private WorldAndPage deriveWorldAndPage(@NotNull BukkitCommandExecutionContext context) {
        String eitherWorldOrPage = context.popFirstArg();
        Player player = context.getPlayer();

        if (eitherWorldOrPage == null) {
            return new WorldAndPage(
                    getPlayerWorld(context.getPlayer()),
                    1);
        }

        // Maybe its a world
        MultiverseWorld targetWorld = getMultiverseWorld(eitherWorldOrPage, true);
        if (targetWorld == null) {
            if (player == null) {
                throw new InvalidCommandArgument("World '" + eitherWorldOrPage + "' not found.");
            }
            // Safely assume its a page
            return new WorldAndPage(
                    getPlayerWorld(context.getPlayer()),
                    parsePageNumber(eitherWorldOrPage, "World '" + eitherWorldOrPage + "' not found."));
        }

        String optionalPageValue = context.popFirstArg();
        return new WorldAndPage(
                targetWorld,
                parsePageNumber(optionalPageValue, "'" + optionalPageValue + "' is not a valid page number."));
    }

    private Integer parsePageNumber(@Nullable String value, @NotNull String errorReason) {
        if (value == null) {
            return 1;
        }
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            throw new InvalidCommandArgument(errorReason);
        }
    }

    private MultiverseWorld deriveMultiverseWorld(@NotNull BukkitCommandExecutionContext context) {
        String worldName = context.popFirstArg();
        boolean worldFromPLayer = context.hasFlag("deriveFromPlayer");
        boolean checkUnloaded = context.hasFlag("checkUnloaded");

        if (worldName == null) {
            if (worldFromPLayer) {
                return getPlayerWorld(context.getPlayer());
            }
            throw new InvalidCommandArgument("Please specify a world name.");
        }

        MultiverseWorld world = getMultiverseWorld(worldName, checkUnloaded);
        if (world == null) {
            throw new InvalidCommandArgument("World '" + worldName + "' not found.");
        }

        return world;
    }

    private MultiverseWorld getMultiverseWorld(@Nullable String worldName, boolean checkUnloaded) {
        if (worldName == null) {
            return null;
        }

        MultiverseWorld targetWorld = this.worldManager.getMVWorld(worldName);
        if (targetWorld != null) {
            return targetWorld;
        }

        if (checkUnloaded && this.worldManager.getUnloadedWorlds().contains(worldName)) {
            throw new InvalidCommandArgument("World " + worldName + " exists, but you need to load it first with: /mv load");
        }

        return null;
    }

    private MultiverseWorld getPlayerWorld(@Nullable Player player) {
        if (player == null) {
            throw new InvalidCommandArgument("You need to specific a world from console.");
        }

        MultiverseWorld targetWorld = this.worldManager.getMVWorld(player.getWorld());
        if (targetWorld == null) {
            //TODO: Overload that doesnt need world name.
            this.plugin.showNotMVWorldMessage(player, player.getWorld().getName());
            throw new InvalidCommandArgument("Invalid world!");
        }

        return targetWorld;
    }

    private Player derivePlayer(@NotNull BukkitCommandExecutionContext context) {
        String playerIdentifier = context.popFirstArg();
        boolean playerFromSelf = context.hasFlag("deriveFromPlayer");

        Player targetPlayer = getPlayerFromValue(playerIdentifier);
        if (targetPlayer != null) {
            return targetPlayer;
        }

        if (!playerFromSelf) {
            throw new InvalidCommandArgument("Player '" + playerIdentifier + "' not found.");
        }

        Player self = context.getPlayer();
        if (self == null) {
            throw new InvalidCommandArgument("You need to specific a player from console.");
        }
        return self;
    }

    private Player getPlayerFromValue(@Nullable String value) {
        if (value == null) {
            return null;
        }
        Player targetPlayer = Bukkit.getPlayerExact(value);
        if (targetPlayer == null) {
            return getPlayerByUUID(value);
        }
        return targetPlayer;
    }

    private Player getPlayerByUUID(@NotNull String playerIdentifier) {
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

    private World.Environment deriveEnvironment(@NotNull BukkitCommandExecutionContext context) {
        String env = context.popFirstArg();

        if (env.equalsIgnoreCase("NORMAL") || env.equalsIgnoreCase("WORLD")) {
            env = "NORMAL";
        }
        else if (env.equalsIgnoreCase("HELL") || env.equalsIgnoreCase("NETHER")) {
            env = "NETHER";
        }
        else if (env.equalsIgnoreCase("END") || env.equalsIgnoreCase("THEEND") || env.equalsIgnoreCase("STARWARS")) {
            env = "THE_END";
        }

        try {
            return World.Environment.valueOf(env);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }
}
