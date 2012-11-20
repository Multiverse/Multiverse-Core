package com.mvplugin.core.api;

import com.dumptruckman.minecraft.pluginbase.messaging.Message;
import com.dumptruckman.minecraft.pluginbase.properties.ListProperty;
import com.dumptruckman.minecraft.pluginbase.properties.NestedProperties;
import com.dumptruckman.minecraft.pluginbase.properties.NestedProperty;
import com.dumptruckman.minecraft.pluginbase.properties.Properties;
import com.dumptruckman.minecraft.pluginbase.properties.PropertyFactory;
import com.dumptruckman.minecraft.pluginbase.properties.PropertyValidator;
import com.dumptruckman.minecraft.pluginbase.properties.SimpleProperty;
import com.mvplugin.core.minecraft.Difficulty;
import com.mvplugin.core.minecraft.GameMode;
import com.mvplugin.core.minecraft.PlayerPosition;
import com.mvplugin.core.minecraft.PortalType;
import com.mvplugin.core.minecraft.WorldEnvironment;
import com.mvplugin.core.util.PropertyDescriptions;

/**
 * Houses all of the properties for a Multiverse world.
 */
public interface WorldProperties extends Properties {

    SimpleProperty<String> ALIAS = PropertyFactory.newProperty(String.class, "alias", "")
            .comment("World aliases allow you to name a world differently than what the folder name is.")
            .comment("This lets you choose fancy names for your worlds while keeping the folders nice and neat.")
            .comment("You may add minecraft color and formatting codes here prepended with a &")
            .description(PropertyDescriptions.ALIAS)
            .build();

    SimpleProperty<Boolean> HIDDEN = PropertyFactory.newProperty(Boolean.class, "hidden", false)
            .comment("The hidden property allows you to have a world that exists but does not show up in lists.")
            .description(PropertyDescriptions.HIDDEN)
            .build();

    SimpleProperty<Boolean> PREFIX_CHAT = PropertyFactory.newProperty(Boolean.class, "prefixChat", true)
            .comment("The prefixChat property adds the world's name (or alias) as a prefix to chat messages.")
            .comment("Please note, this property can be disabled globally in the configuration.")
            .description(PropertyDescriptions.PREFIX_CHAT)
            .build();

    SimpleProperty<Long> SEED = PropertyFactory.newProperty(Long.class, "seed", 0L)
            .comment("The seed property allows you to change the world's seed.")
            .description(PropertyDescriptions.SEED)
            .build();

    SimpleProperty<String> GENERATOR = PropertyFactory.newProperty(String.class, "generator", "")
            .comment("The generator property allows you to specify the generator used to generate this world.")
            .description(PropertyDescriptions.GENERATOR)
            .build();

    SimpleProperty<WorldEnvironment> ENVIRONMENT = PropertyFactory.newProperty(WorldEnvironment.class, "environment", WorldEnvironment.NORMAL)
            .comment("The environment property the Minecraft world environment such as NORMAL, NETHER, THE_END")
            .description(PropertyDescriptions.ENVIRONMENT)
            .build();

    SimpleProperty<Integer> PLAYER_LIMIT = PropertyFactory.newProperty(Integer.class, "playerLimit", -1)
            .comment("The player limit property limits the number of players in a world at a time.")
            .comment("A value of -1 or lower signifies no player limit.")
            .description(PropertyDescriptions.PLAYER_LIMIT)
            .build();

    SimpleProperty<Boolean> ADJUST_SPAWN = PropertyFactory.newProperty(Boolean.class, "adjustSpawn", true)
            .comment("The adjust spawn property determines whether or not Multiverse will make adjustments to the world's spawn location if it is unsafe.")
            .description(PropertyDescriptions.ADJUST_SPAWN)
            .build();


    SimpleProperty<Boolean> AUTO_LOAD = PropertyFactory.newProperty(Boolean.class, "autoLoad", true)
            .comment("The autoLoad dictates whether this world is loaded automatically on startup or not.")
            .description(PropertyDescriptions.AUTO_LOAD)
            .build();

