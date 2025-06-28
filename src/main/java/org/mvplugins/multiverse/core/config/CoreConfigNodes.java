package org.mvplugins.multiverse.core.config;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.PluginManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.queue.ConfirmMode;
import org.mvplugins.multiverse.core.config.node.ConfigHeaderNode;
import org.mvplugins.multiverse.core.config.node.ConfigNode;
import org.mvplugins.multiverse.core.config.node.Node;
import org.mvplugins.multiverse.core.config.node.NodeGroup;
import org.mvplugins.multiverse.core.config.node.functions.NodeStringParser;
import org.mvplugins.multiverse.core.config.node.serializer.NodeSerializer;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.dynamiclistener.EventPriorityMapper;
import org.mvplugins.multiverse.core.event.MVDebugModeEvent;
import org.mvplugins.multiverse.core.exceptions.MultiverseException;
import org.mvplugins.multiverse.core.permissions.PermissionUtils;
import org.mvplugins.multiverse.core.teleportation.PassengerModes;
import org.mvplugins.multiverse.core.world.helpers.DimensionFinder.DimensionFormat;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Service
final class CoreConfigNodes {

    private final NodeGroup nodes = new NodeGroup();
    private PluginManager pluginManager;
    private Provider<MVCommandManager> commandManager;
    private final Provider<DestinationsProvider> destinationsProvider;
    private Provider<EventPriorityMapper> eventPriorityMapper;

    @Inject
    CoreConfigNodes(
            @NotNull PluginManager pluginManager,
            @NotNull Provider<MVCommandManager> commandManager,
            @NotNull Provider<DestinationsProvider> destinationsProvider,
            @NotNull Provider<EventPriorityMapper> eventPriorityMapper) {
        this.pluginManager = pluginManager;
        this.commandManager = commandManager;
        this.destinationsProvider = destinationsProvider;
        this.eventPriorityMapper = eventPriorityMapper;
    }

    NodeGroup getNodes() {
        return nodes;
    }

    private <N extends Node> N node(N node) {
        nodes.add(node);
        return node;
    }

    // BEGIN CHECKSTYLE-SUPPRESSION: Javadoc
    // BEGIN CHECKSTYLE-SUPPRESSION: MemberName
    // BEGIN CHECKSTYLE-SUPPRESSION: Abbreviation
    // BEGIN CHECKSTYLE-SUPPRESSION: VisibilityModifier
    // BEGIN CHECKSTYLE-SUPPRESSION: MultipleStringLiterals
    // BEGIN CHECKSTYLE-SUPPRESSION: LineLength

    private final ConfigHeaderNode worldHeader = node(ConfigHeaderNode.builder("world")
            .comment("####################################################################################################")
            .comment("#                                                                                                  #")
            .comment("#                    █▀▄▀█ █░█ █░░ ▀█▀ █ █░█ █▀▀ █▀█ █▀ █▀▀   █▀▀ █▀█ █▀█ █▀▀                      #")
            .comment("#                    █░▀░█ █▄█ █▄▄ ░█░ █ ▀▄▀ ██▄ █▀▄ ▄█ ██▄   █▄▄ █▄█ █▀▄ ██▄                      #")
            .comment("#                                                                                                  #")
            .comment("#                                                                                                  #")
            .comment("#    WIKI:        https://github.com/Multiverse/Multiverse-Core/wiki                               #")
            .comment("#    DISCORD:     https://discord.gg/NZtfKky                                                       #")
            .comment("#    BUG REPORTS: https://github.com/Multiverse/Multiverse-Core/issues                             #")
            .comment("#                                                                                                  #")
            .comment("#                                                                                                  #")
            .comment("#    Each option in this file is documented and explained here:                                    #")
            .comment("#     ==>  https://github.com/Multiverse/Multiverse-Core/wiki/config.yml                           #")
            .comment("#                                                                                                  #")
            .comment("#                                                                                                  #")
            .comment("#    New options are added to this file automatically. If you manually made changes                #")
            .comment("#    to this file while your server is running, please run `/mv reload` command.                   #")
            .comment("#                                                                                                  #")
            .comment("####################################################################################################")
            .comment("")
            .comment("")
            .build());

