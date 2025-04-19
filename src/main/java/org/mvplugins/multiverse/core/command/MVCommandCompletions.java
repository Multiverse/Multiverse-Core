package org.mvplugins.multiverse.core.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.PaperCommandCompletions;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.RootCommand;
import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.anchor.AnchorManager;
import org.mvplugins.multiverse.core.anchor.MultiverseAnchor;
import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.config.node.functions.DefaultSuggesterProvider;
import org.mvplugins.multiverse.core.config.handle.PropertyModifyAction;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.destination.core.WorldDestination;
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;
import org.mvplugins.multiverse.core.utils.REPatterns;
import org.mvplugins.multiverse.core.utils.StringFormatter;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

import static org.mvplugins.multiverse.core.utils.StringFormatter.addonToCommaSeperated;

@Service
public class MVCommandCompletions extends PaperCommandCompletions {

    private final MVCommandManager commandManager;
    private final WorldManager worldManager;
    private final DestinationsProvider destinationsProvider;
    private final CoreConfig config;
    private final CorePermissionsChecker corePermissionsChecker;
    private final AnchorManager anchorManager;

    @Inject
    MVCommandCompletions(
            @NotNull MVCommandManager mvCommandManager,
            @NotNull WorldManager worldManager,
            @NotNull DestinationsProvider destinationsProvider,
            @NotNull CoreConfig config,
            @NotNull CorePermissionsChecker corePermissionsChecker,
            @NotNull AnchorManager anchorManager) {
        super(mvCommandManager);
        this.commandManager = mvCommandManager;
        this.worldManager = worldManager;
        this.destinationsProvider = destinationsProvider;
        this.config = config;
        this.corePermissionsChecker = corePermissionsChecker;
        this.anchorManager = anchorManager;

        registerAsyncCompletion("anchornames", this::suggestAnchorNames);
        registerAsyncCompletion("commands", this::suggestCommands);
        registerAsyncCompletion("destinations", this::suggestDestinations);
        registerStaticCompletion("difficulties", suggestEnums(Difficulty.class));
        registerStaticCompletion("environments", suggestEnums(World.Environment.class));
        registerAsyncCompletion("flags", this::suggestFlags);
        registerStaticCompletion("gamemodes", suggestEnums(GameMode.class));
        registerStaticCompletion("gamerules", this::suggestGamerules);
        registerAsyncCompletion("gamerulesvalues", this::suggestGamerulesValues);
        registerStaticCompletion("materials", suggestEnums(Material.class));
        registerStaticCompletion("mvconfigs", config.getStringPropertyHandle().getAllPropertyNames());
        registerAsyncCompletion("mvconfigvalues", this::suggestMVConfigValues);
        registerAsyncCompletion("mvworlds", this::suggestMVWorlds);
        registerAsyncCompletion("mvworldpropsname", this::suggestMVWorldPropsName);
        registerAsyncCompletion("mvworldpropsvalue", this::suggestMVWorldPropsValue);
        registerAsyncCompletion("playersarray", this::suggestPlayersArray);
        registerStaticCompletion("propsmodifyaction", suggestEnums(PropertyModifyAction.class));
        registerStaticCompletion("spawncategories", suggestEnums(SpawnCategory.class));
        registerAsyncCompletion("spawncategorypropsname", this::suggestSpawnCategoryPropsName);
        registerAsyncCompletion("spawncategorypropsvalue", this::suggestSpawnCategoryPropsValue);

        setDefaultCompletion("destinations", DestinationInstance.class);
        setDefaultCompletion("difficulties", Difficulty.class);
        setDefaultCompletion("environments", World.Environment.class);
        setDefaultCompletion("flags", String[].class);
        setDefaultCompletion("gamemodes", GameMode.class);
        setDefaultCompletion("gamerules", GameRule.class);
        setDefaultCompletion("mvworlds", LoadedMultiverseWorld.class);
    }

    @Override
    public CommandCompletionHandler registerCompletion(String id, CommandCompletionHandler<BukkitCommandCompletionContext> handler) {
        return super.registerCompletion(id, context ->
                completeWithPreconditions(context, handler));
    }

    @Override
    public CommandCompletionHandler registerAsyncCompletion(String id, AsyncCommandCompletionHandler<BukkitCommandCompletionContext> handler) {
        return super.registerAsyncCompletion(id, context ->
                completeWithPreconditions(context, handler));
    }

