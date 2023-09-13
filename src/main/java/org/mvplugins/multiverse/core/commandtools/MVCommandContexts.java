package org.mvplugins.multiverse.core.commandtools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandContexts;
import co.aikar.commands.contexts.ContextResolver;
import com.dumptruckman.minecraft.util.Logging;
import com.google.common.base.Strings;
import io.vavr.control.Option;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.context.GameRuleValue;
import org.mvplugins.multiverse.core.commandtools.context.MVConfigValue;
import org.mvplugins.multiverse.core.commandtools.context.WorldConfigValue;
import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.configuration.handle.ConfigModifyType;
import org.mvplugins.multiverse.core.configuration.node.Node;
import org.mvplugins.multiverse.core.configuration.node.ValueNode;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.destination.ParsedDestination;
import org.mvplugins.multiverse.core.display.filters.ContentFilter;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.filters.RegexContentFilter;
import org.mvplugins.multiverse.core.utils.PlayerFinder;
import org.mvplugins.multiverse.core.worldnew.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.worldnew.MultiverseWorld;
import org.mvplugins.multiverse.core.worldnew.WorldManager;

@Service
class MVCommandContexts extends PaperCommandContexts {

    private final MVCommandManager mvCommandManager;
    private final DestinationsProvider destinationsProvider;
    private final WorldManager worldManager;
    private final MVCoreConfig config;

