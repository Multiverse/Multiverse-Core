package org.mvplugins.multiverse.core.config;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import jakarta.inject.Provider;
import org.bukkit.plugin.PluginManager;

import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.ConfirmMode;
import org.mvplugins.multiverse.core.configuration.node.ConfigHeaderNode;
import org.mvplugins.multiverse.core.configuration.node.ConfigNode;
import org.mvplugins.multiverse.core.configuration.node.Node;
import org.mvplugins.multiverse.core.configuration.node.NodeGroup;
import org.mvplugins.multiverse.core.event.MVDebugModeEvent;
import org.mvplugins.multiverse.core.exceptions.MultiverseException;
import org.mvplugins.multiverse.core.permissions.PermissionUtils;

import java.util.Locale;

final class MVCoreConfigNodes {

    private final NodeGroup nodes = new NodeGroup();
    private PluginManager pluginManager;
    private Provider<MVCommandManager> commandManager;

    MVCoreConfigNodes(@NotNull PluginManager pluginManager, @NotNull Provider<MVCommandManager> commandManager) {
        this.pluginManager = pluginManager;
        this.commandManager = commandManager;
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

    final ConfigNode<Boolean> enforceAccess = node(ConfigNode.builder("world.enforce-access", Boolean.class)
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

    final ConfigNode<Boolean> autoPurgeEntities = node(ConfigNode.builder("world.auto-purge-entities", Boolean.class)
            .comment("")
            .comment("Sets whether Multiverse will purge mobs and entities automatically.")
            .defaultValue(false)
            .name("auto-purge-entities")
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
            .comment("Sets whether Multiverse will override the first spawn location of a world.")
            .comment("If enabled, Multiverse will set the first spawn location of a world to the spawn location of the world.")
            .comment("If disabled, it will default to server.properties settings.")
            .defaultValue(true)
            .name("first-spawn-override")
            .build());

    final ConfigNode<String> firstSpawnLocation = node(ConfigNode.builder("spawn.first-spawn-location", String.class)
            .comment("")
            .comment("Sets the world that Multiverse will use as the location for players that first join the server.")
            .comment("This only applies if first-spawn-override is set to true.")
            .defaultValue("")
            .name("first-spawn-location")
            .build());

    final ConfigNode<Boolean> enableJoinDestination = node(ConfigNode.builder("spawn.enable-join-destination", Boolean.class)
            .comment("")
            .comment("Enables join-destination below.")
            .defaultValue(false)
            .name("enable-join-destination")
            .build());

    final ConfigNode<String> joinDestination = node(ConfigNode.builder("spawn.join-destination", String.class)
            .comment("")
            .comment("Sets the destination that Multiverse will use to spawn players on every login")
            .comment("Set the above enable-join-destination to false to disable")
            .defaultValue("")
            .name("join-destination")
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
            .comment("This config will only apply if `respawn-world` is set, or `default-respawn-within-same-world` is enabled.")
            .comment("----")
            .comment("When this option is enabled, players will always respawn at the world's spawn location of `respawn-world`,")
            .comment("unless bed or anchor is set and `bed-respawn` or `anchor-spawn` is enabled respectively.")
            .comment("If respawn-world is set, Multiverse will use that world's spawn location, else it will use the world's spawn where the player died in.")
            .comment("----")
            .comment("Set this to false if you want to use the /spawnpoint instead of the world's spawn location.")
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
            .comment("This only applies if use-custom-portal-search is set to true.")
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
            .onSetValue((oldValue, newValue) -> {
                commandManager.get().getLocales().setDefaultLocale(newValue);
            })
            .build());

    final ConfigNode<Boolean> perPlayerLocale = node(ConfigNode.builder("messaging.per-player-locale", Boolean.class)
            .comment("")
            .comment("This config option defines if Multiverse should use the player's language based on their client's language.")
            .comment("If the player's language does not have a translation, it will use the default language set above instead.")
            .defaultValue(true)
            .name("per-player-locale")
            .onSetValue((oldValue, newValue) -> {
                // autoDetectFromClient will be done by MVLocalesListener instead
                commandManager.get().usePerIssuerLocale(newValue, false);
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

    private final ConfigHeaderNode miscHeader = node(ConfigHeaderNode.builder("misc")
            .comment("")
            .comment("")
            .build());

    final ConfigNode<Integer> globalDebug = node(ConfigNode.builder("misc.global-debug", Integer.class)
            .comment("This is our debug flag to help identify issues with Multiverse.")
            .comment("If you are having issues with Multiverse, please set this to 3 and then post your log to pastebin.com")
            .comment("Otherwise, there's no need to touch this. If not instructed by a wiki page or developer.")
            .comment("  0 = Off, No debug messages")
            .comment("  1 = fine")
            .comment("  2 = finer")
            .comment("  3 = finest")
            .defaultValue(0)
            .name("global-debug")
            .validator(value -> (value < 0 || value > 3)
                    ? Try.failure(new MultiverseException("Debug level must be between 0 and 3."))
                    : Try.success(null))
            .onSetValue((oldValue, newValue) -> {
                int level = Logging.getDebugLevel();
                Logging.setDebugLevel(newValue);
                if (level != Logging.getDebugLevel()) {
                    pluginManager.callEvent(new MVDebugModeEvent(level));
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
            .name(null)
            .build());

    // END CHECKSTYLE-SUPPRESSION: Javadoc
    // END CHECKSTYLE-SUPPRESSION: MemberName
    // END CHECKSTYLE-SUPPRESSION: Abbreviation
    // END CHECKSTYLE-SUPPRESSION: VisibilityModifier
    // END CHECKSTYLE-SUPPRESSION: MultipleStringLiterals
    // END CHECKSTYLE-SUPPRESSION: LineLength
}
