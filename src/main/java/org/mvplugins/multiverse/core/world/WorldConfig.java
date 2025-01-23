package org.mvplugins.multiverse.core.world;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.configuration.handle.MemoryConfigurationHandle;
import org.mvplugins.multiverse.core.configuration.handle.StringPropertyHandle;
import org.mvplugins.multiverse.core.configuration.migration.BooleanMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.ConfigMigrator;
import org.mvplugins.multiverse.core.configuration.migration.DeleteMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.DoubleMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.IntegerMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.LongMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.MigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.MoveMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.NullStringMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.VersionMigrator;
import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.world.location.SpawnLocation;

/**
 * Represents a world configuration.
 */
final class WorldConfig {

    private final String worldName;
    private final WorldConfigNodes configNodes;
    private final MemoryConfigurationHandle configHandle;
    private final StringPropertyHandle stringPropertyHandle;

    WorldConfig(
            @NotNull String worldName,
            @NotNull ConfigurationSection configSection,
            @NotNull MultiverseCore multiverseCore) {
        this.worldName = worldName;
        this.configNodes = new WorldConfigNodes(multiverseCore);
        this.configHandle = MemoryConfigurationHandle.builder(configSection, configNodes.getNodes())
                .logger(Logging.getLogger())
                .migrator(ConfigMigrator.builder(configNodes.VERSION)
                        .addVersionMigrator(initialVersionMigrator())
                        .build())
                .build();
        this.stringPropertyHandle = new StringPropertyHandle(configHandle);
        load();
    }

    private VersionMigrator initialVersionMigrator() {
        return VersionMigrator.builder(1.0)
                .addAction(MoveMigratorAction.of("adjustSpawn", "adjust-spawn"))
                .addAction(BooleanMigratorAction.of("adjust-spawn"))
                .addAction(MoveMigratorAction.of("allowFlight", "allow-flight"))
                .addAction(BooleanMigratorAction.of("allow-flight"))
                .addAction(MoveMigratorAction.of("allowWeather", "allow-weather"))
                .addAction(BooleanMigratorAction.of("allow-weather"))
                .addAction(MoveMigratorAction.of("autoHeal", "auto-heal"))
                .addAction(BooleanMigratorAction.of("auto-heal"))
                .addAction(MoveMigratorAction.of("autoLoad", "auto-load"))
                .addAction(BooleanMigratorAction.of("auto-load"))
                .addAction(MoveMigratorAction.of("bedRespawn", "bed-respawn"))
                .addAction(BooleanMigratorAction.of("bed-respawn"))
                //.addAction(MoveMigratorAction.of("difficulty", "difficulty"))
                .addAction(MoveMigratorAction.of("entryfee.amount", "entry-fee.amount"))
                .addAction(DoubleMigratorAction.of("entry-fee.amount"))
                .addAction(MoveMigratorAction.of("entryfee.currency", "entry-fee.currency"))
                .addAction(DeleteMigratorAction.of("entryfee"))
                //.addAction(MoveMigratorAction.of("environment", "environment"))
                .addAction(MoveMigratorAction.of("gameMode", "gamemode"))
                //.addAction(MoveMigratorAction.of("generator", "generator"))
                .addAction(NullStringMigratorAction.of("generator"))
                //.addAction(MoveMigratorAction.of("hidden", "hidden"))
                .addAction(BooleanMigratorAction.of("hidden"))
                //.addAction(MoveMigratorAction.of("hunger", "hunger"))
                .addAction(BooleanMigratorAction.of("hunger"))
                .addAction(MoveMigratorAction.of("keepSpawnInMemory", "keep-spawn-in-memory"))
                .addAction(BooleanMigratorAction.of("keep-spawn-in-memory"))
                .addAction(MoveMigratorAction.of("playerLimit", "player-limit"))
                .addAction(IntegerMigratorAction.of("player-limit"))
                .addAction(MoveMigratorAction.of("portalForm", "portal-form"))
                //.addAction(MoveMigratorAction.of("pvp", "pvp"))
                .addAction(BooleanMigratorAction.of("pvp"))
                .addAction(MoveMigratorAction.of("respawnWorld", "respawn-world"))
                //.addAction(MoveMigratorAction.of("scale", "scale"))
                .addAction(DoubleMigratorAction.of("scale"))
                //.addAction(MoveMigratorAction.of("seed", "seed"))
                .addAction(LongMigratorAction.of("seed"))
                .addAction(MoveMigratorAction.of("spawnLocation", "spawn-location"))
                //.addAction(MoveMigratorAction.of("spawning.animals.spawn", "spawning.animals.spawn"))
                .addAction(BooleanMigratorAction.of("spawning.animals.spawn"))
                .addAction(MoveMigratorAction.of("spawning.animals.spawnrate", "spawning.animals.tick-rate"))
                .addAction(IntegerMigratorAction.of("spawning.animals.tick-rate"))
                //.addAction(MoveMigratorAction.of("spawning.animals.exceptions", "spawning.animals.exceptions"))
                //.addAction(MoveMigratorAction.of("spawning.monsters.spawn", "spawning.monsters.spawn"))
                .addAction(BooleanMigratorAction.of("spawning.monsters.spawn"))
                .addAction(MoveMigratorAction.of("spawning.monsters.spawnrate", "spawning.monsters.tick-rate"))
                .addAction(IntegerMigratorAction.of("spawning.monsters.tick-rate"))
                //.addAction(MoveMigratorAction.of("spawning.monsters.exceptions", "spawning.monsters.exceptions"))
                .addAction(MoveMigratorAction.of("worldBlacklist", "world-blacklist"))
                .addAction(new EntryFeeMigrator())
                .addAction(new LegacyAliasMigrator())
                .build();
    }

