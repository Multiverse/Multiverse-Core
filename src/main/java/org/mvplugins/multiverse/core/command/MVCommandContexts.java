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
import org.mvplugins.multiverse.core.command.context.issueraware.IssuerAwareContextBuilder;
import org.mvplugins.multiverse.core.command.context.PlayerLocation;
import org.mvplugins.multiverse.core.command.context.issueraware.MultiverseWorldValue;
import org.mvplugins.multiverse.core.command.context.issueraware.PlayerArrayValue;
import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.display.filters.ContentFilter;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.filters.RegexContentFilter;
import org.mvplugins.multiverse.core.exceptions.command.MVInvalidCommandArgument;
import org.mvplugins.multiverse.core.locale.message.Message;
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
        registerIssuerAwareContext(LoadedMultiverseWorld.class, loadedMultiverseWorldContextBuilder().generateContext());
        registerIssuerAwareContext(LoadedMultiverseWorld[].class, loadedMultiverseWorldArrayContextBuilder().generateContext());
        registerIssuerAwareContext(MultiverseWorld.class, multiverseWorldContextBuilder().generateContext());
        registerIssuerAwareContext(MultiverseWorldValue.class, multiverseWorldContextBuilder().generateContext(MultiverseWorldValue::new));
        registerIssuerAwareContext(MultiverseWorld[].class, multiverseWorldArrayContextBuilder().generateContext());
        registerContext(MultiverseAnchor.class, this::parseMultiverseAnchor);
        registerIssuerAwareContext(Player.class, playerContextBuilder().generateContext());
        registerIssuerAwareContext(Player[].class, playerArrayContextBuilder().generateContext());
        registerIssuerAwareContext(PlayerArrayValue.class, playerArrayContextBuilder().generateContext(PlayerArrayValue::new));
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

        return destinationsProvider.parseDestination(context.getSender(), destination)
                .getOrThrow(failure ->
                        new InvalidCommandArgument(failure.getFailureMessage().formatted(context.getIssuer())));
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


    private IssuerAwareContextBuilder<LoadedMultiverseWorld> loadedMultiverseWorldContextBuilder() {
        return new IssuerAwareContextBuilder<LoadedMultiverseWorld>()
                .fromPlayer((context, player) -> worldManager.getLoadedWorld(player.getWorld()).getOrNull())
                .fromInput((context, input) -> getLoadedMultiverseWorld(input))
                .issuerOnlyFailMessage((context) -> Message.of("This command can only be used by a player in a loaded Multiverse World."))
                .issuerAwarePlayerFailMessage((context, player) -> Message.of("You are not in a loaded multiverse world. Either specify a multiverse world name or use this command in a loaded multiverse world."))
                .issuerAwareInputFailMessage((context, input) -> Message.of("World '" + input + "' is not a loaded multiverse world. Remember to specify the world name when using this command in console."))
                .inputOnlyFailMessage((context, input) -> Message.of("World " + input + " is not a loaded multiverse world."));
    }

    private IssuerAwareContextBuilder<LoadedMultiverseWorld[]> loadedMultiverseWorldArrayContextBuilder() {
        return new IssuerAwareContextBuilder<LoadedMultiverseWorld[]>()
                .fromPlayer((context, player) -> worldManager.getLoadedWorld(player.getWorld())
                        .map(world -> new LoadedMultiverseWorld[]{world})
                        .getOrNull())
                .fromInput((context, input) -> {
                    String[] worldNames = input == null ? new String[0] : REPatterns.COMMA.split(input);
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
                    return worlds.isEmpty() ? null : worlds.toArray(new LoadedMultiverseWorld[0]);
                })
                .issuerOnlyFailMessage((context) -> Message.of("This command can only be used by a player in a loaded Multiverse World."))
                .issuerAwarePlayerFailMessage((context, player) -> Message.of("You are not in a loaded multiverse world. Either specify a multiverse world name or use this command in a loaded multiverse world."))
                .issuerAwareInputFailMessage((context, input) -> Message.of("World '" + input + "' is not a loaded multiverse world. Remember to specify the world name when using this command in console."))
                .inputOnlyFailMessage((context, input) -> Message.of("World " + input + " is not a loaded multiverse world."));
    }

    @Nullable
    private LoadedMultiverseWorld getLoadedMultiverseWorld(String worldName) {
        return config.getResolveAliasName()
                ? worldManager.getLoadedWorldByNameOrAlias(worldName).getOrNull()
                : worldManager.getLoadedWorld(worldName).getOrNull();
    }

    private IssuerAwareContextBuilder<MultiverseWorld> multiverseWorldContextBuilder() {
        return new IssuerAwareContextBuilder<MultiverseWorld>()
                .fromPlayer((context, player) -> worldManager.getWorld(player.getWorld()).getOrNull())
                .fromInput((context, input) -> getMultiverseWorld(input))
                .issuerOnlyFailMessage((context) -> Message.of("This command can only be used by a player in a Multiverse World."))
                .issuerAwarePlayerFailMessage((context, player) -> Message.of("You are not in a multiverse world. Either specify a multiverse world name or use this command in a multiverse world."))
                .issuerAwareInputFailMessage((context, input) -> Message.of("World '" + input + "' is not a multiverse world. Remember to specify the world name when using this command in console."))
                .inputOnlyFailMessage((context, input) -> Message.of("World " + input + " is not a multiverse world."));
    }

    private IssuerAwareContextBuilder<MultiverseWorld[]> multiverseWorldArrayContextBuilder() {
        return new IssuerAwareContextBuilder<MultiverseWorld[]>()
                .fromPlayer((context, player) -> worldManager.getWorld(player.getWorld())
                        .map(world -> new MultiverseWorld[]{world})
                        .getOrNull())
                .fromInput((context, input) -> {
                    String[] worldNames = input == null ? new String[0] : REPatterns.COMMA.split(input);
                    Set<MultiverseWorld> worlds = new HashSet<>(worldNames.length);
                    for (String worldName : worldNames) {
                        if ("*".equals(worldName)) {
                            worlds.addAll(worldManager.getWorlds());
                            break;
                        }
                        MultiverseWorld world = getMultiverseWorld(worldName);
                        if (world == null) {
                            throw new InvalidCommandArgument("World " + worldName + " is not a multiverse world.");
                        }
                        worlds.add(world);
                    }
                    return worlds.isEmpty() ? null : worlds.toArray(new MultiverseWorld[0]);
                })
                .issuerOnlyFailMessage((context) -> Message.of("This command can only be used by a player in a Multiverse World."))
                .issuerAwarePlayerFailMessage((context, player) -> Message.of("You are not in a multiverse world. Either specify a multiverse world name or use this command in a multiverse world."))
                .issuerAwareInputFailMessage((context, input) -> Message.of("World '" + input + "' is not a multiverse world. Remember to specify the world name when using this command in console."))
                .inputOnlyFailMessage((context, input) -> Message.of("World " + input + " is not a multiverse world."));
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

    private IssuerAwareContextBuilder<Player> playerContextBuilder() {
        return new IssuerAwareContextBuilder<Player>()
                .fromPlayer((context, player) -> player)
                .fromInput((context, input) -> PlayerFinder.get(input, context.getSender()))
                .issuerOnlyFailMessage((context) -> Message.of("This command can only be used by a player."))
                .issuerAwareInputFailMessage((context, input) -> Message.of("Invalid player: " + input + ". Either specify an online player or use this command as a player."))
                .inputOnlyFailMessage((context, input) -> Message.of("Player " + input + " not found."));
    }

    private IssuerAwareContextBuilder<Player[]> playerArrayContextBuilder() {
        return new IssuerAwareContextBuilder<Player[]>()
                .fromPlayer((context, player) -> new Player[]{player})
                .fromInput((context, input) -> {
                    Player[] players = PlayerFinder.getMulti(input, context.getSender()).toArray(new Player[0]);
                    return (players.length == 0) ? null : players;
                })
                .issuerOnlyFailMessage((context) -> Message.of("This command can only be used by a player."))
                .issuerAwareInputFailMessage((context, input) -> Message.of("Invalid player: " + input + ". Either specify an online player or use this command as a player."))
                .inputOnlyFailMessage((context, input) -> Message.of("Player " + input + " not found."));
    }

    private PlayerLocation parsePlayerLocation(BukkitCommandExecutionContext context) {
        Try<Location> location = Try.of(() -> parseLocationFromInput(context.getFirstArg(), context.getSender()));

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
    private Location parseLocationFromInput(String input, CommandSender sender) {
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