    private Collection<String> completeWithPreconditions(
            BukkitCommandCompletionContext context,
            CommandCompletionHandler<BukkitCommandCompletionContext> handler) {
        if (context.hasConfig("playerOnly") && !context.getIssuer().isPlayer()) {
            return Collections.emptyList();
        }
        if (context.hasConfig("resolveUntil")) {
            if (!Try.run(() -> context.getContextValueByName(Object.class, context.getConfig("resolveUntil"))).isSuccess()) {
                return Collections.emptyList();
            }
        }
        if (context.hasConfig("checkPermissions")) {
            for (String permission : REPatterns.SEMICOLON.split(context.getConfig("checkPermissions"))) {
                if (!commandManager.getCommandPermissions().hasPermission(context.getIssuer(), permission)) {
                    return Collections.emptyList();
                }
            }
        }
        if (context.hasConfig("multiple")) {
            return addonToCommaSeperated(context.getInput(), handler.getCompletions(context));
        }
        return handler.getCompletions(context);
    }

    /**
     * Shortcut to suggest enums values
     *
     * @param enumClass The enum class with values
     * @return A collection of possible string values
     * @param <T> The enum type
     */
    public <T extends Enum<T>> Collection<String> suggestEnums(Class<T> enumClass) {
        return EnumSet.allOf(enumClass).stream()
                .map(Enum::name)
                .map(String::toLowerCase)
                .toList();
    }

    private Collection<String> suggestAnchorNames(BukkitCommandCompletionContext context) {
        return anchorManager.getAnchors(context.getPlayer()).stream()
                .map(MultiverseAnchor::getName)
                .toList();
    }

    private Collection<String> suggestCommands(BukkitCommandCompletionContext context) {
        String rootCmdName = context.getConfig();
        if (rootCmdName == null) {
            return Collections.emptyList();
        }

        RootCommand rootCommand = this.commandManager.getRegisteredRootCommands().stream()
                .unordered()
                .filter(c -> c.getCommandName().equals(rootCmdName))
                .findFirst()
                .orElse(null);

        if (rootCommand == null) {
            return Collections.emptyList();
        }

        return rootCommand.getSubCommands().entries().stream()
                .filter(entry -> checkPerms(context.getIssuer(), entry.getValue()))
                .map(Map.Entry::getKey)
                .filter(cmdName -> !cmdName.startsWith("__"))
                .collect(Collectors.toList());
    }

    private boolean checkPerms(CommandIssuer issuer, RegisteredCommand<?> command) {
        return this.commandManager.hasPermission(issuer, command.getRequiredPermissions());
    }

    private Collection<String> suggestDestinations(BukkitCommandCompletionContext context) {
        return Try.of(() -> context.getContextValue(Player[].class))
                .map(players -> {
                    if (players.length == 0) {
                        // Most likely console did not specify a player
                        return Collections.<String>emptyList();
                    }
                    if (context.hasConfig("othersOnly") && (players.length == 1 && players[0].equals(context.getIssuer().getIssuer()))) {
                        return Collections.<String>emptyList();
                    }
                    return suggestDestinationsWithPerms(context.getIssuer().getIssuer(), players, context.getInput());
                })
                .getOrElse(Collections.emptyList());
    }

    private Collection<String> suggestDestinationsWithPerms(CommandSender teleporter, Player[] players, String deststring) {
        return destinationsProvider.suggestDestinations(teleporter, deststring).stream()
                .filter(packet -> corePermissionsChecker
                        .checkDestinationPacketPermission(teleporter, Arrays.asList(players), packet))
                .map(packet -> packet.destination() instanceof WorldDestination
                                ? packet.destinationString()
                                : packet.destination().getIdentifier() + ":" + packet.destinationString())
                .toList();
    }

    private Collection<String> suggestFlags(@NotNull BukkitCommandCompletionContext context) {
        String groupName = context.getConfig("groupName", "");

        return Try.of(() -> context.getContextValue(String[].class))
                .map(flags -> commandManager.getFlagsManager().suggest(groupName, flags))
                .getOrElse(Collections.emptyList());
    }

    private Collection<String> suggestGamerules() {
        return Arrays.stream(GameRule.values()).map(GameRule::getName).collect(Collectors.toList());
    }

    private Collection<String> suggestGamerulesValues(BukkitCommandCompletionContext context) {
       return Try.of(() -> context.getContextValue(GameRule.class))
               // Just use our suggester from configuration lib since gamerules are only boolean or int
               .mapTry(gamerule -> Option.of(DefaultSuggesterProvider.getDefaultSuggester(gamerule.getType()))
                       .map(s -> s.suggest(context.getInput())).getOrElse(Collections.emptyList()))
               .getOrElse(Collections.emptyList());
    }