    final ConfigNode<Boolean> autoImportDefaultWorlds = node(ConfigNode.builder("world.auto-import-default-worlds", Boolean.class)
            .comment("When enabled, Multiverse will automatically import default worlds defined in the server.properties")
            .comment("`level-name` property when the Multiverse is enabled or reloaded. This will include the nether and ")
            .comment("end if the server created them.")
            .defaultValue(true)
            .name("auto-import-default-worlds")
            .build());

    final ConfigNode<Boolean> autoImport3rdPartyWorlds = node(ConfigNode.builder("world.auto-import-3rd-party-worlds", Boolean.class)
            .comment("")
            .comment("When enabled, Multiverse will import all other worlds created by other plugins when Multiverse starts")
            .comment("or when Multiverse is reloaded.")
            .defaultValue(true)
            .name("auto-import-3rd-party-worlds")
            .build());

    final ConfigNode<Boolean> enforceAccess = node(ConfigNode.builder("world.enforce-access", Boolean.class)
            .comment("")
            .comment("This setting will prevent players from entering worlds they don't have access to.")
            .comment("If this is set to false, players will be able to enter any world they want.")
            .comment("If this is set to true, players will only be able to enter worlds they have")
            .comment("the `mv.access.<worldname>` permission.")
            .defaultValue(false)
            .name("enforce-access")
            .build());

    final ConfigNode<Boolean> enforceGamemode = node(ConfigNode.builder("world.enforce-gamemode", Boolean.class)
            .comment("")
            .comment("Sets whether Multiverse will should enforce gamemode on world change.")
            .comment("If enabled, players will be forced into the gamemode of the world they are entering, unless they have")
            .comment("the `mv.bypass.gamemode.<worldname>` permission.")
            .defaultValue(true)
            .name("enforce-gamemode")
            .build());

    final ConfigNode<Boolean> enforceFlight = node(ConfigNode.builder("world.enforce-flight", Boolean.class)
            .comment("")
            .comment("Sets whether Multiverse will should globally enforce flight ability on worlds.")
            .comment("Disable this if you want another plugin to handle player's flight ability.")
            .comment("Disabling this will make the world property `allow-flight` have no effect.")
            .defaultValue(true)
            .name("enforce-flight")
            .build());

    final ConfigNode<Boolean> autoPurgeEntities = node(ConfigNode.builder("world.auto-purge-entities", Boolean.class)
            .comment("")
            .comment("Sets whether Multiverse will purge entities on world load based world's entity spawn config.")
            .defaultValue(false)
            .name("auto-purge-entities")
            .build());

    private final ConfigHeaderNode worldNameFormat = node(ConfigHeaderNode.builder("world.world-name-format")
            .comment("")
            .comment("Format for world names for multiverse to automatically detect a world group consist of overworld, nether and end.")
            .comment("This is used default-respawn-in-overworld and potentially other features.")
            .build());

    final ConfigNode<DimensionFormat> netherWorldNameFormat = node(ConfigNode.builder("world.world-name-format.nether", DimensionFormat.class)
            .defaultValue(() -> new DimensionFormat("%overworld%_nether"))
            .name("nether-world-name-format")
            .serializer(DimensionFormatNodeSerializer.INSTANCE)
            .stringParser(DimensionFormatNodeStringParser.INSTANCE)
            .build());

    final ConfigNode<DimensionFormat> endWorldNameFormat = node(ConfigNode.builder("world.world-name-format.end", DimensionFormat.class)
            .defaultValue(() -> new DimensionFormat("%overworld%_the_end"))
            .name("end-world-name-format")
            .serializer(DimensionFormatNodeSerializer.INSTANCE)
            .stringParser(DimensionFormatNodeStringParser.INSTANCE)
            .build());

    private final ConfigHeaderNode teleportHeader = node(ConfigHeaderNode.builder("teleport")
            .comment("")
            .comment("")
            .build());

