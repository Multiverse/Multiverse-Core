package com.mvplugin.core.api;

import com.dumptruckman.minecraft.pluginbase.messaging.Message;
import com.dumptruckman.minecraft.pluginbase.properties.ListProperty;
import com.dumptruckman.minecraft.pluginbase.properties.Properties;
import com.dumptruckman.minecraft.pluginbase.properties.PropertyFactory;
import com.dumptruckman.minecraft.pluginbase.properties.SimpleProperty;
import com.mvplugin.core.minecraft.WorldEnvironment;

/**
 * Houses all of the properties for a Multiverse world.
 */
public interface WorldProperties extends Properties {

    SimpleProperty<String> ALIAS = PropertyFactory.newProperty(String.class, "alias", "")
            .comment("World aliases allow you to name a world differently than what the folder name is.")
            .comment("This lets you choose fancy names for your worlds while keeping the folders nice and neat.")
            .description(Descriptions.ALIAS)
            .build();

    SimpleProperty<Boolean> HIDDEN = PropertyFactory.newProperty(Boolean.class, "hidden", false)
            .comment("The hidden property allows you to have a world that exists but does not show up in lists.")
            .description(Descriptions.HIDDEN)
            .build();

    SimpleProperty<Boolean> PREFIX_CHAT = PropertyFactory.newProperty(Boolean.class, "prefixChat", true)
            .comment("The prefixChat property adds the world's name (or alias) as a prefix to chat messages.")
            .comment("Please note, this property can be disabled globally in the configuration.")
            .description(Descriptions.PREFIX_CHAT)
            .build();

    SimpleProperty<Long> SEED = PropertyFactory.newProperty(Long.class, "seed", 0L)
            .comment("The seed property allows you to change the world's seed.")
            .description(Descriptions.SEED)
            .build();

    SimpleProperty<String> GENERATOR = PropertyFactory.newProperty(String.class, "generator", "")
            .comment("The generator property allows you to specify the generator used to generate this world.")
            .description(Descriptions.GENERATOR)
            .build();

    SimpleProperty<WorldEnvironment> ENVIRONMENT = PropertyFactory.newProperty(WorldEnvironment.class, "environment", WorldEnvironment.NORMAL)
            .comment("The environment property the Minecraft world environment such as NORMAL, NETHER, THE_END")
            .description(Descriptions.ENVIRONMENT)
            .build();

    SimpleProperty<Integer> PLAYER_LIMIT = PropertyFactory.newProperty(Integer.class, "playerLimit", -1)
            .comment("The player limit property limits the number of players in a world at a time.")
            .comment("A value of -1 or lower signifies no player limit.")
            .description(Descriptions.PLAYER_LIMIT)
            .build();

    SimpleProperty<Boolean> ADJUST_SPAWN = PropertyFactory.newProperty(Boolean.class, "adjustSpawn", true)
            .comment("The adjust spawn property determines whether or not Multiverse will make adjustments to the world's spawn location if it is unsafe.")
            .description(Descriptions.ADJUST_SPAWN)
            .build();


    SimpleProperty<Boolean> AUTO_LOAD = PropertyFactory.newProperty(Boolean.class, "autoLoad", true)
            .comment("This property dictates whether this world is loaded automatically on startup or not.")
            .description(Descriptions.AUTO_LOAD)
            .build();

    SimpleProperty<Boolean> BED_RESPAWN = PropertyFactory.newProperty(Boolean.class, "bedRespawn", true)
            .comment("This property specifies if a player dying in this world should respawn in their bed or not.")
            .description(Descriptions.BED_RESPAWN)
            .build();

    SimpleProperty<Boolean> HUNGER = PropertyFactory.newProperty(Boolean.class, "hunger", true)
            .comment("This property specifies if hunger is depleted in this world")
            .description(Descriptions.HUNGER)
            .build();

    ListProperty<String> BLACK_LIST = PropertyFactory.newListProperty(String.class, "worldBlacklist")
            .comment("This property allows you to specify worlds that people cannot go to from this specified world.")
            .description(Descriptions.BLACK_LIST)
            .build();

    /**
     * Houses localized (english) descriptions of the Multiverse world properties.
     */
    public static class Descriptions {

        public static final Message ALIAS = new Message("world_properties.descriptions.alias",
                "World aliases allow you to name a world differently than what the folder name is.",
                "This lets you choose fancy names for your worlds while keeping the folders nice and neat.");

        public static final Message HIDDEN = new Message("world_properties.descriptions.hidden",
                "The hidden property allows you to have a world that exists but does not show up in lists.");

        public static final Message PREFIX_CHAT = new Message("world_properties.descriptions.prefixChat",
                "The prefixChat property adds the world's name (or alias) as a prefix to chat messages.");

        public static final Message SEED = new Message("world_properties.descriptions.seed",
                "The seed property allows you to change the world's seed.");

        public static final Message GENERATOR = new Message("world_properties.descriptions.generator",
                "The generator property allows you to specify the generator used to generate this world.");

        public static final Message ENVIRONMENT = new Message("world_properties.descriptions.environment",
                "The environment property the Minecraft world environment such as NORMAL, NETHER, THE_END");

        public static final Message PLAYER_LIMIT = new Message("world_properties.descriptions.playerLimit",
                "The player limit property limits the number of players in a world at a time.",
                "A value of -1 or lower signifies no player limit.");

        public static final Message ADJUST_SPAWN = new Message("world_properties.descriptions.adjustSpawn",
                "The adjust spawn property determines whether or not Multiverse will make adjustments to the world's spawn location if it is unsafe.");

        public static final Message AUTO_LOAD = new Message("world_properties.descriptions.autoLoad",
                "This value dictates whether this world is loaded automatically on startup or not.");

        public static final Message BED_RESPAWN = new Message("world_properties.descriptions.bedRespawn",
                "This property specifies if a player dying in this world should respawn in their bed or not.");

        public static final Message HUNGER = new Message("world_properties.descriptions.hunger",
                "This property specifies if hunger is depleted in this world");

        public static final Message BLACK_LIST = new Message("world_properties.descriptions.worldBlacklist",
                 "This property allows you to specify worlds that people cannot go to from this specified world.");

    }
}