    SimpleProperty<Boolean> BED_RESPAWN = PropertyFactory.newProperty(Boolean.class, "bedRespawn", true)
            .comment("The bedRespawn property specifies if a player dying in this world should respawn in their bed or not.")
            .description(PropertyDescriptions.BED_RESPAWN)
            .build();

    SimpleProperty<Boolean> HUNGER = PropertyFactory.newProperty(Boolean.class, "hunger", true)
            .comment("The hunger property specifies if hunger is depleted in this world")
            .description(PropertyDescriptions.HUNGER)
            .build();

    ListProperty<String> BLACK_LIST = PropertyFactory.newListProperty(String.class, "worldBlacklist")
            .comment("The worldBlackList property allows you to specify worlds that people cannot go to from this specified world.")
            .description(PropertyDescriptions.BLACK_LIST)
            .build();

    SimpleProperty<Boolean> PVP = PropertyFactory.newProperty(Boolean.class, "pvp", true)
            .comment("The pvp property states whether or not players may harm each other in this world. If set to true, they may.")
            .comment("Bear in mind, many other plugins may have conflicts with this setting.")
            .description(PropertyDescriptions.PVP)
            .build();

    SimpleProperty<Double> SCALE = PropertyFactory.newProperty(Double.class, "scale", 1D)
            .comment("The scale property represents the scaling of worlds when using Multiverse-NetherPortals.")
            .comment("Setting this value will have no effect on anything but Multiverse-NetherPortals.")
            .description(PropertyDescriptions.SCALE)
            .validator(new ScaleValidator())
            .build();

    class ScaleValidator implements PropertyValidator<Double> {
        @Override
        public boolean isValid(Double scale) {
            return scale > 0D;
        }

        @Override
        public Message getInvalidMessage() {
            return PropertyDescriptions.INVALID_SCALE;
        }
    }

    SimpleProperty<String> RESPAWN_WORLD = PropertyFactory.newProperty(String.class, "respawnWorld", "")
            .comment("The respawnWorld property is the world you will respawn to if you die in this world.")
            .comment("This value can be the same as this world.")
            .description(PropertyDescriptions.RESPAWN_WORLD)
            .build();

    SimpleProperty<Boolean> ALLOW_WEATHER = PropertyFactory.newProperty(Boolean.class, "allowWeather", true)
            .comment("The allowWeather property specifies whether or not to allow weather events in this world.")
            .description(PropertyDescriptions.ALLOW_WEATHER)
            .build();

    SimpleProperty<Difficulty> DIFFICULTY = PropertyFactory.newProperty(Difficulty.class, "difficulty", Difficulty.EASY)
            .comment("The difficulty property allows you to set the difficulty for the world.")
            .comment("World difficulty affects spawn rates, hunger rates, and other things that make the game more or less difficult.")
            .description(PropertyDescriptions.DIFFICULTY)
            .build();

    SimpleProperty<Boolean> AUTO_HEAL = PropertyFactory.newProperty(Boolean.class, "autoHeal", true)
            .comment("The autoHeal property will specify whether ot not players will regain health in PEACEFUL difficulty only.")
            .comment("This setting has no effect on worlds with a difficulty greater than peaceful or 0.")
            .description(PropertyDescriptions.AUTO_HEAL)
            .build();

    SimpleProperty<PortalType> PORTAL_FORM = PropertyFactory.newProperty(PortalType.class, "portalForm", PortalType.ALL)
            .comment("The portalFrom property allows you to specify which type of portals are allowed to be created in this world.")
            .description(PropertyDescriptions.PORTAL_FORM)
            .build();

    SimpleProperty<GameMode> GAME_MODE = PropertyFactory.newProperty(GameMode.class, "gameMode", GameMode.SURVIVAL)
            .comment("The gameMode property allows you to specify the GameMode for this world.")
            .comment("Players entering this world will automatically be switched to this GameMode unless they are exempted.")
            .description(PropertyDescriptions.GAME_MODE)
            .build();

