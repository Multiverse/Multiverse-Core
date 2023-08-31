package com.onarandombox.MultiverseCore.config;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.configuration.node.ConfigHeaderNode;
import com.onarandombox.MultiverseCore.configuration.node.ConfigNode;
import com.onarandombox.MultiverseCore.configuration.node.Node;
import com.onarandombox.MultiverseCore.configuration.node.NodeGroup;
import com.onarandombox.MultiverseCore.event.MVDebugModeEvent;
import com.onarandombox.MultiverseCore.exceptions.MultiverseException;
import io.github.townyadvanced.commentedconfiguration.setting.CommentedNode;
import io.vavr.control.Try;
import org.bukkit.plugin.PluginManager;

class MVCoreConfigNodes {

    private final NodeGroup nodes = new NodeGroup();
    private PluginManager pluginManager;

    MVCoreConfigNodes(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public NodeGroup getNodes() {
        return nodes;
    }

    private <N extends Node> N node(N node) {
        nodes.add(node);
        return node;
    }

    private final ConfigHeaderNode HEADER = node(ConfigHeaderNode.builder("world") // TODO hacky way to get the header to the top of the file
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

//    private final ConfigHeaderNode WORLD_HEADER = node(ConfigHeaderNode.builder("world")
//            .comment("")
//            .comment("")
//            .build());

    public final ConfigNode<Boolean> ENFORCE_ACCESS = node(ConfigNode.builder("world.enforce-access", Boolean.class)
            .comment("This setting will prevent players from entering worlds they don't have access to.")
            .comment("If this is set to false, players will be able to enter any world they want.")
            .comment("If this is set to true, players will only be able to enter worlds they have")
            .comment("the `mv.access.<worldname>` permission.")
            .defaultValue(false)
            .name("enforce-access")
            .build());

    public final ConfigNode<Boolean> ENFORCE_GAMEMODE = node(ConfigNode.builder("world.enforce-gamemode", Boolean.class)
            .comment("")
            .comment("Sets whether Multiverse will should enforce gamemode on world change.")
            .comment("If enabled, players will be forced into the gamemode of the world they are entering, unless they have")
            .comment("the `mv.bypass.gamemode.<worldname>` permission.")
            .defaultValue(true)
            .name("enforce-gamemode")
            .build());

    public final ConfigNode<Boolean> AUTO_PURGE_ENTITIES = node(ConfigNode.builder("world.auto-purge-entities", Boolean.class)
            .comment("")
            .comment("Sets whether Multiverse will purge mobs and entities with be automatically.")
            .defaultValue(false)
            .name("auto-purge-entities")
            .build());

    public final ConfigNode<Boolean> TELEPORT_INTERCEPT = node(ConfigNode.builder("world.teleport-intercept", Boolean.class)
            .comment("")
            .comment("If this is set to true, Multiverse will enforce access permissions for all teleportation,")
            .comment("including teleportation from other plugins.")
            .defaultValue(true)
            .name("teleport-intercept")
            .build());

    private final ConfigHeaderNode SPAWN_HEADER = node(ConfigHeaderNode.builder("spawn")
            .comment("")
            .comment("")
            .build());

    public final ConfigNode<Boolean> FIRST_SPAWN_OVERRIDE = node(ConfigNode.builder("spawn.first-spawn-override", Boolean.class)
            .comment("Sets whether Multiverse will override the first spawn location of a world.")
            .comment("If enabled, Multiverse will set the first spawn location of a world to the spawn location of the world.")
            .comment("If disabled, it will default to server.properties settings.")
            .defaultValue(true)
            .name("first-spawn-override")
            .build());

    public final ConfigNode<String> FIRST_SPAWN_LOCATION = node(ConfigNode.builder("spawn.first-spawn-location", String.class)
            .comment("")
            .comment("Sets the world that Multiverse will use as the location for players that first join the server.")
            .comment("This only applies if first-spawn-override is set to true.")
            .defaultValue("")
            .name("first-spawn-location")
            .build());

    private final ConfigHeaderNode PORTAL_HEADER = node(ConfigHeaderNode.builder("portal")
            .comment("")
            .comment("")
            .build());

    public final ConfigNode<Boolean> USE_CUSTOM_PORTAL_SEARCH = node(ConfigNode.builder("portal.use-custom-portal-search", Boolean.class)
            .comment("This config option defines whether or not Multiverse should interfere with's Bukkit's default portal search radius.")
            .comment("Setting it to false would mean you want to simply let Bukkit decides the search radius itself.")
            .defaultValue(false)
            .name("use-custom-portal-search")
            .build());

    public final ConfigNode<Integer> CUSTOM_PORTAL_SEARCH_RADIUS = node(ConfigNode.builder("portal.custom-portal-search-radius", Integer.class)
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

    public final ConfigNode<Boolean> ENABLE_CHAT_PREFIX = node(ConfigNode.builder("messaging.enable-chat-prefix", Boolean.class)
            .comment("This config option defines whether or not Multiverse should prefix the chat with the world name.")
            .comment("This only applies if use-custom-portal-search is set to true.")
            .defaultValue(false)
            .name("enable-chat-prefix")
            .build());

    public final ConfigNode<String> CHAT_PREFIX_FORMAT = node(ConfigNode.builder("messaging.chat-prefix-format", String.class)
            .comment("")
            .comment("This config option defines the format Multiverse should use when prefixing the chat with the world name.")
            .comment("This only applies if enable-chat-prefix is set to true.")
            .defaultValue("[%world%]%chat%")
            .name("chat-prefix-format")
            .build());

    public final ConfigNode<Boolean> REGISTER_PAPI_HOOK = node(ConfigNode.builder("messaging.register-papi-hook", Boolean.class)
            .comment("")
            .comment("This config option defines whether or not Multiverse should register the PlaceholderAPI hook.")
            .comment("This only applies if PlaceholderAPI is installed.")
            .defaultValue(true)
            .name("register-papi-hook")
            .build());

    private final ConfigHeaderNode MISC_HEADER = node(ConfigHeaderNode.builder("misc")
            .comment("")
            .comment("")
            .build());

    public final ConfigNode<Integer> GLOBAL_DEBUG = node(ConfigNode.builder("misc.global-debug", Integer.class)
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
                    ? Try.failure(new MultiverseException("Debug level must be between 0 and 3.", null))
                    : Try.success(null))
            .onSetValue((oldValue, newValue) -> {
                int level = Logging.getDebugLevel();
                Logging.setDebugLevel(newValue);
                if (level != Logging.getDebugLevel()) {
                    pluginManager.callEvent(new MVDebugModeEvent(level));
                }
            })
            .build());

    public final ConfigNode<Boolean> SILENT_START = node(ConfigNode.builder("misc.silent-start", Boolean.class)
            .comment("")
            .comment("If true, the startup console messages will no longer show.")
            .defaultValue(false)
            .name("silent-start")
            .onSetValue((oldValue, newValue) -> Logging.setShowingConfig(!newValue))
            .build());

    public final ConfigNode<Boolean> SHOW_DONATION_MESSAGE = node(ConfigNode.builder("misc.show-donation-message", Boolean.class)
            .comment("")
            .comment("If you don't want to donate, you can set this to false and Multiverse will stop nagging you.")
            .defaultValue(true)
            .name("show-donation-message")
            .build());

    public final ConfigNode<Double> VERSION = node(ConfigNode.builder("version", Double.class)
            .comment("")
            .comment("")
            .comment("This just signifies the version number so we can see what version of config you have.")
            .comment("NEVER TOUCH THIS VALUE")
            .defaultValue(MVCoreConfig.CONFIG_VERSION)
            .name(null)
            .build());
}
