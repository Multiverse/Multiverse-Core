package com.mvplugin.core.api;

import com.dumptruckman.minecraft.pluginbase.messaging.Message;
import com.dumptruckman.minecraft.pluginbase.properties.ListProperty;
import com.dumptruckman.minecraft.pluginbase.properties.NestedProperties;
import com.dumptruckman.minecraft.pluginbase.properties.NestedProperty;
import com.dumptruckman.minecraft.pluginbase.properties.Properties;
import com.dumptruckman.minecraft.pluginbase.properties.PropertyFactory;
import com.dumptruckman.minecraft.pluginbase.properties.SimpleProperty;
import com.mvplugin.core.minecraft.Difficulty;
import com.mvplugin.core.minecraft.GameMode;
import com.mvplugin.core.minecraft.PlayerPosition;
import com.mvplugin.core.minecraft.PortalType;
import com.mvplugin.core.minecraft.WorldEnvironment;

/**
 * Houses all of the properties for a Multiverse world.
 */
public interface WorldProperties extends Properties {

    SimpleProperty<String> ALIAS = PropertyFactory.newProperty(String.class, "alias", "")
            .comment("World aliases allow you to name a world differently than what the folder name is.")
            .comment("This lets you choose fancy names for your worlds while keeping the folders nice and neat.")
            .comment("You may add minecraft color and formatting codes here prepended with a &")
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
            .comment("The autoLoad dictates whether this world is loaded automatically on startup or not.")
            .description(Descriptions.AUTO_LOAD)
            .build();

    SimpleProperty<Boolean> BED_RESPAWN = PropertyFactory.newProperty(Boolean.class, "bedRespawn", true)
            .comment("The bedRespawn property specifies if a player dying in this world should respawn in their bed or not.")
            .description(Descriptions.BED_RESPAWN)
            .build();

    SimpleProperty<Boolean> HUNGER = PropertyFactory.newProperty(Boolean.class, "hunger", true)
            .comment("The hunger property specifies if hunger is depleted in this world")
            .description(Descriptions.HUNGER)
            .build();

    ListProperty<String> BLACK_LIST = PropertyFactory.newListProperty(String.class, "worldBlacklist")
            .comment("The worldBlackList property allows you to specify worlds that people cannot go to from this specified world.")
            .description(Descriptions.BLACK_LIST)
            .build();

    SimpleProperty<Boolean> PVP = PropertyFactory.newProperty(Boolean.class, "pvp", true)
            .comment("The pvp property states whether or not players may harm each other in this world. If set to true, they may.")
            .comment("Bear in mind, many other plugins may have conflicts with this setting.")
            .description(Descriptions.PVP)
            .build();

    SimpleProperty<Double> SCALE = PropertyFactory.newProperty(Double.class, "scale", 1D)
            .comment("The scale property represents the scaling of worlds when using Multiverse-NetherPortals.")
            .comment("Setting this value will have no effect on anything but Multiverse-NetherPortals.")
            .description(Descriptions.SCALE)
            .build();

    SimpleProperty<String> RESPAWN_WORLD = PropertyFactory.newProperty(String.class, "respawnWorld", "")
            .comment("The respawnWorld property is the world you will respawn to if you die in this world.")
            .comment("This value can be the same as this world.")
            .description(Descriptions.RESPAWN_WORLD)
            .build();

    SimpleProperty<Boolean> ALLOW_WEATHER = PropertyFactory.newProperty(Boolean.class, "allowWeather", true)
            .comment("The allowWeather property specifies whether or not to allow weather events in this world.")
            .description(Descriptions.ALLOW_WEATHER)
            .build();

    SimpleProperty<Difficulty> DIFFICULTY = PropertyFactory.newProperty(Difficulty.class, "difficulty", Difficulty.EASY)
            .comment("The difficulty property allows you to set the difficulty for the world.")
            .comment("World difficulty affects spawn rates, hunger rates, and other things that make the game more or less difficult.")
            .description(Descriptions.DIFFICULTY)
            .build();

    SimpleProperty<Boolean> AUTO_HEAL = PropertyFactory.newProperty(Boolean.class, "autoHeal", true)
            .comment("The autoHeal property will specify whether ot not players will regain health in PEACEFUL difficulty only.")
            .comment("This setting has no effect on worlds with a difficulty greater than peaceful or 0.")
            .description(Descriptions.AUTO_HEAL)
            .build();

    SimpleProperty<PortalType> PORTAL_FORM = PropertyFactory.newProperty(PortalType.class, "portalForm", PortalType.ALL)
            .comment("The portalFrom property allows you to specify which type of portals are allowed to be created in this world.")
            .description(Descriptions.PORTAL_FORM)
            .build();

    SimpleProperty<GameMode> GAME_MODE = PropertyFactory.newProperty(GameMode.class, "gameMode", GameMode.SURVIVAL)
            .comment("The gameMode property allows you to specify the GameMode for this world.")
            .comment("Players entering this world will automatically be switched to this GameMode unless they are exempted.")
            .description(Descriptions.GAME_MODE)
            .build();