    SimpleProperty<Boolean> KEEP_SPAWN = PropertyFactory.newProperty(Boolean.class, "keepSpawnInMemory", true)
            .comment("The keepSpawnInMemory property specifies whether or not to keep the spawn chunks loaded in memory when players aren't in the spawn area.")
            .comment("Setting this to false will potentially save you some memory.")
            .description(PropertyDescriptions.KEEP_SPAWN)
            .build();

    SimpleProperty<PlayerPosition> SPAWN_LOCATION = PropertyFactory.newProperty(PlayerPosition.class, "spawnLocation", PlayerPosition.NULL_LOCATION)
            .comment("The spawnLocation property specifies where in the world players will spawn.")
            .comment("The world specified here has no effect.")
            .description(PropertyDescriptions.SPAWN_LOCATION)
            .build();

    NestedProperty<EntryFee> ENTRY_FEE = PropertyFactory.newNestedProperty(EntryFee.class, "entryFee")
            .build();

    public static interface EntryFee extends NestedProperties {

        SimpleProperty<Double> AMOUNT = PropertyFactory.newProperty(Double.class, "amount", 0D)
                .comment("The amount property specifies how much a player has to pay to enter this world.")
                .comment("What the player has to pay is specified by the 'currency' property")
                .description(PropertyDescriptions.AMOUNT)
                .build();

        SimpleProperty<Integer> CURRENCY = PropertyFactory.newProperty(Integer.class, "currency", -1)
                .comment("The currency property specifies what type of currency the player must pay (if any) to enter this world.")
                .comment("Currency can be an economy money by specifying -1 or a block type by specifying the block id.")
                .description(PropertyDescriptions.CURRENCY)
                .build();
    }

    NestedProperty<Spawning> SPAWNING = PropertyFactory.newNestedProperty(Spawning.class, "spawning")
            .build();

    public static interface Spawning extends NestedProperties {

        NestedProperty<Animals> ANIMALS = PropertyFactory.newNestedProperty(Animals.class, "animals")
                .build();

        NestedProperty<Monsters> MONSTERS = PropertyFactory.newNestedProperty(Monsters.class, "monsters")
                .build();

        public static interface Animals extends NestedProperties {

            SimpleProperty<Boolean> SPAWN = PropertyFactory.newProperty(Boolean.class, "spawn", true)
                    .comment("The animals spawn property specifies whether or not to spawn animals in this world.")
                    .description(PropertyDescriptions.ANIMALS_SPAWN)
                    .build();

            SimpleProperty<Integer> SPAWN_RATE = PropertyFactory.newProperty(Integer.class, "spawnRate", -1)
                    .comment("The animals spawnRate property defines how many ticks in between attempting to spawn animals.")
                    .comment("A value of -1 indicates the default should be used and is recommended unless you know what you are doing.")
                    .description(PropertyDescriptions.ANIMALS_SPAWN_RATE)
                    .build();

            ListProperty<String> EXCEPTIONS = PropertyFactory.newListProperty(String.class, "exceptions")
                    .comment("The animals exceptions property defines what animals are exempt from the animals spawn property.")
                    .description(PropertyDescriptions.ANIMALS_SPAWN_EXCEPTIONS)
                    .build();
        }

        public static interface Monsters extends NestedProperties {

            SimpleProperty<Boolean> SPAWN = PropertyFactory.newProperty(Boolean.class, "spawn", true)
                    .comment("The monsters spawn property specifies whether or not to spawn monsters in this world.")
                    .description(PropertyDescriptions.MONSTERS_SPAWN)
                    .build();

            SimpleProperty<Integer> SPAWN_RATE = PropertyFactory.newProperty(Integer.class, "spawnRate", -1)
                    .comment("The monsters spawnRate property defines how many ticks in between attempting to spawn monsters.")
                    .comment("A value of -1 indicates the default should be used and is recommended unless you know what you are doing.")
                    .description(PropertyDescriptions.MONSTERS_SPAWN_RATE)
                    .build();

            ListProperty<String> EXCEPTIONS = PropertyFactory.newListProperty(String.class, "exceptions")
                    .comment("The monsters exceptions property defines what monsters are exempt from the monsters spawn property.")
                    .description(PropertyDescriptions.MONSTERS_SPAWN_EXCEPTIONS)
                    .build();
        }
    }
}