    Try<Void> load() {
        return configHandle.load();
    }

    Try<Void> load(ConfigurationSection section) {
        return configHandle.load(section);
    }

    StringPropertyHandle getStringPropertyHandle() {
        return stringPropertyHandle;
    }

    String getWorldName() {
        return worldName;
    }

    boolean getAdjustSpawn() {
        return configHandle.get(configNodes.ADJUST_SPAWN);
    }

    Try<Void> setAdjustSpawn(boolean adjustSpawn) {
        return configHandle.set(configNodes.ADJUST_SPAWN, adjustSpawn);
    }

    @Nullable String getAlias() {
        return configHandle.get(configNodes.ALIAS);
    }

    Try<Void> setAlias(String alias) {
        return configHandle.set(configNodes.ALIAS, alias);
    }

    boolean getAllowFlight() {
        return configHandle.get(configNodes.ALLOW_FLIGHT);
    }

    Try<Void> setAllowFlight(boolean allowFlight) {
        return configHandle.set(configNodes.ALLOW_FLIGHT, allowFlight);
    }

    boolean getAllowWeather() {
        return configHandle.get(configNodes.ALLOW_WEATHER);
    }

    Try<Void> setAllowWeather(boolean allowWeather) {
        return configHandle.set(configNodes.ALLOW_WEATHER, allowWeather);
    }
    boolean getAnchorRespawn() {
        return configHandle.get(configNodes.ANCHOR_RESPAWN);
    }

    Try<Void> setAnchorSpawn(boolean anchorSpawn) {
        return configHandle.set(configNodes.ANCHOR_RESPAWN, anchorSpawn);
    }

    boolean getAutoHeal() {
        return configHandle.get(configNodes.AUTO_HEAL);
    }

    Try<Void> setAutoHeal(boolean autoHeal) {
        return configHandle.set(configNodes.AUTO_HEAL, autoHeal);
    }

    boolean getAutoLoad() {
        return configHandle.get(configNodes.AUTO_LOAD);
    }

    Try<Void> setAutoLoad(boolean autoLoad) {
        return configHandle.set(configNodes.AUTO_LOAD, autoLoad);
    }

    Biome getBiome() {
        return configHandle.get(configNodes.BIOME);
    }

    Try<Void> setBiome(Biome biome) {
        return configHandle.set(configNodes.BIOME, biome);
    }

    boolean getBedRespawn() {
        return configHandle.get(configNodes.BED_RESPAWN);
    }

    Try<Void> setBedRespawn(boolean bedRespawn) {
        return configHandle.set(configNodes.BED_RESPAWN, bedRespawn);
    }

    Difficulty getDifficulty() {
        return configHandle.get(configNodes.DIFFICULTY);
    }

    Try<Void> setDifficulty(Difficulty difficulty) {
        return configHandle.set(configNodes.DIFFICULTY, difficulty);
    }

    boolean isEntryFeeEnabled() {
        return configHandle.get(configNodes.ENTRY_FEE_ENABLED);
    }

    Try<Void> setEntryFeeEnabled(boolean entryFeeEnabled) {
        return configHandle.set(configNodes.ENTRY_FEE_ENABLED, entryFeeEnabled);
    }

    double getEntryFeeAmount() {
        return configHandle.get(configNodes.ENTRY_FEE_AMOUNT);
    }

    Try<Void> setEntryFeeAmount(double entryFeeAmount) {
        return configHandle.set(configNodes.ENTRY_FEE_AMOUNT, entryFeeAmount);
    }

    Material getEntryFeeCurrency() {
        return configHandle.get(configNodes.ENTRY_FEE_CURRENCY);
    }

    Try<Void> setEntryFeeCurrency(Material entryFeeCurrency) {
        return configHandle.set(configNodes.ENTRY_FEE_CURRENCY, entryFeeCurrency);
    }