    final ConfigNode<Boolean> useFinerTeleportPermissions = node(ConfigNode.builder("teleport.use-finer-teleport-permissions", Boolean.class)
            .comment("Sets whether Multiverse will use more fine-grained teleport permissions.")
            .comment("----")
            .comment("New finer teleport permissions for /mvtp and /mvspawn commands:")
            .comment("  - For specific teleport types: `multiverse.teleport.<self|other>.<type>.<target>`")
            .comment("  - For specific world spawn: `multiverse.core.spawn.<self|other>.<worldname>`")
            .comment("For example, if `multiverse.teleport.self.w.world2` is set, Multiverse will only allow the player to teleport to the world2.")
            .comment("----")
            .comment("Legacy permissions will be used if this is set to false:")
            .comment("  - For teleport destinations: `multiverse.teleport.<self|other>.<type>`")
            .comment("  - For spawn: `multiverse.core.spawn.<self|other>`")
            .defaultValue(true)
            .name("use-finer-teleport-permissions")
            .build());

    final ConfigNode<PassengerModes> passengerMode = node(ConfigNode.builder("teleport.passenger-mode", PassengerModes.class)
            .comment("")
            .comment("Configures how passengers and vehicles are handled when an entity is teleported.")
            .comment("  default: Server will handle passengers and vehicles, this usually means entities will not be teleported to a different world if they have passengers.")
            .comment("  dismount_passengers: Passengers will be removed from the parent entity before the teleport.")
            .comment("  dismount_vehicle: Vehicle will be removed and from the parent entity before the teleport.")
            .comment("  dismount_all: All passengers and vehicles will be removed from the parent entity before the teleport.")
            .comment("  retain_passengers: Passengers will teleport together with the parent entity.")
            .comment("  retain_vehicle: Vehicles will teleport together with the parent entity.")
            .comment("  retain_all: All passengers and vehicles will teleport together with the parent entity.")
            .defaultValue(PassengerModes.DEFAULT)
            .name("passenger-mode")
            .build());

    final ConfigNode<Integer> concurrentTeleportLimit = node(ConfigNode.builder("teleport.concurrent-teleport-limit", Integer.class)
            .comment("")
            .comment("Sets the maximum number of players allowed to be teleported at once with `/mv teleport` command")
            .defaultValue(50)
            .name("concurrent-teleport-limit")
            .build());

    final ConfigNode<Boolean> teleportIntercept = node(ConfigNode.builder("teleport.teleport-intercept", Boolean.class)
            .comment("")
            .comment("If this is set to true, Multiverse will enforce access permissions for all teleportation,")
            .comment("including teleportation from other plugins. You should not disable this unless you are facing")
            .comment("conflict with another plugin handling teleportation.")
            .defaultValue(true)
            .name("teleport-intercept")
            .build());

    final ConfigNode<Integer> safeLocationHorizontalSearchRadius = node(ConfigNode.builder("teleport.safe-location-horizontal-search-radius", Integer.class)
            .comment("")
            .comment("Sets the horizontal (x and z-axis) search radius for finding a safe location to teleport to.")
            .comment("Increasing this value will widen the search area at the cost of performance.")
            .comment("To disable, set to 0.")
            .defaultValue(3)
            .name("safe-location-horizontal-search-radius")
            .build());

    final ConfigNode<Integer> safeLocationVerticalSearchRadius = node(ConfigNode.builder("teleport.safe-location-vertical-search-radius", Integer.class)
            .comment("")
            .comment("Sets the vertical (y-axis) search radius for finding a safe location to teleport to.")
            .comment("Increasing this value will widen the search area at the cost of performance.")
            .comment("To disable, set to 0.")
            .defaultValue(3)
            .name("safe-location-vertical-search-radius")
            .build());

    private final ConfigHeaderNode spawnHeader = node(ConfigHeaderNode.builder("spawn")
            .comment("")
            .comment("")
            .build());

    final ConfigNode<Boolean> firstSpawnOverride = node(ConfigNode.builder("spawn.first-spawn-override", Boolean.class)
            .comment("Sets whether Multiverse will override the location where the player spawns when they join the server")
            .comment("for the first time. For fixed spawn location on every login, see the `join-destination` option below.")
            .comment("If disabled, it will default to server.properties settings.")
            .defaultValue(false)
            .name("first-spawn-override")
            .build());

