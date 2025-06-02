package org.mvplugins.multiverse.core.command;

import java.util.HashSet;
import java.util.Set;

import co.aikar.commands.ACFUtil;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MinecraftMessageKeys;
import co.aikar.commands.PaperCommandContexts;
import co.aikar.commands.contexts.ContextResolver;
import com.google.common.base.Strings;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.anchor.AnchorManager;
import org.mvplugins.multiverse.core.anchor.MultiverseAnchor;
import org.mvplugins.multiverse.core.command.context.GameRuleValue;
import org.mvplugins.multiverse.core.command.context.PlayerLocation;
import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.display.filters.ContentFilter;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.filters.RegexContentFilter;
import org.mvplugins.multiverse.core.exceptions.command.MVInvalidCommandArgument;
import org.mvplugins.multiverse.core.utils.PlayerFinder;
import org.mvplugins.multiverse.core.utils.REPatterns;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.generators.GeneratorPlugin;
import org.mvplugins.multiverse.core.world.generators.GeneratorProvider;

@Service
public class MVCommandContexts extends PaperCommandContexts {

    private final MVCommandManager mvCommandManager;
    private final DestinationsProvider destinationsProvider;
    private final WorldManager worldManager;
    private final CoreConfig config;
    private final AnchorManager anchorManager;
    private final GeneratorProvider generatorProvider;

    @Inject
    MVCommandContexts(
            MVCommandManager mvCommandManager,
            DestinationsProvider destinationsProvider,
            WorldManager worldManager,
            CoreConfig config,
            AnchorManager anchorManager,
            GeneratorProvider generatorProvider
    ) {
        super(mvCommandManager);
        this.mvCommandManager = mvCommandManager;
        this.destinationsProvider = destinationsProvider;
        this.worldManager = worldManager;
        this.config = config;
        this.anchorManager = anchorManager;
        this.generatorProvider = generatorProvider;

        registerIssuerOnlyContext(BukkitCommandIssuer.class, BukkitCommandExecutionContext::getIssuer);
        registerIssuerOnlyContext(MVCommandIssuer.class, this::parseMVCommandIssuer);
        registerOptionalContext(ContentFilter.class, this::parseContentFilter);
        registerContext(DestinationInstance.class, this::parseDestination);
        registerContext(GameRule.class, this::parseGameRule);
        registerContext(GameRuleValue.class, this::parseGameRuleValue);
        registerContext(GeneratorPlugin.class, this::parseGeneratorPlugin);
        registerIssuerAwareContext(LoadedMultiverseWorld.class, this::parseLoadedMultiverseWorld);
        registerIssuerAwareContext(LoadedMultiverseWorld[].class, this::parseLoadedMultiverseWorldArray);
        registerIssuerAwareContext(MultiverseWorld.class, this::parseMultiverseWorld);
        registerIssuerAwareContext(MultiverseWorld[].class, this::parseMultiverseWorldArray);
        registerContext(MultiverseAnchor.class, this::parseMultiverseAnchor);
        registerIssuerAwareContext(Player.class, this::parsePlayer);
        registerIssuerAwareContext(Player[].class, this::parsePlayerArray);
        registerIssuerAwareContext(PlayerLocation.class, this::parsePlayerLocation);
        registerContext(SpawnCategory[].class, this::parseSpawnCategories);
    }

    private MVCommandIssuer parseMVCommandIssuer(BukkitCommandExecutionContext context) {
        if (context.getIssuer() instanceof MVCommandIssuer) {
            return (MVCommandIssuer) context.getIssuer();
        }
        return mvCommandManager.getCommandIssuer(context.getSender());
    }

    private ContentFilter parseContentFilter(BukkitCommandExecutionContext context) {
        if (Strings.isNullOrEmpty(context.getFirstArg())) {
            return DefaultContentFilter.get();
        }
        String filterString = context.popFirstArg();
        return RegexContentFilter.fromString(filterString);
    }

    private DestinationInstance<?, ?> parseDestination(BukkitCommandExecutionContext context) {
        String destination = context.popFirstArg();
        if (Strings.isNullOrEmpty(destination)) {
            throw new InvalidCommandArgument("No destination specified.");
        }

        return destinationsProvider.parseDestination(destination)
                .getOrThrow(failure -> MVInvalidCommandArgument.of(failure.getFailureMessage()));
    }

