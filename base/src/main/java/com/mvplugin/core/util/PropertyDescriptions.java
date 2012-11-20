package com.mvplugin.core.util;

import com.dumptruckman.minecraft.pluginbase.messaging.Message;

/**
 * Houses localized (english) descriptions of the Multiverse world properties.
 */
public class PropertyDescriptions {

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

    public static final Message ANIMALS_SPAWN = new Message("world_properties.descriptions.animals.spawn",
            "The animals spawn property specifies whether or not to spawn animals in this world.");

    public static final Message ANIMALS_SPAWN_RATE = new Message("world_properties.descriptions.animals.spawnRate",
            "The animals spawnRate property defines how many ticks in between attempting to spawn animals.",
            "A value of -1 indicates the default should be used and is recommended unless you know what you are doing.");

    public static final Message ANIMALS_SPAWN_EXCEPTIONS = new Message("world_properties.descriptions.animals.exceptions",
            "The animals exceptions property defines what animals are exempt from the animals spawn property.");

    public static final Message MONSTERS_SPAWN = new Message("world_properties.descriptions.monsters.spawn",
            "The monsters spawn property specifies whether or not to spawn monsters in this world.");

    public static final Message MONSTERS_SPAWN_RATE = new Message("world_properties.descriptions.monsters.spawnRate",
            "The monsters spawnRate property defines how many ticks in between attempting to spawn monsters.",
            "A value of -1 indicates the default should be used and is recommended unless you know what you are doing.");

    public static final Message MONSTERS_SPAWN_EXCEPTIONS = new Message("world_properties.descriptions.monsters.exceptions",
            "The monsters exceptions property defines what monsters are exempt from the monsters spawn property.");


    public static final Message INVALID_SCALE = new Message("world_properties.validation.scale",
            "Scale must be a number higher than 0!");

    public static final Message INVALID_RESPAWN_WORLD = new Message("world_properties.validation.respawnWorld",
            "Respawn world must be a world known to Multiverse!");
}