    final ConfigNode<String> firstSpawnLocation = node(ConfigNode.builder("spawn.first-spawn-location", String.class)
            .comment("")
            .comment("Sets the world that Multiverse will use as the location for players that join the server for the first time.")
            .comment("This only applies if first-spawn-override is set to true.")
            .defaultValue("")
            .name("first-spawn-location")
            .suggester(this::suggestDestinations)
            .stringParser(this::parseDestinationString)
            .build());

    final ConfigNode<Boolean> enableJoinDestination = node(ConfigNode.builder("spawn.enable-join-destination", Boolean.class)
            .comment("")
            .comment("Enables setting of a fixed location for players to spawn in when they join the server every time.")
            .comment("See `join-destination` option below as well.")
            .defaultValue(false)
            .name("enable-join-destination")
            .build());

    final ConfigNode<String> joinDestination = node(ConfigNode.builder("spawn.join-destination", String.class)
            .comment("")
            .comment("Sets the destination that Multiverse will use to spawn players on every login.")
            .comment("Set the above enable-join-destination to false to disable")
            .defaultValue("")
            .name("join-destination")
            .suggester(this::suggestDestinations)
            .stringParser(this::parseDestinationString)
            .build());

    final ConfigNode<Boolean> defaultRespawnInOverworld = node(ConfigNode.builder("spawn.default-respawn-in-overworld", Boolean.class)
            .comment("")
            .comment("This only applies if the `respawn-world` property is not set for the world that the player died in,")
            .comment("and the player does not have bed or anchor set.")
            .comment("----")
            .comment("When this option is enabled, players will respawn in the overworld when dying in nether or end, mimicking the vanilla behavior.")
            .comment("The automatic selection of overworld is determined by the `world-name-format` config section above.")
            .comment("This option takes precedence over the `default-respawn-within-same-world` option.")
            .comment("----")
            .comment("Set this to false if you want another plugin to handle respawning or do not want this vanilla behavior.")
            .defaultValue(true)
            .name("default-respawn-in-overworld")
            .build());

    final ConfigNode<Boolean> defaultRespawnWithinSameWorld = node(ConfigNode.builder("spawn.default-respawn-within-same-world", Boolean.class)
            .comment("")
            .comment("This only applies if the `respawn-world` property is not set for the world that the player died in,")
            .comment("and the player does not have bed or anchor set.")
            .comment("----")
            .comment("When this option is enabled, players will respawn in the same world's that they died in.")
            .comment("If the /spawnpoint is already within that world and `enforce-respawn-at-world-spawn` is disabled,")
            .comment("Multiverse will use that spawn location, else it will use the world's spawn where the player died in.")
            .comment("----")
            .comment("You can set `respawn-world` property with the command: `/mv modify <worldname> set respawn-world <worldname>`")
            .comment("You can reset `respawn-world` property with the command: `/mv modify <worldname> reset respawn-world`")
            .comment("----")
            .comment("Set this to false if you want another plugin to handle respawning.")
            .defaultValue(true)
            .name("default-respawn-within-same-world")
            .build());

    final ConfigNode<Boolean> enforceRespawnAtWorldSpawn = node(ConfigNode.builder("spawn.enforce-respawn-at-world-spawn", Boolean.class)
            .comment("")
            .comment("When this option is enabled, players will always respawn at the world's spawn location of calculated respawn world,")
            .comment("unless bed or anchor is set and `bed-respawn` or `anchor-spawn` is enabled respectively.")
            .comment("----")
            .comment("Set this to false if you want to use a custom spawn location such as /spawnpoint instead of the world's spawn location.")
            .defaultValue(true)
            .name("enforce-respawn-at-world-spawn")
            .build());

    private final ConfigHeaderNode portalHeader = node(ConfigHeaderNode.builder("portal")
            .comment("")
            .comment("")
            .build());

    final ConfigNode<Boolean> useCustomPortalSearch = node(ConfigNode.builder("portal.use-custom-portal-search", Boolean.class)
            .comment("This config option defines whether or not Multiverse should interfere with's Bukkit's default portal search radius.")
            .comment("Setting it to false would mean you want to simply let Bukkit decides the search radius itself.")
            .defaultValue(false)
            .name("use-custom-portal-search")
            .build());