    private GameRule<?> parseGameRule(BukkitCommandExecutionContext context) {
        String gameRuleName = context.popFirstArg();
        if (Strings.isNullOrEmpty(gameRuleName)) {
            throw new InvalidCommandArgument("No game rule specified.");
        }

        GameRule<?> gameRule = GameRule.getByName(gameRuleName);
        if (gameRule == null) {
            throw new InvalidCommandArgument("The game rule " + gameRuleName + " is not valid.");
        }

        return gameRule;
    }

    private GameRuleValue parseGameRuleValue(BukkitCommandExecutionContext context) {
        GameRule<?> gameRule = (GameRule<?>) context.getResolvedArg(GameRule.class);
        if (gameRule == null) {
            throw new InvalidCommandArgument("No game rule specified.");
        }
        String valueString = context.getFirstArg();
        if (Strings.isNullOrEmpty(valueString)) {
            throw new InvalidCommandArgument("No game rule value specified.");
        }

        ContextResolver<?, BukkitCommandExecutionContext> resolver = getResolver(gameRule.getType());
        if (resolver == null) {
            return new GameRuleValue(valueString);
        }

        Object resolvedValue = resolver.getContext(context);
        if (resolvedValue == null) {
            throw new InvalidCommandArgument("The game rule value " + valueString + " is not valid for game rule " + gameRule.getName() + ".");
        }

        return new GameRuleValue(resolvedValue);
    }

    private GeneratorPlugin parseGeneratorPlugin(BukkitCommandExecutionContext context) {
        GeneratorPlugin generatorPlugin = generatorProvider.getGeneratorPlugin(context.popFirstArg());
        if (generatorPlugin == null) {
            throw new InvalidCommandArgument("Invalid generator plugin: " + context.getFirstArg());
        }
        return generatorPlugin;
    }