    SimpleProperty<Boolean> KEEP_SPAWN = PropertyFactory.newProperty(Boolean.class, "keepSpawnInMemory", true)
            .comment("The keepSpawnInMemory property specifies whether or not to keep the spawn chunks loaded in memory when players aren't in the spawn area.")
            .comment("Setting this to false will potentially save you some memory.")
            .description(Descriptions.KEEP_SPAWN)
            .build();

    SimpleProperty<PlayerPosition> SPAWN_LOCATION = PropertyFactory.newProperty(PlayerPosition.class, "spawnLocation", new NullLocation())
            .comment("The spawnLocation property specifies where in the world players will spawn.")
            .comment("The world specified here has no effect.")
            .description(Descriptions.SPAWN_LOCATION)
            .build();

    public static final class NullLocation extends PlayerPosition {
        public NullLocation() {
            super(null, 0, 0, 0, 0, 0);
        }
    }

    NestedProperty<EntryFee> ENTRY_FEE = PropertyFactory.newNestedProperty(EntryFee.class, "entryFee")
            .build();

    public static interface EntryFee extends NestedProperties {

        SimpleProperty<Double> AMOUNT = PropertyFactory.newProperty(Double.class, "amount", 0D)
                .comment("The amount property specifies how much a player has to pay to enter this world.")
                .comment("What the player has to pay is specified by the 'currency' property")
                .description(Descriptions.AMOUNT)
                .build();

        SimpleProperty<Integer> CURRENCY = PropertyFactory.newProperty(Integer.class, "currency", -1)
                .comment("The currency property specifies what type of currency the player must pay (if any) to enter this world.")
                .comment("Currency can be an economy money by specifying -1 or a block type by specifying the block id.")
                .description(Descriptions.CURRENCY)
                .build();
    }

    /**
     * Houses localized (english) descriptions of the Multiverse world properties.
     */
    public static class Descriptions {

        public static void init() { }

        public static final Message ALIAS = new Message("world_properties.descriptions.alias",
                "World aliases allow you to name a world differently than what the folder name is.",
                "This lets you choose fancy names for your worlds while keeping the folders nice and neat.",
                "You may add minecraft color and formatting codes here prepended with a &");

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
                "The autoLoad property dictates whether this world is loaded automatically on startup or not.");

        public static final Message BED_RESPAWN = new Message("world_properties.descriptions.bedRespawn",
                "The bedRespawn property specifies if a player dying in this world should respawn in their bed or not.");

        public static final Message HUNGER = new Message("world_properties.descriptions.hunger",
                "The hunger property specifies if hunger is depleted in this world");

        public static final Message BLACK_LIST = new Message("world_properties.descriptions.worldBlacklist",
                 "The worldBlackList property allows you to specify worlds that people cannot go to from this specified world.");

        public static final Message PVP = new Message("world_properties.descriptions.pvp",
                "The pvp property states whether or not players may harm each other in this world. If set to true, they may.");

        public static final Message SCALE = new Message("world_properties.descriptions.scale",
                "The scale property represents the scaling of worlds when using Multiverse-NetherPortals.",
                "Setting this value will have no effect on anything but Multiverse-NetherPortals.");

        public static final Message RESPAWN_WORLD = new Message("world_properties.descriptions.respawnWorld",
                "The respawnWorld property is the world you will respawn to if you die in this world.",
                "This value can be the same as this world.");

        public static final Message ALLOW_WEATHER = new Message("world_properties.descriptions.allowWeather",
                "The allowWeather property specifies whether or not to allow weather events in this world.");

        public static final Message DIFFICULTY = new Message("world_properties.descriptions.difficulty",
                "The difficulty property allows you to set the difficulty for the world.",
                "World difficulty affects spawn rates, hunger rates, and other things that make the game more or less difficult.");

        public static final Message AUTO_HEAL = new Message("world_properties.descriptions.autoHeal",
                "The autoHeal property will specify whether ot not players will regain health in PEACEFUL difficulty only.",
                "This setting has no effect on worlds with a difficulty greater than peaceful or 0.");

        public static final Message PORTAL_FORM = new Message("world_properties.descriptions.portalForm",
                "The portalFrom property allows you to specify which type of portals are allowed to be created in this world.");

        public static final Message GAME_MODE = new Message("world_properties.descriptions.gameMode",
                "The gameMode property allows you to specify the GameMode for this world.",
                "Players entering this world will automatically be switched to this GameMode unless they are exempted.");

        public static final Message KEEP_SPAWN = new Message("world_properties.descriptions.keepSpawnInMemory",
                "The keepSpawnInMemory property specifies whether or not to keep the spawn chunks loaded in memory when players aren't in the spawn area.",
                "Setting this to false will potentially save you some memory.");

        public static final Message SPAWN_LOCATION = new Message("world_properties.descriptions.spawnLocation",
                "The spawnLocation property specifies where in the world players will spawn.");

        public static final Message AMOUNT = new Message("world_properties.descriptions.amount",
                "The amount property specifies how much a player has to pay to enter this world.",
                "What the player has to pay is specified by the 'currency' property");

        public static final Message CURRENCY = new Message("world_properties.descriptions.currency",
                "The currency property specifies what type of currency the player must pay (if any) to enter this world.",
                "Currency can be an economy money by specifying -1 or a block type by specifying the block id.");
    }
}
