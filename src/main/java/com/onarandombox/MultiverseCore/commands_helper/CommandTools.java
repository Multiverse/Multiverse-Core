package com.onarandombox.MultiverseCore.commands_helper;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.enums.AddProperties;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandTools {
    private final MultiverseCore plugin;
    private final PaperCommandManager commandHandler;
    private final MVWorldManager worldManager;

    private static final Set<String> BLACKLIST_WORLD_FOLDER = Stream.of("plugins", "cache", "logs").collect(Collectors.toCollection(HashSet::new));
    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}";

    public CommandTools(MultiverseCore plugin) {
        this.plugin = plugin;
        this.commandHandler = this.plugin.getCommandHandler();
        this.worldManager = this.plugin.getMVWorldManager();
    }

    public void registerCommandCompletions() {
        this.commandHandler.getCommandCompletions().registerAsyncCompletion(
                "MVWorlds",
                this::suggestMVWorlds
        );

        this.commandHandler.getCommandCompletions().registerAsyncCompletion(
                "unloadedWorlds",
                this::suggestUnloadedWorlds
        );

        this.commandHandler.getCommandCompletions().registerAsyncCompletion(
                "potentialWorlds",
                this::suggestPotentialWorlds
        );

        //TODO: Change to static
        this.commandHandler.getCommandCompletions().registerAsyncCompletion(
                "MVConfigs",
                this::suggestMVConfig
        );

        this.commandHandler.getCommandCompletions().registerStaticCompletion(
                "gameRules",
                suggestGameRules()
        );

        this.commandHandler.getCommandCompletions().registerStaticCompletion(
                "environments",
                suggestEnvironments()
        );

        this.commandHandler.getCommandCompletions().registerStaticCompletion(
                "addProperties",
                suggestAddProperties()
        );

        //TODO: set properties

        //TODO: add properties

        //TODO: remove properties

        //TODO: destination

        //TODO: version

        //TODO: environment
    }

    @NotNull
    private List<String> suggestMVWorlds(@NotNull BukkitCommandCompletionContext context) {
        return worldManager.getMVWorlds().stream()
                .map(MultiverseWorld::getName)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<String> suggestUnloadedWorlds(@NotNull BukkitCommandCompletionContext context) {
        return this.worldManager.getUnloadedWorlds();
    }

    @NotNull
    private List<String> suggestPotentialWorlds(@NotNull BukkitCommandCompletionContext context) {
        //TODO: this should be in WorldManager API
        List<String> knownWorlds = this.worldManager.getMVWorlds().stream()
                .map(MultiverseWorld::getName)
                .collect(Collectors.toList());
        knownWorlds.addAll(this.worldManager.getUnloadedWorlds());

        return Arrays.stream(this.plugin.getServer().getWorldContainer().listFiles())
                .filter(File::isDirectory)
                .filter(file -> !knownWorlds.contains(file.getName()))
                .filter(this::validateWorldFolder)
                .map(File::getName)
                .collect(Collectors.toList());
    }

    private boolean validateWorldFolder(@NotNull File worldFolder) {
        if (!worldFolder.isDirectory()) {
            return false;
        }
        if (BLACKLIST_WORLD_FOLDER.contains(worldFolder.getName())) {
            return false;
        }
        return folderHasDat(worldFolder);
    }


    @NotNull
    private Set<String> suggestMVConfig(@NotNull BukkitCommandCompletionContext context) {
        return this.plugin.getMVConfig().serialize().keySet();
    }

    @NotNull
    private List<String> suggestGameRules() {
        return Arrays.stream(GameRule.values())
                .map(GameRule::getName)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<String> suggestEnvironments() {
        return Arrays.stream(World.Environment.values())
                .map(e -> e.toString().toLowerCase())
                .collect(Collectors.toList());
    }

    @NotNull
    private List<String> suggestAddProperties() {
        return Arrays.stream(AddProperties.values())
                .map(p -> p.toString().toLowerCase())
                .collect(Collectors.toList());
    }

    public void registerCommandContexts() {
        this.commandHandler.getCommandContexts().registerIssuerAwareContext(
                MultiverseWorld.class,
                this::deriveMultiverseWorld
        );

        this.commandHandler.getCommandContexts().registerContext(
                CommandPlayer.class,
                this::deriveCommandPlayer
        );

        this.commandHandler.getCommandContexts().registerIssuerAwareContext(
                Player.class,
                this::derivePlayer
        );

        this.commandHandler.getCommandContexts().registerContext(
                World.Environment.class,
                this::deriveEnvironment
        );

        this.commandHandler.getCommandContexts().registerIssuerAwareContext(
                WorldFlags.class,
                this::deriveWorldFlags
        );

        this.commandHandler.getCommandContexts().registerIssuerAwareContext(
                GameRule.class,
                this::deriveGameRule
        );

        //TODO: Destination
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
        } else if (env.equalsIgnoreCase("HELL") || env.equalsIgnoreCase("NETHER")) {
            env = "NETHER";
        } else if (env.equalsIgnoreCase("END") || env.equalsIgnoreCase("THEEND") || env.equalsIgnoreCase("STARWARS")) {
            env = "THE_END";
        }

        try {
            return World.Environment.valueOf(env);
        } catch (IllegalArgumentException e) {
            throw new InvalidCommandArgument("'" + env + "' is not a valid environment.");
            //TODO: Possibly show valid environments.
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
            throw new InvalidCommandArgument("Invalid generator '" + value + "'.");
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

    public void registerCommandConditions() {
        this.commandHandler.getCommandConditions().addCondition(
                String.class,
                "isMVWorld",
                this::checkIsMVWorld
        );

        this.commandHandler.getCommandConditions().addCondition(
                String.class,
                "isUnloadedWorld",
                this::checkIsUnloadedWorld
        );

        this.commandHandler.getCommandConditions().addCondition(
                String.class,
                "isWorldInConfig",
                this::checkIsWorldInConfig
        );

        this.commandHandler.getCommandConditions().addCondition(
                String.class,
                "worldFolderExist",
                this::checkWorldFolderExist
        );

        this.commandHandler.getCommandConditions().addCondition(
                String.class,
                "validWorldFolder",
                this::checkValidWorldFolder
        );
    }

    //TODO: Message seems a bit too targeted to create world only
    private void checkIsMVWorld(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                @NotNull BukkitCommandExecutionContext executionContext,
                                @NotNull String worldName) {

        boolean shouldBeMVWorld = Boolean.parseBoolean(context.getConfig());
        boolean isMVWorld = this.worldManager.isMVWorld(worldName);

        if (isMVWorld && !shouldBeMVWorld) {
            executionContext.getSender().sendMessage(ChatColor.RED + "Multiverse cannot create " + ChatColor.GOLD + ChatColor.UNDERLINE
                    + "another" + ChatColor.RESET + ChatColor.RED + " world named " + worldName);
            throw new ConditionFailedException();
        }
        if (!isMVWorld && shouldBeMVWorld) {
            executionContext.getSender().sendMessage(ChatColor.RED + "Multiverse doesn't know about " + ChatColor.DARK_AQUA + worldName + ChatColor.WHITE + " yet.");
            executionContext.getSender().sendMessage("Type " + ChatColor.DARK_AQUA + "/mv import ?" + ChatColor.WHITE + " for help!");
            throw new ConditionFailedException();
        }
    }

    private void checkIsUnloadedWorld(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                      @NotNull BukkitCommandExecutionContext executionContext,
                                      @NotNull String worldName) {

        if (this.worldManager.isMVWorld(worldName)) {
            throw new ConditionFailedException("World '" + worldName + "' is already loaded.");
        }

        if (!this.worldManager.getUnloadedWorlds().contains(worldName)) {
            throw new ConditionFailedException("World '" + worldName + "' not found.");
        }
    }

    private void checkIsWorldInConfig(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                      @NotNull BukkitCommandExecutionContext executionContext,
                                      @NotNull String worldName) {

        //TODO: Should have direct API for it, instead of check both loaded and unloaded.
        if (!this.worldManager.isMVWorld(worldName) && !this.worldManager.getUnloadedWorlds().contains(worldName)) {
            throw new ConditionFailedException("World '" + worldName + "' not found.");
        }
    }

        private void checkWorldFolderExist(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                           @NotNull BukkitCommandExecutionContext executionContext,
                                           @NotNull String worldFolder) {

        boolean shouldExist = Boolean.parseBoolean(context.getConfig());
        boolean worldFileExist = new File(this.plugin.getServer().getWorldContainer(), worldFolder).exists();

        if (worldFileExist && !shouldExist) {
            executionContext.getSender().sendMessage(ChatColor.RED + "A Folder/World already exists with this name!");
            executionContext.getSender().sendMessage(ChatColor.RED + "If you are confident it is a world you can import with /mv import");
            throw new ConditionFailedException();
        }
        if (!worldFileExist && shouldExist) {
            executionContext.getSender().sendMessage(ChatColor.RED + "World folder '" + worldFolder + "' does not exist.");
            //TODO: Possibly show potential worlds.
            throw new ConditionFailedException();
        }
    }

    private void checkValidWorldFolder(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                       @NotNull BukkitCommandExecutionContext executionContext,
                                       @NotNull String worldName) {

        File worldFolder = new File(this.plugin.getServer().getWorldContainer(), worldName);
        if (!worldFolder.isDirectory()) {
            throw new ConditionFailedException("That world folder does not exist.");
        }
        if (BLACKLIST_WORLD_FOLDER.contains(worldFolder.getName())) {
            throw new ConditionFailedException("World should be not in reversed server folders.");
        }
        if (!folderHasDat(worldFolder)) {
            throw new ConditionFailedException("'" + worldName + "' does not appear to be a world. It is lacking a .dat file.");
        }
    }

    private boolean folderHasDat(@NotNull File worldFolder) {
        File[] files = worldFolder.listFiles((file, name) -> name.equalsIgnoreCase("level.dat"));
        return files != null && files.length > 0;
    }
}