    World.Environment getEnvironment() {
        return configHandle.get(configNodes.ENVIRONMENT);
    }

    Try<Void> setEnvironment(World.Environment environment) {
        return configHandle.set(configNodes.ENVIRONMENT, environment);
    }

    GameMode getGameMode() {
        return configHandle.get(configNodes.GAMEMODE);
    }

    Try<Void> setGameMode(GameMode gamemode) {
        return configHandle.set(configNodes.GAMEMODE, gamemode);
    }

    @Nullable String getGenerator() {
        return configHandle.get(configNodes.GENERATOR);
    }

    Try<Void> setGenerator(String generator) {
        return configHandle.set(configNodes.GENERATOR, generator);
    }

    boolean isHidden() {
        return configHandle.get(configNodes.HIDDEN);
    }

    Try<Void> setHidden(boolean hidden) {
        return configHandle.set(configNodes.HIDDEN, hidden);
    }

    boolean getHunger() {
        return configHandle.get(configNodes.HUNGER);
    }

    Try<Void> setHunger(boolean hunger) {
        return configHandle.set(configNodes.HUNGER, hunger);
    }

    boolean getKeepSpawnInMemory() {
        return configHandle.get(configNodes.KEEP_SPAWN_IN_MEMORY);
    }

    Try<Void> setKeepSpawnInMemory(boolean keepSpawnInMemory) {
        return configHandle.set(configNodes.KEEP_SPAWN_IN_MEMORY, keepSpawnInMemory);
    }

    int getPlayerLimit() {
        return configHandle.get(configNodes.PLAYER_LIMIT);
    }

    Try<Void> setPlayerLimit(int playerLimit) {
        return configHandle.set(configNodes.PLAYER_LIMIT, playerLimit);
    }

    AllowedPortalType getPortalForm() {
        return configHandle.get(configNodes.PORTAL_FORM);
    }

    Try<Void> setPortalForm(AllowedPortalType portalForm) {
        return configHandle.set(configNodes.PORTAL_FORM, portalForm);
    }

    boolean getPvp() {
        return configHandle.get(configNodes.PVP);
    }

    Try<Void> setPvp(boolean pvp) {
        return configHandle.set(configNodes.PVP, pvp);
    }

    String getRespawnWorld() {
        return configHandle.get(configNodes.RESPAWN_WORLD);
    }

    Try<Void> setRespawnWorld(String respawnWorld) {
        return configHandle.set(configNodes.RESPAWN_WORLD, respawnWorld);
    }

    double getScale() {
        return configHandle.get(configNodes.SCALE);
    }

    Try<Void> setScale(double scale) {
        return configHandle.set(configNodes.SCALE, scale);
    }

    long getSeed() {
        return configHandle.get(configNodes.SEED);
    }

    Try<Void> setSeed(long seed) {
        return configHandle.set(configNodes.SEED, seed);
    }

    SpawnLocation getSpawnLocation() {
        return configHandle.get(configNodes.SPAWN_LOCATION);
    }

    Try<Void> setSpawnLocation(SpawnLocation spawnLocation) {
        return configHandle.set(configNodes.SPAWN_LOCATION, spawnLocation);
    }

    boolean getSpawningAnimals() {
        return configHandle.get(configNodes.SPAWNING_ANIMALS);
    }

    Try<Void> setSpawningAnimals(boolean spawningAnimals) {
        return configHandle.set(configNodes.SPAWNING_ANIMALS, spawningAnimals);
    }

    int getSpawningAnimalsTicks() {
        return configHandle.get(configNodes.SPAWNING_ANIMALS_TICKS);
    }

    Try<Void> setSpawningAnimalsTicks(int spawningAnimalsAmount) {
        return configHandle.set(configNodes.SPAWNING_ANIMALS_TICKS, spawningAnimalsAmount);
    }

    List<String> getSpawningAnimalsExceptions() {
        return configHandle.get(configNodes.SPAWNING_ANIMALS_EXCEPTIONS);
    }

    Try<Void> setSpawningAnimalsExceptions(List<String> spawningAnimalsExceptions) {
        return configHandle.set(configNodes.SPAWNING_ANIMALS_EXCEPTIONS, spawningAnimalsExceptions);
    }

    boolean getSpawningMonsters() {
        return configHandle.get(configNodes.SPAWNING_MONSTERS);
    }

    Try<Void> setSpawningMonsters(boolean spawningMonsters) {
        return configHandle.set(configNodes.SPAWNING_MONSTERS, spawningMonsters);
    }

    int getSpawningMonstersTicks() {
        return configHandle.get(configNodes.SPAWNING_MONSTERS_TICKS);
    }

