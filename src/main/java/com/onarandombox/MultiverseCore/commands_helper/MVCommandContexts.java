package com.onarandombox.MultiverseCore.commands_helper;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandContexts;
import co.aikar.commands.annotation.Values;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.destination.InvalidDestination;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class MVCommandContexts extends PaperCommandContexts {

    private final MultiverseCore plugin;
    private final MVWorldManager worldManager;

    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}";

    public MVCommandContexts(MVCommandManager manager, MultiverseCore plugin) {
        super(manager);
        this.plugin = plugin;
        this.worldManager = plugin.getMVWorldManager();

        registerIssuerAwareContext(MultiverseWorld.class, this::deriveMultiverseWorld);
        registerContext(CommandPlayer.class, this::deriveCommandPlayer);
        registerIssuerAwareContext(Player.class, this::derivePlayer);
        registerContext(World.Environment.class, this::deriveEnvironment);
        registerIssuerAwareContext(WorldFlags.class, this::deriveWorldFlags);
        registerIssuerAwareContext(GameRule.class, this::deriveGameRule);
        registerIssuerAwareContext(MVDestination.class, this::deriveMVDestination);
        registerIssuerAwareContext(String.class, this::deriveString);
    }

    @NotNull
    private MultiverseWorld deriveMultiverseWorld(@NotNull BukkitCommandExecutionContext context) {
        Player player = context.getPlayer();

        if (!context.hasFlag("other")) {
            return getPlayerWorld(player, "You cannot run this command from console.");
        }

        String worldName = context.getFirstArg();
        if (worldName == null) {
            if (context.hasFlag("defaultself")) {
                return getPlayerWorld(player, "You need to specific a world name from console.");
            }
            throw new InvalidCommandArgument("You need to specific a world name.");
        }

        MultiverseWorld world = getWorld(context.getSender(), worldName, !context.hasFlag("ignoreunload"));
        if (world == null) {
            if (context.hasFlag("fallbackself")) {
                return getPlayerWorld(player, "World '" + worldName + "' not found.");
            }
            throw new InvalidCommandArgument("World '" + worldName + "' not found.", false);
        }

        context.popFirstArg();
        return world;
    }

    @Nullable
    private MultiverseWorld getWorld(@NotNull CommandSender sender,
                                     @NotNull String worldName,
                                     boolean checkUnloaded) {

        MultiverseWorld targetWorld = this.worldManager.getMVWorld(worldName);
        if (targetWorld != null) {
            return targetWorld;
        }

        //TODO: API should have a isUnloadedWorld method.
        if (checkUnloaded && this.worldManager.getUnloadedWorlds().contains(worldName)) {
            sender.sendMessage("World '" + worldName + "' exists, but it is unloaded!");
            sender.sendMessage("You can load it with: " + ChatColor.AQUA + "/mv load " + worldName);
            throw new InvalidCommandArgument();
        }

        return null;
    }

    @NotNull
    private MultiverseWorld getPlayerWorld(@Nullable Player player, String errorReason) {
        if (player == null) {
            throw new InvalidCommandArgument(errorReason, false);
        }

        MultiverseWorld targetWorld = this.worldManager.getMVWorld(player.getWorld());
        if (targetWorld == null) {
            player.sendMessage("Multiverse doesn't know about " + ChatColor.DARK_AQUA + player.getWorld().getName() + ChatColor.WHITE + " yet.");
            player.sendMessage("Type " + ChatColor.DARK_AQUA + "/mv import ?" + ChatColor.WHITE + " for help!");
            throw new InvalidCommandArgument();
        }

        return targetWorld;
    }

    @NotNull CommandPlayer deriveCommandPlayer(@NotNull BukkitCommandExecutionContext context) {
        String playerIdentifier = context.popFirstArg();
        if (playerIdentifier == null) {
            throw new InvalidCommandArgument((context.getPlayer() == null)
                    ? "You need to specify a player from console."
                    : "You need to specify a player.");
        }

        Player player = getPlayerFromValue(context.getSender(), playerIdentifier);
        if (player == null) {
            throw new InvalidCommandArgument("Player '" + playerIdentifier + "' not found.");
        }

        return new CommandPlayer(player);
    }

    @NotNull
    private Player derivePlayer(@NotNull BukkitCommandExecutionContext context) {
        boolean mustBeSelf = context.hasFlag("onlyself");
        String error = (mustBeSelf)
                ? "You cannot run this command from console."
                : "You need to specify a player from console.";

        if (mustBeSelf || !context.hasFlag("other")) {
            return getPlayerFromSelf(context, error);
        }

        String playerIdentifier = context.getFirstArg();
        if (playerIdentifier == null) {
            if (context.hasFlag("defaultself")) {
                return getPlayerFromSelf(context, error);
            }
            throw new InvalidCommandArgument("You need to specify a player.");
        }

        Player player = getPlayerFromValue(context.getSender(), playerIdentifier);
        if (player == null) {
            if (context.hasFlag("fallbackself")) {
                return getPlayerFromSelf(context, "Player '" + playerIdentifier + "' not found.");
            }
            throw new InvalidCommandArgument("Player '" + playerIdentifier + "' not found.");
        }

        context.popFirstArg();
        return player;
    }

    @NotNull
    private Player getPlayerFromSelf(@NotNull BukkitCommandExecutionContext context, String errorReason) {
        Player self = context.getPlayer();
        if (self == null) {
            throw new InvalidCommandArgument(errorReason, false);
        }
        return self;
    }

    @Nullable
    private Player getPlayerFromValue(@NotNull CommandSender sender,
                                      @Nullable String playerIdentifier) {

        if (playerIdentifier == null) {
            return null;
        }

        Player player = getPlayerBySelector(sender, playerIdentifier);
        if (player == null) {
            player = Bukkit.getPlayerExact(playerIdentifier);
        }
        if (player == null) {
            player = getPlayerByUUID(playerIdentifier);
        }

        return player;
    }

    @Nullable
    private Player getPlayerBySelector(@NotNull CommandSender sender,
                                       @NotNull String playerIdentifier) {

        if (playerIdentifier.charAt(0) != '@') {
            return null;
        }

        List<Player> matchedPlayers;
        try {
            matchedPlayers = this.plugin.getServer().selectEntities(sender, playerIdentifier).stream()
                    .filter(e -> e instanceof Player)
                    .map(e -> ((Player) e))
                    .collect(Collectors.toList());
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new InvalidCommandArgument("Error parsing selector '" + playerIdentifier + "' for " + sender.getName());
        }
        if (matchedPlayers.isEmpty()) {
            throw new InvalidCommandArgument("No player found with selector '" + playerIdentifier + "' for " + sender.getName());
        }
        if (matchedPlayers.size() > 1) {
            throw new InvalidCommandArgument("Error parsing selector '" + playerIdentifier + "' for " + sender.getName() +
                    ": ambiguous result (more than one player matched) - " + matchedPlayers.toString());
        }

        return matchedPlayers.get(0);
    }

    @Nullable
    private Player getPlayerByUUID(@NotNull String playerIdentifier) {
        if (!playerIdentifier.matches(UUID_REGEX)) {
            return null;
        }
        UUID playerUUID;
        try {
            playerUUID = UUID.fromString(playerIdentifier);
        } catch (Exception e) {
            return null;
        }
        return Bukkit.getPlayer(playerUUID);
    }

    @NotNull
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
            CommandSender sender = context.getSender();
            sender.sendMessage("'" + env + "' is not a valid environment.");
            sender.sendMessage("For a list of available world types, see " + ChatColor.AQUA + "/mv env");
            //TODO: Possibly show valid environments.
            throw new InvalidCommandArgument();
        }
    }

    @NotNull
    private WorldFlags deriveWorldFlags(@NotNull BukkitCommandExecutionContext context) {
        Map<String, String> flags = parseFlags(context.getArgs());
        return new WorldFlags(
                flags.get("-s"),
                validateGenerator(flags.get("-g")),
                getWorldType(flags.get("-t")),
                !flags.containsKey("-n"),
                doGenerateStructures(flags.get("-a"))
        );
    }

    @Nullable
    private String validateGenerator(@Nullable String value) {
        if (value == null) {
            return null;
        }

        List<String> genArray = new ArrayList<>(Arrays.asList(value.split(":")));
        if (genArray.size() < 2) {
            // If there was only one arg specified, pad with another empty one.
            genArray.add("");
        }
        if (this.worldManager.getChunkGenerator(genArray.get(0), genArray.get(1), "test") == null) {
            throw new InvalidCommandArgument("Invalid generator '" + value + "'. See /mv gens for available generators");
        }

        return value;
    }

    @NotNull
    private WorldType getWorldType(@Nullable String type) {
        if (type == null || type.length() == 0) {
            return WorldType.NORMAL;
        }

        if (type.equalsIgnoreCase("normal")) {
            type = "NORMAL";
        }
        else if (type.equalsIgnoreCase("flat")) {
            type = "FLAT";
        }
        else if (type.equalsIgnoreCase("largebiomes")) {
            type = "LARGE_BIOMES";
        }
        else if (type.equalsIgnoreCase("amplified")) {
            type = "AMPLIFIED";
        }

        try {
            return WorldType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new InvalidCommandArgument("'" + type + "' is not a valid World Type.");
        }
    }

    private boolean doGenerateStructures(@Nullable String value) {
        return value == null || value.equalsIgnoreCase("true");
    }

    @NotNull
    private Map<String, String> parseFlags(@NotNull List<String> args) {
        Map<String, String> flags = new HashMap<>();
        if (!validateFlagArgs(args)) {
            return flags;
        }

        mapOutTheArgs(args, flags);
        return flags;
    }

    private boolean validateFlagArgs(@Nullable List<String> args) {
        if (args == null || args.size() == 0) {
            return false;
        }
        if (!isFlagKey(args.get(0))) {
            throw new InvalidCommandArgument("No flag defined for value '" + args.get(0) + "'");
        }
        return true;
    }

    private void mapOutTheArgs(@NotNull List<String> args,
                               @NotNull Map<String, String> flags) {

        String preFlagKey = args.remove(0);
        StringBuilder flagValue = new StringBuilder();

        for (String arg : args) {
            if (!isFlagKey(arg)) {
                flagValue.append(arg);
                continue;
            }
            if (preFlagKey != null) {
                flags.put(preFlagKey, flagValue.toString());
                flagValue = new StringBuilder();
            }
            preFlagKey = arg;
        }

        flags.put(preFlagKey, flagValue.toString());
    }

    private boolean isFlagKey(@NotNull String value) {
        return value.charAt(0) == '-';
    }

    @NotNull
    private GameRule deriveGameRule(@NotNull BukkitCommandExecutionContext context) {
        String rule = context.popFirstArg();
        if (rule == null) {
            throw new InvalidCommandArgument("You need to specify a gamerule.");
        }

        GameRule gameRule = GameRule.getByName(rule);
        if (gameRule == null) {
            throw new InvalidCommandArgument("'" + rule + "' is not a valid gamerule.");
        }
        return gameRule;
    }

    @NotNull
    private MVDestination deriveMVDestination(@NotNull BukkitCommandExecutionContext context) {
        String destString = context.popFirstArg();
        if (destString == null) {
            throw new InvalidCommandArgument("Please specify a destination.");
        }

        MVDestination destination = this.plugin.getDestFactory().getDestination(destString);
        if (destination instanceof InvalidDestination) {
            throw new  InvalidCommandArgument("No such destination '" + destString + "' found.");
        }
        return destination;
    }

    @NotNull
    private String deriveString(@NotNull BukkitCommandExecutionContext context) {
        if (context.hasAnnotation(Values.class)) {
            return context.popFirstArg();
        }

        String string = context.popFirstArg();
        if (context.hasFlag("trim")) {
            return trimWorldName(string);
        }

        return string;
    }

    @NotNull
    private String trimWorldName(@NotNull String worldName) {
        // Removes relative paths.
        return worldName.replaceAll("^[./\\\\]+", "");
    }
}