    final ConfigNode<Integer> customPortalSearchRadius = node(ConfigNode.builder("portal.custom-portal-search-radius", Integer.class)
            .comment("")
            .comment("This config option defines the search radius Multiverse should use when searching for a portal.")
            .comment("This only applies if use-custom-portal-search is set to true.")
            .defaultValue(128)
            .name("custom-portal-search-radius")
            .validator(value -> value < 0
                    ? Try.failure(new MultiverseException("The value must be greater than or equal to 0.", null))
                    : Try.success(null))
            .build());

    private final ConfigHeaderNode MESSAGING_HEADER = node(ConfigHeaderNode.builder("messaging")
            .comment("")
            .comment("")
            .build());

    final ConfigNode<Boolean> enableChatPrefix = node(ConfigNode.builder("messaging.enable-chat-prefix", Boolean.class)
            .comment("This config option defines whether or not Multiverse should prefix the chat with the world name.")
            .comment("Ensure this is false if you want another plugin to handle chat formatting.")
            .defaultValue(false)
            .name("enable-chat-prefix")
            .build());

    final ConfigNode<String> chatPrefixFormat = node(ConfigNode.builder("messaging.chat-prefix-format", String.class)
            .comment("")
            .comment("This config option defines the format Multiverse should use when prefixing the chat with the world name.")
            .comment("This only applies if enable-chat-prefix is set to true.")
            .defaultValue("[%world%]%chat%")
            .name("chat-prefix-format")
            .build());

    final ConfigNode<Boolean> registerPapiHook = node(ConfigNode.builder("messaging.register-papi-hook", Boolean.class)
            .comment("")
            .comment("This config option defines whether or not Multiverse should register the PlaceholderAPI hook.")
            .comment("This only applies if PlaceholderAPI is installed.")
            .defaultValue(true)
            .name("register-papi-hook")
            .build());

    final ConfigNode<Locale> defaultLocale = node(ConfigNode.builder("messaging.default-locale", Locale.class)
            .comment("")
            .comment("This config option defines the default language Multiverse should use.")
            .defaultValue(Locale.ENGLISH)
            .name("default-locale")
            .onSetValue((oldValue, newValue) ->
                    commandManager.get().getLocales().setDefaultLocale(newValue))
            .build());

    final ConfigNode<Boolean> perPlayerLocale = node(ConfigNode.builder("messaging.per-player-locale", Boolean.class)
            .comment("")
            .comment("This config option defines if Multiverse should use the player's language based on their client's language.")
            .comment("If the player's language does not have a translation, it will use the default language set above instead.")
            .defaultValue(true)
            .name("per-player-locale")
            .onSetValue((oldValue, newValue) -> {
                commandManager.get().usePerIssuerLocale(newValue);
            })
            .build());

    private final ConfigHeaderNode commandHeader = node(ConfigHeaderNode.builder("command")
            .comment("")
            .comment("")
            .build());

    final ConfigNode<Boolean> resolveAliasName = node(ConfigNode.builder("command.resolve-alias-name", Boolean.class)
            .comment("If this is set to true, Multiverse will resolve world based on their alias names for commands and destinations.")
            .comment("Normal world names will still be accepted.")
            .comment("In the event you have multiple worlds with the same alias name, the first world found will be used.")
            .defaultValue(true)
            .name("resolve-alias-name")
            .build());

    final ConfigNode<ConfirmMode> confirmMode = node(ConfigNode.builder("command.confirm-mode", ConfirmMode.class)
            .comment("")
            .comment("This config option defines whether `/mv confirm` is needed before running a DANGEROUS action.")
            .comment("  enable: `/mv confirm` is required.")
            .comment("  player_only: `/mv confirm` only required when running command as a player.")
            .comment("  disable_command_blocks: `/mv confirm` not required for command blocks.")
            .comment("  disable_console: `/mv confirm` not required for the console.")
            .comment("  disable: `/mv confirm` is not required.")
            .defaultValue(ConfirmMode.ENABLE)
            .name("confirm-mode")
            .build());

    final ConfigNode<Boolean> useConfirmOtp = node(ConfigNode.builder("command.use-confirm-otp", Boolean.class)
            .comment("")
            .comment("If this is set to true, `/mv confirm` will include a 3 digit random number that must be entered to confirm the command.")
            .comment("For example: `/mv confirm 726`")
            .defaultValue(true)
            .name("use-confirm-otp")
            .build());

