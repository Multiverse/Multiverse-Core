package com.onarandombox.MultiverseCore.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.onarandombox.MultiverseCore.utils.settings.node.MVCommentedNode;
import com.onarandombox.MultiverseCore.utils.settings.node.MVValueNode;
import com.onarandombox.MultiverseCore.utils.settings.node.NodeGroup;
import io.github.townyadvanced.commentedconfiguration.setting.CommentedNode;

public class MVConfigNodes {
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
            .build());

    public static final MVValueNode<Boolean> AUTO_PURGE_ENTITIES = node(MVValueNode.builder("world.auto-purge-entities", Boolean.class)
            .comment("")
            .comment("Sets whether Multiverse will purge mobs and entities with be automatically.")
            .defaultValue(false)
            .build());

    public static final MVValueNode<Boolean> TELEPORT_INTERCEPT = node(MVValueNode.builder("world.teleport-intercept", Boolean.class)
            .comment("")
            .comment("If this is set to true, Multiverse will enforce access permissions for all teleportation,")
            .comment("including teleportation from other plugins.")
            .defaultValue(true)
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
            .build());

    public static final MVValueNode<String> FIRST_SPAWN_LOCATION = node(MVValueNode.builder("spawn.first-spawn-location", String.class)
            .comment("")
            .comment("Sets the world that Multiverse will use as the location for players that first join the server.")
            .comment("This only applies if first-spawn-override is set to true.")
            .defaultValue("")
            .build());

    private static final MVCommentedNode PORTAL_HEADER = node(MVCommentedNode.builder("portal")
            .comment("")
            .comment("")
            .build());

    public static final MVValueNode<Boolean> USE_CUSTOM_PORTAL_SEARCH = node(MVValueNode.builder("portal.use-custom-portal-search", Boolean.class)
            .comment("This config option defines whether or not Multiverse should interfere with's Bukkit's default portal search radius.")
            .comment("Setting it to false would mean you want to simply let Bukkit decides the search radius itself.")
            .defaultValue(false)
            .build());

    public static final MVValueNode<Integer> CUSTOM_PORTAL_SEARCH_RADIUS = node(MVValueNode.builder("portal.custom-portal-search-radius", Integer.class)
            .comment("")
            .comment("This config option defines the search radius Multiverse should use when searching for a portal.")
            .comment("This only applies if use-custom-portal-search is set to true.")
            .defaultValue(128)
            .build());

    private static final MVCommentedNode MESSAGING_HEADER = node(MVCommentedNode.builder("messaging")
            .comment("")
            .comment("")
            .build());

    public static final MVValueNode<Boolean> ENABLE_CHAT_PREFIX = node(MVValueNode.builder("messaging.enable-chat-prefix", Boolean.class)
            .comment("This config option defines whether or not Multiverse should prefix the chat with the world name.")
            .comment("This only applies if use-custom-portal-search is set to true.")
            .defaultValue(false)
            .build());

    public static final MVValueNode<String> CHAT_PREFIX_FORMAT = node(MVValueNode.builder("messaging.chat-prefix-format", String.class)
            .comment("")
            .comment("This config option defines the format Multiverse should use when prefixing the chat with the world name.")
            .comment("This only applies if enable-chat-prefix is set to true.")
            .defaultValue("[%world%]%chat%")
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
            .build());

    public static final MVValueNode<Boolean> SILENT_START = node(MVValueNode.builder("misc.silent-start", Boolean.class)
            .comment("")
            .comment("If true, the startup console messages will no longer show.")
            .defaultValue(false)
            .build());

    public static final MVValueNode<Boolean> I_DONT_WANT_TO_DONATE = node(MVValueNode.builder("misc.i-dont-want-to-donate", Boolean.class)
            .comment("")
            .comment("If you don't want to donate, you can set this to true and Multiverse will stop nagging you.")
            .defaultValue(false)
            .build());

    public static final MVValueNode<Double> VERSION = node(MVValueNode.builder("version", Double.class)
            .comment("")
            .comment("")
            .comment("This just signifies the version number so we can see what version of config you have.")
            .comment("NEVER TOUCH THIS VALUE")
            .defaultValue(DefaultMVConfig.CONFIG_VERSION)
            .name(null)
            .build());
}
