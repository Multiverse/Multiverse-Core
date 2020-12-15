package com.onarandombox.MultiverseCore.commands_helper;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                "MVWorlds",
                context -> worldManager.getMVWorlds()
                        .stream()
                        .map(MultiverseWorld::getName)
                        .collect(Collectors.toList())
        );

        this.commandHandler.getCommandCompletions().registerAsyncCompletion(
                "unloadedWorlds",
                context -> this.worldManager.getUnloadedWorlds()
        );

        this.commandHandler.getCommandCompletions().registerAsyncCompletion(
                "MVConfigs",
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

        this.commandHandler.getCommandContexts().registerIssuerAwareContext(
                WorldFlags.class,
                this::deriveWorldFlags
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
        } catch (NumberFormatException e) {
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
        } catch (Exception e) {
            return null;
        }
        return Bukkit.getPlayer(playerUUID);
    }

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

    private String validateGenerator(String value) {
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

    private WorldType getWorldType(String type) {
        if (type == null || type.length() == 0) {
            return WorldType.NORMAL;
        }

        if (type.equalsIgnoreCase("normal")) {
            type = "NORMAL";
        } else if (type.equalsIgnoreCase("flat")) {
            type = "FLAT";
        } else if (type.equalsIgnoreCase("largebiomes")) {
            type = "LARGE_BIOMES";
        } else if (type.equalsIgnoreCase("amplified")) {
            type = "AMPLIFIED";
        }

        try {
            return WorldType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new InvalidCommandArgument("'" + type + "' is not a valid World Type.");
        }
    }

    private boolean doGenerateStructures(String value) {
        if (value == null) {
            return true;
        }
        return value.equalsIgnoreCase("true");
    }

    private Map<String, String> parseFlags(List<String> args) {
        Map<String, String> flags = new HashMap<>();
        if (!validateFlagArgs(args)) {
            return flags;
        }

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

        return flags;
    }

    private boolean validateFlagArgs(List<String> args) {
        if (args == null || args.size() == 0) {
            return false;
        }
        if (!isFlagKey(args.get(0))) {
            throw new InvalidCommandArgument("No flag defined for value '" + args.get(0) + "'");
        }
        return true;
    }

    private boolean isFlagKey(String value) {
        return value.charAt(0) == '-';
    }

    public void registerCommandConditions() {
        this.commandHandler.getCommandConditions().addCondition(
                String.class,
                "isMVWorld",
                this::checkIsMVWorld
        );

        this.commandHandler.getCommandConditions().addCondition(
                String.class,
                "worldFolderExist",
                this::checkIsWorldFolderExist
        );
    }

    private void checkIsMVWorld(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext executionContext, String worldName) {
        boolean shouldBeMVWorld = Boolean.parseBoolean(context.getConfig());
        boolean isMVWorld = this.plugin.getMVWorldManager().isMVWorld(worldName);

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

    private void checkIsWorldFolderExist(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext executionContext, String worldFolder) {
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
}