    final ConfigNode<Integer> confirmTimeout = node(ConfigNode.builder("command.confirm-timeout", Integer.class)
            .comment("")
            .comment("The amount of time in seconds before `/mv confirm` times out")
            .defaultValue(30)
            .name("confirm-timeout")
            .validator(value -> (value <= 0)
                    ? Try.failure(new MultiverseException("Confirm timeout must be a positive number!"))
                    : Try.success(null))
            .build());

    final ConfigNode<Boolean> showLegacyAliases = node(ConfigNode.builder("command.show-legacy-aliases", Boolean.class)
            .comment("")
            .comment("If this is set to true, legacy aliases will be shown in tab completion.")
            .comment("These are old mv4 aliases such as `/mvclone` in addition to `/mv clone` which crowds the tab completion.")
            .comment("!!!NOTE: This will only apply after a server restart!")
            .defaultValue(false)
            .name("show-legacy-aliases")
            .build());

    private final ConfigHeaderNode eventPriorityHeader = node(ConfigHeaderNode.builder("event-priority")
            .comment("")
            .comment("")
            .build());

    final ConfigNode<EventPriority> eventPriorityPlayerPortal = node(ConfigNode.builder("event-priority.player-portal", EventPriority.class)
            .defaultValue(EventPriority.HIGH)
            .comment("The follow configuration changes the bukkit's EventPriority for certain events.")
            .comment("Only ever change this if you need multiverse's events outcomes to override another plugin, or if")
            .comment("you want another plugin's outcome to override multiverse's.")
            .comment("----")
            .comment("!!!NOTE: This will only apply after a server restart!")
            .comment("")
            .comment("This config option defines the priority for the PlayerPortalEvent.")
            .name("event-priority-player-portal")
            .onSetValue((oldValue, newValue) ->
                    eventPriorityMapper.get().setPriority("mvcore-player-portal", newValue))
            .build());

    final ConfigNode<EventPriority> eventPriorityPlayerRespawn = node(ConfigNode.builder("event-priority.player-respawn", EventPriority.class)
            .defaultValue(EventPriority.LOW)
            .name("event-priority-player-respawn")
            .comment("")
            .comment("This config option defines the priority for the PlayerRespawnEvent.")
            .onSetValue((oldValue, newValue) ->
                    eventPriorityMapper.get().setPriority("mvcore-player-respawn", newValue))
            .build());

    final ConfigNode<EventPriority> eventPriorityPlayerSpawnLocation = node(ConfigNode.builder("event-priority.player-spawn-location", EventPriority.class)
            .defaultValue(EventPriority.NORMAL)
            .comment("")
            .comment("This config option defines the priority for the PlayerSpawnLocationEvent.")
            .name("event-priority-player-spawn-location").onSetValue((oldValue, newValue) ->
                    eventPriorityMapper.get().setPriority("mvcore-player-spawn-location", newValue))
            .build());

    final ConfigNode<EventPriority> eventPriorityPlayerTeleport = node(ConfigNode.builder("event-priority.player-teleport", EventPriority.class)
            .defaultValue(EventPriority.HIGHEST)
            .name("event-priority-player-teleport")
            .comment("")
            .comment("This config option defines the priority for the PlayerTeleportEvent.")
            .onSetValue((oldValue, newValue) ->
                    eventPriorityMapper.get().setPriority("mvcore-player-teleport", newValue))
            .build());

    private final ConfigHeaderNode miscHeader = node(ConfigHeaderNode.builder("misc")
            .comment("")
            .comment("")
            .build());

    final ConfigNode<String> bukkitYmlPath = node(ConfigNode.builder("misc.bukkit-yml-path", String.class)
            .comment("Change this if you use a custom path for the bukkit.yml file with `--bukkit-settings` startup flag.")
            .comment("Note: this config option needs a server restart to take effect.")
            .defaultValue("bukkit.yml")
            .name("bukkit-yml-path")
            .build());