    Try<Void> setSpawningMonstersTicks(int spawningMonstersAmount) {
        return configHandle.set(configNodes.SPAWNING_MONSTERS_TICKS, spawningMonstersAmount);
    }

    List<String> getSpawningMonstersExceptions() {
        return configHandle.get(configNodes.SPAWNING_MONSTERS_EXCEPTIONS);
    }

    Try<Void> setSpawningMonstersExceptions(List<String> spawningMonstersExceptions) {
        return configHandle.set(configNodes.SPAWNING_MONSTERS_EXCEPTIONS, spawningMonstersExceptions);
    }

    List<String> getWorldBlacklist() {
        return configHandle.get(configNodes.WORLD_BLACKLIST);
    }

    Try<Void> setWorldBlacklist(List<String> worldBlacklist) {
        return configHandle.set(configNodes.WORLD_BLACKLIST, worldBlacklist);
    }

    void setMVWorld(@NotNull MultiverseWorld world) {
        configNodes.setWorld(world);
    }

    boolean isLoadedWorld() {
        return configNodes.getWorld() instanceof LoadedMultiverseWorld;
    }

    void deferenceMVWorld() {
        configNodes.setWorld(null);
    }

    ConfigurationSection getConfigurationSection() {
        return configHandle.getConfig();
    }

    /**
     * Migrates the entry fee settings. Assumes entry fee is disabled if currency is not set.
     */
    static final class EntryFeeMigrator implements MigratorAction {
        @Override
        public void migrate(ConfigurationSection config) {
            String currency = config.getString("entry-fee.currency", "");
            Logging.info("Entry fee currency: %s", currency);
            if (currency.isEmpty()) {
                config.set("entry-fee.enabled", false);
                config.set("entry-fee.currency", MVEconomist.VAULT_ECONOMY_CODE);
            } else {
                config.set("entry-fee.enabled", true);
            }
        }
    }

    /**
     * Migrates the alias settings.
     */
    private static final class LegacyAliasMigrator implements MigratorAction {
        @Override
        public void migrate(ConfigurationSection config) {
            AtomicReference<String> alias = new AtomicReference<>(config.getString("alias", ""));
            String color = config.getString("color", "");
            String style = config.getString("style", "");
            config.set("color", null);
            config.set("style", null);

            if (alias.get().isEmpty()) return;

            Try.of(() -> Enum.valueOf(EnglishChatColor.class, color.toUpperCase()))
                    .map(c -> c.color)
                    .onSuccess(c -> {
                        if (c != ChatColor.WHITE) {
                            alias.set("&" + c.getChar() + alias.get());
                        }
                    });

            Try.of(() -> Enum.valueOf(EnglishChatStyle.class, style.toUpperCase()))
                    .map(c -> c.color)
                    .onSuccess(s -> {
                        if (s != null) {
                            alias.set("&" + s.getChar() + alias.get());
                        }
                    });

            config.set("alias", alias.get());
        }

        private enum EnglishChatColor {
            // BEGIN CHECKSTYLE-SUPPRESSION: JavadocVariable
            AQUA(ChatColor.AQUA),
            BLACK(ChatColor.BLACK),
            BLUE(ChatColor.BLUE),
            DARKAQUA(ChatColor.DARK_AQUA),
            DARKBLUE(ChatColor.DARK_BLUE),
            DARKGRAY(ChatColor.DARK_GRAY),
            DARKGREEN(ChatColor.DARK_GREEN),
            DARKPURPLE(ChatColor.DARK_PURPLE),
            DARKRED(ChatColor.DARK_RED),
            GOLD(ChatColor.GOLD),
            GRAY(ChatColor.GRAY),
            GREEN(ChatColor.GREEN),
            LIGHTPURPLE(ChatColor.LIGHT_PURPLE),
            RED(ChatColor.RED),
            YELLOW(ChatColor.YELLOW),
            WHITE(ChatColor.WHITE);
            // END CHECKSTYLE-SUPPRESSION: JavadocVariable

            private final ChatColor color;
            //private final String text;

            EnglishChatColor(ChatColor color) {
                this.color = color;
            }
        }

        private enum EnglishChatStyle {
            // BEGIN CHECKSTYLE-SUPPRESSION: JavadocVariable
            /**
             * No style.
             */
            NORMAL(null),
            MAGIC(ChatColor.MAGIC),
            BOLD(ChatColor.BOLD),
            STRIKETHROUGH(ChatColor.STRIKETHROUGH),
            UNDERLINE(ChatColor.UNDERLINE),
            ITALIC(ChatColor.ITALIC);
            // END CHECKSTYLE-SUPPRESSION: JavadocVariable

            private final ChatColor color;

            EnglishChatStyle(ChatColor color) {
                this.color = color;
            }

        }
    }
}
