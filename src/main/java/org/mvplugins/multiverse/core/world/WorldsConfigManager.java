package org.mvplugins.multiverse.core.world;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Manages the worlds.yml file.
 */
@Service
final class WorldsConfigManager {
    private static final String CONFIG_FILENAME = "worlds.yml";

    private final SortedMap<String, WorldConfig> worldConfigMap;
    private final File worldConfigFile;
    private YamlConfiguration worldsConfig;

    private final MultiverseCore multiverseCore;

    @Inject
    WorldsConfigManager(@NotNull MultiverseCore core, @NotNull MultiverseCore multiverseCore) {
        worldConfigMap = new TreeMap<>();
        worldConfigFile = core.getDataFolder().toPath().resolve(CONFIG_FILENAME).toFile();

        this.multiverseCore = multiverseCore;
    }

    /**
     * Loads the worlds.yml file and creates a WorldConfig for each world in the file if it doesn't already exist.
     *
     * @return A {@link NewAndRemovedWorlds} record.
     */
    public Try<NewAndRemovedWorlds> load() {
        return Try.of(() -> {
            loadWorldYmlFile();
            return parseNewAndRemovedWorlds();
        }).onFailure(e -> {
            Logging.severe("Failed to load worlds.yml file: %s", e.getMessage());
        });
    }

    /**
     * Loads the worlds.yml file.
     *
     * @throws IOException                      If an error occurs while loading the file.
     * @throws InvalidConfigurationException    If the file is not a valid YAML file.
     */
    private void loadWorldYmlFile() throws IOException, InvalidConfigurationException {
        boolean exists = worldConfigFile.exists();
        if (!exists && !worldConfigFile.createNewFile()) {
            throw new IllegalStateException("Could not create worlds.yml config file");
        }
        if (exists) {
            migrateRemoveOldConfigSerializable();
        }
        worldsConfig = new YamlConfiguration();
        worldsConfig.load(worldConfigFile);
    }

    private void migrateRemoveOldConfigSerializable() {
        Try.of(() -> Files.readString(worldConfigFile.toPath()))
                .mapTry(configData -> {
                    if (!configData.contains("==: MVWorld")) {
                        throw new ConfigMigratedException();
                    }

                    // Copy old config file to `worlds.yml.old`
                    Path oldWorldConfig = worldConfigFile.toPath().getParent()
                            .resolve(CONFIG_FILENAME + ".old." + System.currentTimeMillis());
                    Files.copy(worldConfigFile.toPath(), oldWorldConfig, COPY_ATTRIBUTES, REPLACE_EXISTING);

                    return configData.replace("==: MVWorld", "w@: world")
                            .replace("==: MVSpawnSettings", "")
                            .replace("==: MVSpawnSubSettings", "")
                            .replace("==: MVEntryFee", "");
                })
                .andThenTry(configData -> Files.writeString(worldConfigFile.toPath(), configData))
                .andThenTry(() -> {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(worldConfigFile);

                    ConfigurationSection worldsSection = config.getConfigurationSection("worlds");
                    if (worldsSection == null) {
                        worldsSection = config.createSection("worlds");
                    }

                    List<String> worldNames = getOldConfigWorldNames(worldsSection);

                    Map<String, ConfigurationSection> worldConfigMap = new HashMap<>();
                    for (String worldName : worldNames) {
                        ConfigurationSection worldSection = worldsSection.getConfigurationSection(worldName);
                        if (worldSection != null) {
                            worldConfigMap.put(worldName, worldSection);
                        }
                    }

                    config.set("worlds", null);

                    for (Map.Entry<String, ConfigurationSection> entry : worldConfigMap.entrySet()) {
                        config.set(encodeWorldName(entry.getKey()), entry.getValue());
                    }
                    config.save(worldConfigFile);
                })
                .onFailure(e -> {
                    if (e instanceof ConfigMigratedException) {
                        Logging.fine("Config already migrated");
                        return;
                    }
                    Logging.warning("Failed to migrate old worlds.yml file: %s", e.getMessage());
                    e.printStackTrace();
                });
    }

    private @NotNull List<String> getOldConfigWorldNames(ConfigurationSection worldsSection) {
        List<String> worldNames = new ArrayList<>();
        recursiveGetOldConfigWorldNames(worldsSection, worldNames);
        return worldNames;
    }

    private void recursiveGetOldConfigWorldNames(ConfigurationSection section, List<String> worldNames) {
        Set<String> keys = section.getKeys(false);
        if (keys.isEmpty()) {
            // No keys in this section, nothing to do
            return;
        }

        if (keys.contains("w@")) {
            // this is the world data section already, get path without the "worlds." prefix
            worldNames.add(section.getCurrentPath().substring(7));
            return;
        }

        for (String key : keys) {
            ConfigurationSection dataSection = section.getConfigurationSection(key);
            if (dataSection == null) {
                // Something is wrong with the config, skip this key
                continue;
            }
            recursiveGetOldConfigWorldNames(dataSection, worldNames);
        }
    }