    private Collection<String> suggestMVConfigValues(BukkitCommandCompletionContext context) {
        return Try.of(() -> context.getContextValue(String.class))
                .map(propertyName -> config.getStringPropertyHandle()
                        .getSuggestedPropertyValue(propertyName, context.getInput(), PropertyModifyAction.SET))
                .getOrElse(Collections.emptyList());
    }

    private Collection<String> suggestMVWorlds(BukkitCommandCompletionContext context) {
        String scope = context.getConfig("scope", "loaded");
        switch (scope) {
            case "both" -> {
                return worldManager.getWorlds().stream()
                        .map(MultiverseWorld::getTabCompleteName)
                        .map(StringFormatter::quoteMultiWordString)
                        .toList();
            }
            case "loaded" -> {
                return worldManager.getLoadedWorlds().stream()
                        .map(MultiverseWorld::getTabCompleteName)
                        .map(StringFormatter::quoteMultiWordString)
                        .toList();
            }
            case "unloaded" -> {
                return worldManager.getUnloadedWorlds().stream()
                        .map(MultiverseWorld::getTabCompleteName)
                        .map(StringFormatter::quoteMultiWordString)
                        .toList();
            }
            case "potential" -> {
                return worldManager.getPotentialWorlds();
            }
        }
        Logging.severe("Invalid MVWorld scope: " + scope);
        return Collections.emptyList();
    }

    private Collection<String> suggestMVWorldPropsName(BukkitCommandCompletionContext context) {
        return Try.of(() -> {
            MultiverseWorld world = context.getContextValue(MultiverseWorld.class);
            PropertyModifyAction action = context.getContextValue(PropertyModifyAction.class);
            return world.getStringPropertyHandle().getModifiablePropertyNames(action);
        }).getOrElse(Collections.emptyList());
    }

    private Collection<String> suggestMVWorldPropsValue(BukkitCommandCompletionContext context) {
        return Try.of(() -> {
            MultiverseWorld world = context.getContextValue(MultiverseWorld.class);
            PropertyModifyAction action = context.getContextValue(PropertyModifyAction.class);
            String propertyName = context.getContextValue(String.class);
            return world.getStringPropertyHandle().getSuggestedPropertyValue(propertyName, context.getInput(), action);
        }).getOrElse(Collections.emptyList());
    }

    private Collection<String> suggestPlayersArray(BukkitCommandCompletionContext context) {
        CommandSender sender = context.getSender();
        Validate.notNull(sender, "Sender cannot be null");
        Player senderPlayer = sender instanceof Player ? (Player)sender : null;
        List<String> matchedPlayers = new ArrayList<>();

        for(Player player : Bukkit.getOnlinePlayers()) {
            String name = player.getName();
            if ((senderPlayer == null || senderPlayer.canSee(player))
                    && (!context.hasConfig("excludeSelf") || !player.equals(senderPlayer))) {
                matchedPlayers.add(name);
            }
        }
        return addonToCommaSeperated(context.getInput(), matchedPlayers);
    }

    private Collection<String> suggestSpawnCategoryPropsName(BukkitCommandCompletionContext context) {
        return Try.of(() -> {
            MultiverseWorld world = context.getContextValue(MultiverseWorld.class);
            SpawnCategory spawnCategory = context.getContextValue(SpawnCategory.class);
            PropertyModifyAction action = context.getContextValue(PropertyModifyAction.class);
            return world.getEntitySpawnConfig()
                    .getSpawnCategoryConfig(spawnCategory)
                    .getStringPropertyHandle()
                    .getModifiablePropertyNames(action);
        }).getOrElse(Collections.emptyList());
    }

    private Collection<String> suggestSpawnCategoryPropsValue(BukkitCommandCompletionContext context) {
        return Try.of(() -> {
            MultiverseWorld world = context.getContextValue(MultiverseWorld.class);
            SpawnCategory spawnCategory = context.getContextValue(SpawnCategory.class);
            PropertyModifyAction action = context.getContextValue(PropertyModifyAction.class);
            String propertyName = context.getContextValue(String.class);
            return world.getEntitySpawnConfig()
                    .getSpawnCategoryConfig(spawnCategory)
                    .getStringPropertyHandle()
                    .getSuggestedPropertyValue(propertyName, context.getInput(), action);
        }).getOrElse(Collections.emptyList());
    }
}
