package com.onarandombox.MultiverseCore.commands_helper;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandContexts;
import co.aikar.commands.annotation.Values;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commands_acf.EnvironmentCommand;
import com.onarandombox.MultiverseCore.commands_acf.GeneratorCommand;
import com.onarandombox.MultiverseCore.destination.InvalidDestination;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteServiceType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
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
import java.util.Optional;
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
        registerIssuerAwareContext(Location.class, this::deriveLocation);
        registerIssuerAwareContext(PasteServiceType.class, this::derivePasteServiceType);
        registerOptionalContext(String.class, this::deriveString);
        registerOptionalContext(PageFilter.class, this::derivePageFilter);
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

    @Nullable
    private Player derivePlayer(@NotNull BukkitCommandExecutionContext context) {
        boolean mustBeSelf = context.hasFlag("onlyself");
        String error = (mustBeSelf)
                ? "You must be a player to run this command."
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

    @Nullable
    private Player getPlayerFromSelf(@NotNull BukkitCommandExecutionContext context, String errorReason) {
        Player self = context.getPlayer();
        if (self == null && !context.isOptional()) {
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
            matchedPlayers = this.plugin.getServer().selectEntities(sender, playerIdentifier).parallelStream()
                    .unordered()
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
            sender.sendMessage(ChatColor.RED + "'" + env + "' is not a valid environment.");
            EnvironmentCommand.showEnvironments(sender);
            throw new InvalidCommandArgument(false);
        }
    }

    @NotNull
    private WorldFlags deriveWorldFlags(@NotNull BukkitCommandExecutionContext context) {
        Map<String, String> flags = parseFlags(context.getArgs());
        return new WorldFlags(
                flags.keySet(),
                flags.get("-s"),
                validateGenerator(flags.get("-g"), context.getSender()),
                getWorldType(flags.get("-t"), context.getSender()),
                !flags.containsKey("-n"),
                doGenerateStructures(flags.get("-a"))
        );
    }

    @Nullable
    private String validateGenerator(@Nullable String value,
                                     @NotNull CommandSender sender) {

        if (value == null) {
            return null;
        }

        List<String> genArray = new ArrayList<>(Arrays.asList(value.split(":")));
        if (genArray.size() < 2) {
            // If there was only one arg specified, pad with another empty one.
            genArray.add("");
        }
        if (this.worldManager.getChunkGenerator(genArray.get(0), genArray.get(1), "test") == null) {
            sender.sendMessage(ChatColor.RED + "Invalid generator '" + value + "'.");
            GeneratorCommand.showAvailableGenerator(sender);
            throw new InvalidCommandArgument(false);
        }

        return value;
    }

    @NotNull
    private WorldType getWorldType(@Nullable String type,
                                   @NotNull CommandSender sender) {
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
        }
        catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "'" + type + "' is not a valid World Type.");
            EnvironmentCommand.showWorldTypes(sender);
            throw new InvalidCommandArgument(false);
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

    @Nullable
    private String deriveString(@NotNull BukkitCommandExecutionContext context) {
        if (context.hasAnnotation(Values.class)) {
            return context.popFirstArg();
        }

        String string = context.popFirstArg();
        if (string == null) {
            if (!context.isOptional()) {
                String argType = context.getFlagValue("type", "string");
                throw new InvalidCommandArgument("You need to specify a " + argType + ".");
            }
            return null;
        }

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

    private Location deriveLocation(BukkitCommandExecutionContext context) {
        if (context.getArgs().isEmpty()) {
            Player player = context.getPlayer();
            if (player != null) {
                return player.getLocation();
            }
            throw new InvalidCommandArgument("You need to specify world and coordinates from the console!");
        }

        MultiverseWorld world;
        try {
            world = deriveMultiverseWorld(context);
        }
        catch (ClassCastException e) {
            e.printStackTrace();
            throw new InvalidCommandArgument("There was an error getting Target location world!");
        }

        List<String> locationArgs = context.getArgs();
        if (locationArgs.size() != 3 && locationArgs.size() != 5) {
            context.getSender().sendMessage(ChatColor.RED + "Invalid location arguments.");
            context.getSender().sendMessage("Use no arguments for your current location, or world/x/y/z, or world/x/y/z/yaw/pitch!");
            throw new InvalidCommandArgument(true);
        }

        double x = parsePos(locationArgs.get(0), "x");
        double y = parsePos(locationArgs.get(1), "y");
        double z = parsePos(locationArgs.get(2), "z");

        double yaw = 0.0;
        double pitch = 0.0;

        if (locationArgs.size() == 5) {
            yaw = parsePos(locationArgs.get(3), "yaw");
            pitch = parsePos(locationArgs.get(4), "pitch");
        }

        return new Location(world.getCBWorld(), x, y, z, (float) yaw, (float) pitch);
    }

    private double parsePos(String value, String posType) {
        try {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException e) {
            throw new InvalidCommandArgument("'" + value + "' for "+ posType + " coordinate is not a number.", false);
        }
    }

    @NotNull
    private PasteServiceType derivePasteServiceType(BukkitCommandExecutionContext context) {
        String pasteType = context.popFirstArg();
        if (pasteType == null) {
            return PasteServiceType.NONE;
        }

        try {
            return PasteServiceType.valueOf(pasteType.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new InvalidCommandArgument("Invalid paste service type '" + pasteType + "'");
        }
    }


    @NotNull
    private PageFilter derivePageFilter(@NotNull BukkitCommandExecutionContext context) {
        final int argLength = context.getArgs().size();
        if (argLength == 0) {
            return new PageFilter(null, 1);
        }
        if (argLength == 1) {
            String pageOrFilter = context.popFirstArg();
            Optional<Integer> page = tryParseInt(pageOrFilter);
            return page.isPresent()
                    ? new PageFilter(null, page.get())
                    : new PageFilter(pageOrFilter, 1);
        }

        String filter = context.popFirstArg();
        String pageString = context.popFirstArg();
        Optional<Integer> page = tryParseInt(pageString);
        if (!page.isPresent()) {
            throw new InvalidCommandArgument("'" + pageString + "' is not a number.", false);
        }
        return new PageFilter(filter, page.get());
    }

    private Optional<Integer> tryParseInt(String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        }
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