    /**
     * Parses the worlds.yml file and creates a WorldConfig for each world in the file if it doesn't already exist.
     *
     * @return A tuple containing a list of the new WorldConfigs added and a list of the worlds removed from the config.
     */
    private NewAndRemovedWorlds parseNewAndRemovedWorlds() {
        List<String> allWorldsInConfig = worldsConfig.getKeys(false)
                .stream()
                .map(this::decodeWorldName)
                .toList();

        List<WorldConfig> newWorldsAdded = new ArrayList<>();

        for (String worldName : allWorldsInConfig) {
            getWorldConfig(worldName)
                    .peek(config -> config.load(getWorldConfigSection(worldName)))
                    .orElse(() -> {
                        WorldConfig newWorldConfig = new WorldConfig(
                                worldName,
                                getWorldConfigSection(worldName),
                                multiverseCore);
                        worldConfigMap.put(worldName, newWorldConfig);
                        newWorldsAdded.add(newWorldConfig);
                        return Option.of(newWorldConfig);
                    })
                    .peek(WorldConfig::save);
        }

        List<String> worldsRemoved = worldConfigMap.keySet().stream()
                .filter(worldName -> !allWorldsInConfig.contains(worldName))
                .toList();

        for (String s : worldsRemoved) {
            worldConfigMap.remove(s);
        }

        return new NewAndRemovedWorlds(newWorldsAdded, worldsRemoved);
    }

    /**
     * Whether the worlds.yml file has been loaded.
     *
     * @return Whether the worlds.yml file has been loaded.
     */
    public boolean isLoaded() {
        return worldsConfig != null;
    }

    /**
     * Saves the worlds.yml file.
     *
     * @return Whether the save was successful or the error that occurred.
     */
    public Try<Void> save() {
        return Try.run(() -> {
            if (!isLoaded()) {
                throw new IllegalStateException("WorldsConfigManager is not loaded!");
            }
            worldsConfig = new YamlConfiguration();
            worldConfigMap.forEach((worldName, worldConfig) -> {
                worldConfig.save().onFailure(e -> {
                    throw new RuntimeException("Failed to save world config: " + worldName, e);
                });
                worldsConfig.set(encodeWorldName(worldName), worldConfig.getConfigurationSection());
            });
            worldsConfig.save(worldConfigFile);
        }).onFailure(e -> {
            Logging.severe("Failed to save worlds.yml file: %s", e.getMessage());
        });
    }

    /**
     * Gets the {@link WorldConfig} instance of all worlds in the worlds.yml file.
     *
     * @param worldName The name of the world to check.
     * @return The {@link WorldConfig} instance of the world, or empty option if it doesn't exist.
     */
    public @NotNull Option<WorldConfig> getWorldConfig(@NotNull String worldName) {
        return Option.of(worldConfigMap.get(worldName));
    }

    /**
     * Add a new world to the worlds.yml file. If a world with the given name already exists, an exception is thrown.
     *
     * @param worldName The name of the world to add.
     * @return The newly created {@link WorldConfig} instance.
     */
    public @NotNull WorldConfig addWorldConfig(@NotNull String worldName) {
        if (worldConfigMap.containsKey(worldName)) {
            throw new IllegalArgumentException("WorldConfig for world " + worldName + " already exists.");
        }
        WorldConfig worldConfig = new WorldConfig(worldName, getWorldConfigSection(worldName), multiverseCore);
        worldConfigMap.put(worldName, worldConfig);
        return worldConfig;
    }

    /**
     * Deletes the world config for the given world.
     *
     * @param worldName The name of the world to delete.
     */
    public void deleteWorldConfig(@NotNull String worldName) {
        worldConfigMap.remove(worldName);
        worldsConfig.set(encodeWorldName(worldName), null);
    }

    /**
     * Gets the {@link ConfigurationSection} for the given world in the worlds.yml file. If the section doesn't exist,
     * it is created.
     *
     * @param worldName The name of the world.
     * @return The {@link ConfigurationSection} for the given world.
     */
    private ConfigurationSection getWorldConfigSection(String worldName) {
        worldName = encodeWorldName(worldName);
        return worldsConfig.isConfigurationSection(worldName)
                ? worldsConfig.getConfigurationSection(worldName)
                : worldsConfig.createSection(worldName);
    }

    /**
     * Dot is a special character in YAML that causes sub-path issues.
     */
    private String encodeWorldName(String worldName) {
        return worldName.replace(".", "[dot]");
    }

    private String decodeWorldName(String worldName) {
        return worldName.replace("[dot]", ".");
    }

    private static final class ConfigMigratedException extends RuntimeException {
        private ConfigMigratedException() {
            super("Config migrated");
        }
    }
}