    final ConfigNode<Integer> globalDebug = node(ConfigNode.builder("misc.global-debug", Integer.class)
            .comment("")
            .comment("This is our debug flag to help identify issues with Multiverse.")
            .comment("If you are having issues with Multiverse, please set this to 3 and then post your log to pastebin.com")
            .comment("Otherwise, there's no need to touch this. If not instructed by a wiki page or developer.")
            .comment("  0 = Off, No debug messages")
            .comment("  1 = fine")
            .comment("  2 = finer")
            .comment("  3 = finest")
            .defaultValue(0)
            .name("global-debug")
            .suggester(input -> List.of("0", "1", "2", "3"))
            .validator(value -> (value < 0 || value > 3)
                    ? Try.failure(new MultiverseException("Debug level must be between 0 and 3."))
                    : Try.success(null))
            .onSetValue((oldValue, newValue) -> {
                if (newValue != Logging.getDebugLevel()) {
                    Logging.setDebugLevel(newValue);
                    pluginManager.callEvent(new MVDebugModeEvent(newValue));
                }
            })
            .build());

    final ConfigNode<Boolean> debugPermissions = node(ConfigNode.builder("misc.debug-permissions", Boolean.class)
            .comment("Sets whether console will log every permission check done by all multiverse plugins.")
            .comment("This will only work if the above 'global-debug' is set to 1 or more.")
            .defaultValue(false)
            .name("debug-permissions")
            .onSetValue((oldValue, newValue) -> PermissionUtils.setDebugPermissions(newValue))
            .build());

    final ConfigNode<Boolean> silentStart = node(ConfigNode.builder("misc.silent-start", Boolean.class)
            .comment("")
            .comment("If true, the startup console messages will no longer show.")
            .defaultValue(false)
            .name("silent-start")
            .onSetValue((oldValue, newValue) -> Logging.setShowingConfig(!newValue))
            .build());

    final ConfigNode<Boolean> showDonationMessage = node(ConfigNode.builder("misc.show-donation-message", Boolean.class)
            .comment("")
            .comment("If you don't want to donate, you can set this to false and Multiverse will stop nagging you.")
            .defaultValue(true)
            .name("show-donation-message")
            .build());

    final ConfigNode<Double> version = node(ConfigNode.builder("version", Double.class)
            .comment("")
            .comment("")
            .comment("This just signifies the version number so we can see what version of config you have.")
            .comment("NEVER TOUCH THIS VALUE")
            .defaultValue(0.0)
            .hidden()
            .build());

    private Collection<String> suggestDestinations(CommandSender sender, String input) {
        return destinationsProvider.get().suggestDestinationStrings(sender, input);
    }

    private Try<String> parseDestinationString(CommandSender sender, String input, Class<String> type) {
        return destinationsProvider.get().parseDestination(sender, input)
                .map(DestinationInstance::toString)
                .toTry();
    }

    private static final class DimensionFormatNodeSerializer implements NodeSerializer<DimensionFormat> {

        private static final DimensionFormatNodeSerializer INSTANCE = new DimensionFormatNodeSerializer();

        private DimensionFormatNodeSerializer() {}

        @Override
        public DimensionFormat deserialize(Object object, Class<DimensionFormat> type) {
            return new DimensionFormat(String.valueOf(object));
        }

        @Override
        public Object serialize(DimensionFormat dimensionFormat, Class<DimensionFormat> type) {
            return dimensionFormat.getFormat();
        }
    }

    private static final class DimensionFormatNodeStringParser implements NodeStringParser<DimensionFormat> {

        private static final DimensionFormatNodeStringParser INSTANCE = new DimensionFormatNodeStringParser();

        private DimensionFormatNodeStringParser() {}

        @Override
        public @NotNull Try<DimensionFormat> parse(@Nullable String string, @NotNull Class<DimensionFormat> type) {
            return Try.of(() -> new DimensionFormat(string));
        }
    }

    // END CHECKSTYLE-SUPPRESSION: Javadoc
    // END CHECKSTYLE-SUPPRESSION: MemberName
    // END CHECKSTYLE-SUPPRESSION: Abbreviation
    // END CHECKSTYLE-SUPPRESSION: VisibilityModifier
    // END CHECKSTYLE-SUPPRESSION: MultipleStringLiterals
    // END CHECKSTYLE-SUPPRESSION: LineLength
}