    @Inject
    MVCommandContexts(
            MVCommandManager mvCommandManager,
            DestinationsProvider destinationsProvider,
            WorldManager worldManager,
            MVCoreConfig config) {
        super(mvCommandManager);
        this.mvCommandManager = mvCommandManager;
        this.destinationsProvider = destinationsProvider;
        this.worldManager = worldManager;
        this.config = config;

        registerIssuerOnlyContext(BukkitCommandIssuer.class, BukkitCommandExecutionContext::getIssuer);
        registerIssuerOnlyContext(MVCommandIssuer.class, this::parseMVCommandIssuer);
        registerOptionalContext(ContentFilter.class, this::parseContentFilter);
        registerContext(ParsedDestination.class, this::parseDestination);
        registerContext(GameRule.class, this::parseGameRule);
        registerContext(GameRuleValue.class, this::parseGameRuleValue);
        registerContext(MVConfigValue.class, this::parseMVConfigValue);
        registerIssuerAwareContext(MultiverseWorld.class, this::parseWorld);
        registerIssuerAwareContext(LoadedMultiverseWorld.class, this::parseLoadedWorld);
        registerIssuerAwareContext(LoadedMultiverseWorld[].class, this::parseLoadedWorldArray);
        registerIssuerAwareContext(Player.class, this::parsePlayer);
        registerIssuerAwareContext(Player[].class, this::parsePlayerArray);
        registerIssuerAwareContext(WorldConfigValue.class, this::parseWorldConfigValue);
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

    private ParsedDestination<?> parseDestination(BukkitCommandExecutionContext context) {
        String destination = context.popFirstArg();
        if (Strings.isNullOrEmpty(destination)) {
            throw new InvalidCommandArgument("No destination specified.");
        }

        ParsedDestination<?> parsedDestination = destinationsProvider.parseDestination(destination);
        if (parsedDestination == null) {
            throw new InvalidCommandArgument("The destination " + destination + " is not valid.");
        }

        return parsedDestination;
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

    private MVConfigValue parseMVConfigValue(BukkitCommandExecutionContext context) {
        String configName = (String) context.getResolvedArg(String.class);
        if (Strings.isNullOrEmpty(configName)) {
            throw new InvalidCommandArgument("No config name specified.");
        }
        Option<Node> node = config.getNodes().findNode(configName);
        if (node.isEmpty()) {
            throw new InvalidCommandArgument("The config " + configName + " is not valid.");
        }

        String valueString = context.getFirstArg();
        if (Strings.isNullOrEmpty(valueString)) {
            throw new InvalidCommandArgument("No config value specified.");
        }

        if (!(node.get() instanceof ValueNode)) {
            context.popFirstArg();
            return new MVConfigValue(valueString);
        }

        ContextResolver<?, BukkitCommandExecutionContext> resolver = getResolver(((ValueNode<?>) node.get()).getType());
        if (resolver == null) {
            context.popFirstArg();
            return new MVConfigValue(valueString);
        }

        Object resolvedValue = resolver.getContext(context);
        if (resolvedValue == null) {
            throw new InvalidCommandArgument("The config value " + valueString + " is not valid for config " + configName + ".");
        }
        return new MVConfigValue(resolvedValue);
    }

    private MultiverseWorld parseWorld(BukkitCommandExecutionContext context) {
        String resolve = context.getFlagValue("resolve", "");

        // Get world based on sender only
        if (resolve.equals("issuerOnly")) {
            if (context.getIssuer().isPlayer()) {
                return worldManager.getWorld(context.getIssuer().getPlayer().getWorld())
                        .getOrElseThrow(() -> new InvalidCommandArgument("Player is not in a Multiverse World."));
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("This command can only be used by a player in a Multiverse World.");
        }

        String worldName = context.getFirstArg();
        MultiverseWorld world = worldManager.getWorld(worldName).getOrNull();

        // Get world based on input, fallback to sender if input is not a world
        if (resolve.equals("issuerAware")) {
            if (world != null) {
                context.popFirstArg();
                return world;
            }
            if (context.getIssuer().isPlayer()) {
                return worldManager.getWorld(context.getIssuer().getPlayer().getWorld())
                        .getOrElseThrow(() -> new InvalidCommandArgument("Player is not in a Multiverse World."));
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("Player is not in a Multiverse World.");
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


    private LoadedMultiverseWorld parseLoadedWorld(BukkitCommandExecutionContext context) {
        String resolve = context.getFlagValue("resolve", "");

        // Get world based on sender only
        if (resolve.equals("issuerOnly")) {
            if (context.getIssuer().isPlayer()) {
                return worldManager.getLoadedWorld(context.getIssuer().getPlayer().getWorld())
                        .getOrElseThrow(() -> new InvalidCommandArgument("Player is not in a Multiverse World."));
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("This command can only be used by a player in a Multiverse World.");
        }

        String worldName = context.getFirstArg();
        LoadedMultiverseWorld world = worldManager.getLoadedWorld(worldName).getOrNull();

        // Get world based on input, fallback to sender if input is not a world
        if (resolve.equals("issuerAware")) {
            if (world != null) {
                context.popFirstArg();
                return world;
            }
            if (context.getIssuer().isPlayer()) {
                return worldManager.getLoadedWorld(context.getIssuer().getPlayer().getWorld())
                        .getOrElseThrow(() -> new InvalidCommandArgument("Player is not in a Multiverse World."));
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("Player is not in a Multiverse World.");
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

    private LoadedMultiverseWorld[] parseLoadedWorldArray(BukkitCommandExecutionContext context) {
        String resolve = context.getFlagValue("resolve", "");

        LoadedMultiverseWorld playerWorld = null;
        if (context.getIssuer().isPlayer()) {
            playerWorld = worldManager.getLoadedWorld(context.getIssuer().getPlayer().getWorld()).getOrNull();
        }

        // Get world based on sender only
        if (resolve.equals("issuerOnly")) {
            if (playerWorld != null) {
                return new LoadedMultiverseWorld[]{playerWorld};
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("This command can only be used by a player in a Multiverse World.");
        }

        String worldStrings = context.getFirstArg();
        String[] worldNames = worldStrings == null ? new String[0] : worldStrings.split(",");
        Set<LoadedMultiverseWorld> worlds = new HashSet<>(worldNames.length);
        for (String worldName : worldNames) {
            if ("*".equals(worldName)) {
                worlds.addAll(worldManager.getLoadedWorlds());
                break;
            }
            LoadedMultiverseWorld world = worldManager.getLoadedWorld(worldName).getOrNull();
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
            if (playerWorld != null) {
                return new LoadedMultiverseWorld[]{playerWorld};
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("Player is not in a Multiverse World.");
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

    private WorldConfigValue parseWorldConfigValue(BukkitCommandExecutionContext context) {
        MultiverseWorld mvWorld = (MultiverseWorld) context.getResolvedArg(MultiverseWorld.class);
        ConfigModifyType modifyType = (ConfigModifyType) context.getResolvedArg(ConfigModifyType.class);
        String propertyName = (String) context.getResolvedArg(String.class);
        if (mvWorld == null || modifyType == null || propertyName == null) {
            throw new InvalidCommandArgument("No world or property specified.");
        }

        if (modifyType == ConfigModifyType.RESET) {
            if (context.popFirstArg() != null) {
                throw new InvalidCommandArgument("No value should be specified for reset.");
            }
            return new WorldConfigValue(null);
        }

        Class<?> type = mvWorld.getPropertyType(propertyName)
                .getOrElseThrow(() -> {
                    return new InvalidCommandArgument("The property " + propertyName + " is not valid for world "
                            + mvWorld.getName() + ".");
                });
        return new WorldConfigValue(parseType(context, type));
    }

    private Object parseType(BukkitCommandExecutionContext context, Class<?> type) {
        Object value = context.getFirstArg();
        if (value == null) {
            throw new InvalidCommandArgument("No value specified.");
        }

        // Special case for enums
        if (type.isEnum()) {
            return Try.of(() -> Enum.valueOf((Class<? extends Enum>) type, context.popFirstArg().toUpperCase()))
                    .getOrElseThrow(() -> new InvalidCommandArgument(("The value %s is not a valid %s. "
                            + "Valid values are: %s").formatted(
                                    value,
                                    type.getSimpleName(),
                                    Arrays.stream(((Class<? extends Enum>) type).getEnumConstants())
                                            .map(enumValue -> enumValue.name().toLowerCase())
                                            .reduce((a, b) -> a + ", " + b)
                                            .orElse(""))));
        }

        ContextResolver<?, BukkitCommandExecutionContext> resolver = getResolver(type);
        return resolver == null ? value : resolver.getContext(context);
    }
}
