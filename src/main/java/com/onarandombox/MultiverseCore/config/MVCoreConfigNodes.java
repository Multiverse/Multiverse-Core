package com.onarandombox.MultiverseCore.config;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.configuration.node.MVCommentedNode;
import com.onarandombox.MultiverseCore.configuration.node.MVValueNode;
import com.onarandombox.MultiverseCore.configuration.node.NodeGroup;
import io.github.townyadvanced.commentedconfiguration.setting.CommentedNode;

public class MVCoreConfigNodes {
    private static final NodeGroup nodes = new NodeGroup();

    public static NodeGroup getNodes() {
        return nodes;
    }

    private static <N extends CommentedNode> N node(N node) {
        nodes.add(node);
        return node;
    }

    private static final MVCommentedNode HEADER = node(MVCommentedNode.builder("world") // TODO hacky way to get the header to the top of the file
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

//    private static final MVCommentedNode WORLD_HEADER = node(MVCommentedNode.builder("world")
//            .comment("")
//            .comment("")
//            .build());

    public static final MVValueNode<Boolean> ENFORCE_ACCESS = node(MVValueNode.builder("world.enforce-access", Boolean.class)
            .comment("This setting will prevent players from entering worlds they don't have access to.")
            .comment("If this is set to false, players will be able to enter any world they want.")
            .comment("If this is set to true, players will only be able to enter worlds they have")
            .comment("the `mv.access.<worldname>` permission.")
            .defaultValue(false)
            .name("enforce-access")
            .build());

    public static final MVValueNode<Boolean> ENFORCE_GAMEMODE = node(MVValueNode.builder("world.enforce-gamemode", Boolean.class)
            .comment("")
            .comment("Sets whether Multiverse will should enforce gamemode on world change.")
            .comment("If enabled, players will be forced into the gamemode of the world they are entering, unless they have")
            .comment("the `mv.bypass.gamemode.<worldname>` permission.")
            .defaultValue(true)
            .name("enforce-gamemode")
            .build());

    public static final MVValueNode<Boolean> AUTO_PURGE_ENTITIES = node(MVValueNode.builder("world.auto-purge-entities", Boolean.class)
            .comment("")
            .comment("Sets whether Multiverse will purge mobs and entities with be automatically.")
            .defaultValue(false)
            .name("auto-purge-entities")
            .build());

    public static final MVValueNode<Boolean> TELEPORT_INTERCEPT = node(MVValueNode.builder("world.teleport-intercept", Boolean.class)
            .comment("")
            .comment("If this is set to true, Multiverse will enforce access permissions for all teleportation,")
            .comment("including teleportation from other plugins.")
            .defaultValue(true)
            .name("teleport-intercept")
            .build());

    private static final MVCommentedNode SPAWN_HEADER = node(MVCommentedNode.builder("spawn")
            .comment("")
            .comment("")
            .build());

    public static final MVValueNode<Boolean> FIRST_SPAWN_OVERRIDE = node(MVValueNode.builder("spawn.first-spawn-override", Boolean.class)
            .comment("Sets whether Multiverse will override the first spawn location of a world.")
            .comment("If enabled, Multiverse will set the first spawn location of a world to the spawn location of the world.")
            .comment("If disabled, it will default to server.properties settings.")
            .defaultValue(true)
            .name("first-spawn-override")
            .build());

    public static final MVValueNode<String> FIRST_SPAWN_LOCATION = node(MVValueNode.builder("spawn.first-spawn-location", String.class)
            .comment("")
            .comment("Sets the world that Multiverse will use as the location for players that first join the server.")
            .comment("This only applies if first-spawn-override is set to true.")
            .defaultValue("")
            .name("first-spawn-location")
            .build());

    private static final MVCommentedNode PORTAL_HEADER = node(MVCommentedNode.builder("portal")
            .comment("")
            .comment("")
            .build());

    public static final MVValueNode<Boolean> USE_CUSTOM_PORTAL_SEARCH = node(MVValueNode.builder("portal.use-custom-portal-search", Boolean.class)
            .comment("This config option defines whether or not Multiverse should interfere with's Bukkit's default portal search radius.")
            .comment("Setting it to false would mean you want to simply let Bukkit decides the search radius itself.")
            .defaultValue(false)
            .name("use-custom-portal-search")
            .build());

    public static final MVValueNode<Integer> CUSTOM_PORTAL_SEARCH_RADIUS = node(MVValueNode.builder("portal.custom-portal-search-radius", Integer.class)
            .comment("")
            .comment("This config option defines the search radius Multiverse should use when searching for a portal.")
            .comment("This only applies if use-custom-portal-search is set to true.")
            .defaultValue(128)
            .name("custom-portal-search-radius")
            .validator(value -> value >= 0)
            .build());

    private static final MVCommentedNode MESSAGING_HEADER = node(MVCommentedNode.builder("messaging")
            .comment("")
            .comment("")
            .build());

    public static final MVValueNode<Boolean> ENABLE_CHAT_PREFIX = node(MVValueNode.builder("messaging.enable-chat-prefix", Boolean.class)
            .comment("This config option defines whether or not Multiverse should prefix the chat with the world name.")
            .comment("This only applies if use-custom-portal-search is set to true.")
            .defaultValue(false)
            .name("enable-chat-prefix")
            .build());

    public static final MVValueNode<String> CHAT_PREFIX_FORMAT = node(MVValueNode.builder("messaging.chat-prefix-format", String.class)
            .comment("")
            .comment("This config option defines the format Multiverse should use when prefixing the chat with the world name.")
            .comment("This only applies if enable-chat-prefix is set to true.")
            .defaultValue("[%world%]%chat%")
            .name("chat-prefix-format")
            .build());

    public static final MVValueNode<Boolean> REGISTER_PAPI_HOOK = node(MVValueNode.builder("messaging.register-papi-hook", Boolean.class)
            .comment("")
            .comment("This config option defines whether or not Multiverse should register the PlaceholderAPI hook.")
            .comment("This only applies if PlaceholderAPI is installed.")
            .defaultValue(true)
            .name("register-papi-hook")
            .build());

    private static final MVCommentedNode MISC_HEADER = node(MVCommentedNode.builder("misc")
            .comment("")
            .comment("")
            .build());

    public static final MVValueNode<Integer> GLOBAL_DEBUG = node(MVValueNode.builder("misc.global-debug", Integer.class)
            .comment("This is our debug flag to help identify issues with Multiverse.")
            .comment("If you are having issues with Multiverse, please set this to 3 and then post your log to pastebin.com")
            .comment("Otherwise, there's no need to touch this. If not instructed by a wiki page or developer.")
            .comment("  0 = Off, No debug messages")
            .comment("  1 = fine")
            .comment("  2 = finer")
            .comment("  3 = finest")
            .defaultValue(0)
            .name("global-debug")
            .validator(value -> value >= 0 && value <= 3)
            .onSetValue((oldValue, newValue) -> Logging.setDebugLevel(newValue))
            .build());

    public static final MVValueNode<Boolean> SILENT_START = node(MVValueNode.builder("misc.silent-start", Boolean.class)
            .comment("")
            .comment("If true, the startup console messages will no longer show.")
            .defaultValue(false)
            .name("silent-start")
            .onSetValue((oldValue, newValue) -> Logging.setShowingConfig(!newValue))
            .build());

    public static final MVValueNode<Boolean> SHOW_DONATION_MESSAGE = node(MVValueNode.builder("misc.show-donation-message", Boolean.class)
            .comment("")
            .comment("If you don't want to donate, you can set this to false and Multiverse will stop nagging you.")
            .defaultValue(true)
            .name("show-donation-message")
            .build());

    public static final MVValueNode<Double> VERSION = node(MVValueNode.builder("version", Double.class)
            .comment("")
            .comment("")
            .comment("This just signifies the version number so we can see what version of config you have.")
            .comment("NEVER TOUCH THIS VALUE")
            .defaultValue(MVCoreConfig.CONFIG_VERSION)
            .name(null)
            .build());
}