    private LoadedMultiverseWorld parseLoadedMultiverseWorld(BukkitCommandExecutionContext context) {
        String resolve = context.getFlagValue("resolve", "");

        // Get world based on sender only
        if (resolve.equals("issuerOnly")) {
            if (context.getIssuer().isPlayer()) {
                return worldManager.getLoadedWorld(context.getIssuer().getPlayer().getWorld()).getOrNull();
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("This command can only be used by a player in a Multiverse World.");
        }

        String worldName = context.getFirstArg();
        LoadedMultiverseWorld world = getLoadedMultiverseWorld(worldName);

        // Get world based on input, fallback to sender if input is not a world
        if (resolve.equals("issuerAware")) {
            if (world != null) {
                context.popFirstArg();
                return world;
            }
            if (context.getIssuer().isPlayer()) {
                return worldManager.getLoadedWorld(context.getPlayer().getWorld())
                        .getOrElseThrow(() -> new InvalidCommandArgument("You are not in a multiverse world. Either specify a multiverse world name or use this command in a multiverse world."));
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("World '" + worldName + "' is not a loaded multiverse world. Remember to specify the world name when using this command in console.");
        }

        // Get world based on input only
        if (world != null) {
            context.popFirstArg();
            return world;
        }
        if (context.isOptional()) {
            return null;
        }
        throw new InvalidCommandArgument("World " + worldName + " is not a loaded multiverse world.");
    }

    private LoadedMultiverseWorld[] parseLoadedMultiverseWorldArray(BukkitCommandExecutionContext context) {
        String resolve = context.getFlagValue("resolve", "");

        // Get world based on sender only
        if (resolve.equals("issuerOnly")) {
            if (context.getIssuer().isPlayer()) {
                LoadedMultiverseWorld playerWorld = worldManager.getLoadedWorld(context.getIssuer().getPlayer().getWorld())
                        .getOrElseThrow(() -> new InvalidCommandArgument("You are not in a multiverse world. Either specify a multiverse world name or use this command in a multiverse world."));
                return new LoadedMultiverseWorld[]{playerWorld};
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("This command can only be used by a player in a Multiverse World.");
        }

        String worldStrings = context.getFirstArg();
        String[] worldNames = worldStrings == null ? new String[0] : REPatterns.COMMA.split(worldStrings);
        Set<LoadedMultiverseWorld> worlds = new HashSet<>(worldNames.length);
        for (String worldName : worldNames) {
            if ("*".equals(worldName)) {
                worlds.addAll(worldManager.getLoadedWorlds());
                break;
            }
            LoadedMultiverseWorld world = getLoadedMultiverseWorld(worldName);
            if (world == null) {
                throw new InvalidCommandArgument("World " + worldName + " is not a loaded multiverse world.");
            }
            worlds.add(world);
        }

        // Get world based on input, fallback to sender if input is not a world
        if (resolve.equals("issuerAware")) {
            if (!worlds.isEmpty()) {
                context.popFirstArg();
                return worlds.toArray(new LoadedMultiverseWorld[0]);
            }
            if (context.getIssuer().isPlayer()) {
                LoadedMultiverseWorld playerWorld = worldManager.getLoadedWorld(context.getIssuer().getPlayer().getWorld())
                        .getOrElseThrow(() -> new InvalidCommandArgument("You are not in a multiverse world. Either specify a multiverse world name or use this command in a multiverse world."));
                return new LoadedMultiverseWorld[]{playerWorld};
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("Worlds '" + worldStrings + "' is not a loaded multiverse world. Remember to specify the world name when using this command in console.");
        }

        // Get world based on input only
        if (!worlds.isEmpty()) {
            context.popFirstArg();
            return worlds.toArray(new LoadedMultiverseWorld[0]);
        }
        if (context.isOptional()) {
            return null;
        }
        throw new InvalidCommandArgument("World " + worldStrings + " is not a loaded multiverse world.");
    }

    @Nullable
    private LoadedMultiverseWorld getLoadedMultiverseWorld(String worldName) {
        return config.getResolveAliasName()
                ? worldManager.getLoadedWorldByNameOrAlias(worldName).getOrNull()
                : worldManager.getLoadedWorld(worldName).getOrNull();
    }

    private MultiverseWorld parseMultiverseWorld(BukkitCommandExecutionContext context) {
        String resolve = context.getFlagValue("resolve", "");

        // Get world based on sender only
        if (resolve.equals("issuerOnly")) {
            if (context.getIssuer().isPlayer()) {
                return worldManager.getWorld(context.getIssuer().getPlayer().getWorld()).getOrNull();
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("This command can only be used by a player in a Multiverse World.");
        }

        String worldName = context.getFirstArg();
        MultiverseWorld world = getMultiverseWorld(worldName);

        // Get world based on input, fallback to sender if input is not a world
        if (resolve.equals("issuerAware")) {
            if (world != null) {
                context.popFirstArg();
                return world;
            }
            if (context.getIssuer().isPlayer()) {
                return worldManager.getWorld(context.getPlayer().getWorld())
                        .getOrElseThrow(() -> new InvalidCommandArgument("You are not in a multiverse world. Either specify a multiverse world name or use this command in a multiverse world."));
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("World '" + worldName + "' is not a loaded multiverse world. Remember to specify the world name when using this command in console.");
        }

        // Get world based on input only
        if (world != null) {
            context.popFirstArg();
            return world;
        }
        if (context.isOptional()) {
            return null;
        }
        throw new InvalidCommandArgument("World " + worldName + " is not a loaded multiverse world.");
    }

    private MultiverseWorld[] parseMultiverseWorldArray(BukkitCommandExecutionContext context) {
        String resolve = context.getFlagValue("resolve", "");

        // Get world based on sender only
        if (resolve.equals("issuerOnly")) {
            if (context.getIssuer().isPlayer()) {
                MultiverseWorld playerWorld = worldManager.getWorld(context.getIssuer().getPlayer().getWorld())
                        .getOrElseThrow(() -> new InvalidCommandArgument("You are not in a multiverse world. Either specify a multiverse world name or use this command in a multiverse world."));
                return new MultiverseWorld[]{playerWorld};
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("This command can only be used by a player in a Multiverse World.");
        }

        String worldStrings = context.getFirstArg();
        String[] worldNames = worldStrings == null ? new String[0] : REPatterns.COMMA.split(worldStrings);
        Set<MultiverseWorld> worlds = new HashSet<>(worldNames.length);
        for (String worldName : worldNames) {
            if ("*".equals(worldName)) {
                worlds.addAll(worldManager.getWorlds());
                break;
            }
            MultiverseWorld world = getMultiverseWorld(worldName);
            if (world == null) {
                throw new InvalidCommandArgument("World " + worldName + " is not a loaded multiverse world.");
            }
            worlds.add(world);
        }

        // Get world based on input, fallback to sender if input is not a world
        if (resolve.equals("issuerAware")) {
            if (!worlds.isEmpty()) {
                context.popFirstArg();
                return worlds.toArray(new MultiverseWorld[0]);
            }
            if (context.getIssuer().isPlayer()) {
                MultiverseWorld playerWorld = worldManager.getWorld(context.getIssuer().getPlayer().getWorld())
                        .getOrElseThrow(() -> new InvalidCommandArgument("You are not in a multiverse world. Either specify a multiverse world name or use this command in a multiverse world."));
                return new MultiverseWorld[]{playerWorld};
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("Worlds '" + worldStrings + "' is not a loaded multiverse world. Remember to specify the world name when using this command in console.");
        }

        // Get world based on input only
        if (!worlds.isEmpty()) {
            context.popFirstArg();
            return worlds.toArray(new MultiverseWorld[0]);
        }
        if (context.isOptional()) {
            return null;
        }
        throw new InvalidCommandArgument("World " + worldStrings + " is not a loaded multiverse world.");
    }

    @Nullable
    private MultiverseWorld getMultiverseWorld(String worldName) {
        return config.getResolveAliasName()
                ? worldManager.getWorldByNameOrAlias(worldName).getOrNull()
                : worldManager.getWorld(worldName).getOrNull();
    }

    private MultiverseAnchor parseMultiverseAnchor(BukkitCommandExecutionContext context) {
        String anchorName = context.popFirstArg();
        return anchorManager.getAnchor(anchorName)
                .getOrElseThrow(() -> new InvalidCommandArgument("The anchor '" +anchorName + "' does not exist."));
    }

    private Player parsePlayer(BukkitCommandExecutionContext context) {
        String resolve = context.getFlagValue("resolve", "");

        // Get player based on sender only
        if (resolve.equals("issuerOnly")) {
            if (context.getIssuer().isPlayer()) {
                return context.getIssuer().getPlayer();
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("This command can only be used by a player.");
        }

        String playerIdentifier = context.getFirstArg();
        Player player = PlayerFinder.get(playerIdentifier, context.getSender());

        // Get player based on input, fallback to sender if input is not a player
        if (resolve.equals("issuerAware")) {
            if (player != null) {
                context.popFirstArg();
                return player;
            }
            if (context.getIssuer().isPlayer()) {
                return context.getIssuer().getPlayer();
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("Invalid player: " + playerIdentifier
                    + ". Either specify an online player or use this command as a player.");
        }

        // Get player based on input only
        if (player != null) {
            context.popFirstArg();
            return player;
        }
        if (context.isOptional()) {
            return null;
        }
        throw new InvalidCommandArgument("Player " + playerIdentifier + " not found.");
    }

    private Player[] parsePlayerArray(BukkitCommandExecutionContext context) {
        String resolve = context.getFlagValue("resolve", "");

        // Get player based on sender only
        if (resolve.equals("issuerOnly")) {
            if (context.getIssuer().isPlayer()) {
                return new Player[]{context.getIssuer().getPlayer()};
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("This command can only be used by a player.");
        }

        String playerIdentifier = context.getFirstArg();
        Player[] players = PlayerFinder.getMulti(playerIdentifier, context.getSender()).toArray(new Player[0]);

        // Get player based on input, fallback to sender if input is not a player
        if (resolve.equals("issuerAware")) {
            if (players.length > 0) {
                context.popFirstArg();
                return players;
            }
            if (context.getIssuer().isPlayer()) {
                return new Player[]{context.getIssuer().getPlayer()};
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("Invalid player: " + playerIdentifier
                    + ". Either specify an online player or use this command as a player.");
        }

        // Get player based on input only
        if (players.length > 0) {
            context.popFirstArg();
            return players;
        }
        if (context.isOptional()) {
            return null;
        }
        throw new InvalidCommandArgument("Player " + playerIdentifier + " not found.");
    }

    private PlayerLocation parsePlayerLocation(BukkitCommandExecutionContext context) {
        Try<Location> location = Try.of(() -> parseLocation(context.getFirstArg(), context.getSender()));

        if (location.isSuccess()) {
            context.popFirstArg();
            return new PlayerLocation(location.get());
        }
        if (context.getPlayer() != null) {
            return new PlayerLocation(context.getPlayer().getLocation());
        }
        if (context.getFirstArg() == null) {
            throw new InvalidCommandArgument("You must specify a world location when using this command in console.");
        }
        if (location.getCause() instanceof InvalidCommandArgument) {
            throw (InvalidCommandArgument)location.getCause();
        }
        throw new RuntimeException(location.getCause());
    }

    // copied from ACF
    private Location parseLocation(String input, CommandSender sender) {
        String[] split = REPatterns.COLON.split(input, 2);
        if (split.length == 0) {
            throw new InvalidCommandArgument(true);
        } else if (split.length < 2 && !(sender instanceof Player) && !(sender instanceof BlockCommandSender)) {
            throw new InvalidCommandArgument(MinecraftMessageKeys.LOCATION_PLEASE_SPECIFY_WORLD, new String[0]);
        } else {
            Location sourceLoc = null;
            String world;
            String rest;
            if (split.length == 2) {
                world = split[0];
                rest = split[1];
            } else if (sender instanceof Player) {
                sourceLoc = ((Player)sender).getLocation();
                world = sourceLoc.getWorld().getName();
                rest = split[0];
            } else {
                if (!(sender instanceof BlockCommandSender)) {
                    throw new InvalidCommandArgument(true);
                }

                sourceLoc = ((BlockCommandSender)sender).getBlock().getLocation();
                world = sourceLoc.getWorld().getName();
                rest = split[0];
            }

            boolean rel = rest.startsWith("~");
            split = REPatterns.COMMA.split(rel ? rest.substring(1) : rest);
            if (split.length < 3) {
                throw new InvalidCommandArgument(MinecraftMessageKeys.LOCATION_PLEASE_SPECIFY_XYZ, new String[0]);
            } else {
                Double x = ACFUtil.parseDouble(split[0], rel ? (double)0.0F : null);
                Double y = ACFUtil.parseDouble(split[1], rel ? (double)0.0F : null);
                Double z = ACFUtil.parseDouble(split[2], rel ? (double)0.0F : null);
                if (sourceLoc != null && rel) {
                    x = x + sourceLoc.getX();
                    y = y + sourceLoc.getY();
                    z = z + sourceLoc.getZ();
                } else if (rel) {
                    throw new InvalidCommandArgument(MinecraftMessageKeys.LOCATION_CONSOLE_NOT_RELATIVE, new String[0]);
                }

                if (x != null && y != null && z != null) {
                    World worldObj = Bukkit.getWorld(world);
                    if (worldObj == null) {
                        throw new InvalidCommandArgument(MinecraftMessageKeys.INVALID_WORLD, new String[0]);
                    } else if (split.length >= 5) {
                        Float yaw = ACFUtil.parseFloat(split[3]);
                        Float pitch = ACFUtil.parseFloat(split[4]);
                        if (pitch != null && yaw != null) {
                            return new Location(worldObj, x, y, z, yaw, pitch);
                        } else {
                            throw new InvalidCommandArgument(MinecraftMessageKeys.LOCATION_PLEASE_SPECIFY_XYZ, new String[0]);
                        }
                    } else {
                        return new Location(worldObj, x, y, z);
                    }
                } else {
                    throw new InvalidCommandArgument(MinecraftMessageKeys.LOCATION_PLEASE_SPECIFY_XYZ, new String[0]);
                }
            }
        }
    }

    private SpawnCategory[] parseSpawnCategories(BukkitCommandExecutionContext context) {
        if (context.isOptional() && context.getArgs().isEmpty()) {
            return new SpawnCategory[0];
        }
        Set<SpawnCategory> categories = new HashSet<>();
        String[] split = REPatterns.COMMA.split(context.popFirstArg());
        for (String category : split) {
            SpawnCategory spawnCategory = ACFUtil.simpleMatch(SpawnCategory.class, category);
            if (spawnCategory != null) {
                categories.add(spawnCategory);
            }
        }
        return categories.toArray(new SpawnCategory[0]);
    }
}
