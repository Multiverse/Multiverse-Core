package com.onarandombox.MultiverseCore.commandtools.flag;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteServiceType;
import org.bukkit.Bukkit;
import org.bukkit.WorldType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class CoreFlags {

    private static MultiverseCore multiverse;

    public static void setCoreInstance(MultiverseCore plugin) {
        multiverse = plugin;
    }

    /**
     * Flag for custom seed.
     */
    public static final CommandFlag<String> SEED = new RequiredCommandFlag<String>
            ("Seed", "--seed", String.class) {
        @Override
        public Collection<String> suggestValue() {
            return Collections.singleton(String.valueOf(new Random().nextLong()));
        }

        @Override
        public String getValue(@NotNull String input) throws FlagParseFailedException {
            return input;
        }
    }.addAliases("-s");

    /**
     * Flag for custom seed. No value means random seed.
     */
    public static final CommandFlag<String> RANDOM_SEED = new OptionalCommandFlag<String>
            ("Seed", "--seed", String.class) {
        @Override
        public Collection<String> suggestValue() {
            return Collections.singletonList(String.valueOf(new Random().nextLong()));
        }

        @Override
        public String getValue(@NotNull String input) throws FlagParseFailedException {
            return input;
        }
    }.addAliases("-s");

    /**
     * Flag for world type used.
     */
    public static final CommandFlag<WorldType> WORLD_TYPE = new RequiredCommandFlag<WorldType>
            ("WorldType", "--type", WorldType.class) {

        private final Map<String, String> typeAlias = new HashMap<String, String>(4){{
            put("normal", "NORMAL");
            put("flat", "FLAT");
            put("largebiomes", "LARGE_BIOMES");
            put("amplified", "AMPLIFIED");
        }};

        @Override
        public Collection<String> suggestValue() {
            return typeAlias.keySet();
        }

        @Override
        public WorldType getValue(@NotNull String input) throws FlagParseFailedException {
            String typeName = typeAlias.getOrDefault(input, input);
            try {
                return WorldType.valueOf(input.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                throw new FlagParseFailedException("'%s' is not a valid World Type. See /mv env for available World Types.", input);
            }
        }

        @Override
        public WorldType getDefaultValue() {
            return WorldType.NORMAL;
        }
    }.addAliases("-t");

    /**
     * Flag for world generator.
     */
    public static final CommandFlag<String> GENERATOR = new RequiredCommandFlag<String>
            ("Generator", "--gen", String.class) {
        @Override
        public Collection<String> suggestValue() {
            return Arrays.stream(Bukkit.getServer().getPluginManager().getPlugins())
                    .filter(Plugin::isEnabled)
                    .filter(plugin -> multiverse.getUnsafeCallWrapper().wrap(
                            () -> plugin.getDefaultWorldGenerator("world", ""),
                            plugin.getName(),
                            "Get generator"
                    ) != null)
                    .map(plugin -> plugin.getDescription().getName())
                    .collect(Collectors.toList());
        }

        @Override
        public String getValue(@NotNull String input) throws FlagParseFailedException {
            String[] genArray = input.split(":");
            String generator = genArray[0];
            String generatorId = (genArray.length > 1) ? genArray[1] : "";
            try {
                if (multiverse.getMVWorldManager().getChunkGenerator(generator, generatorId, "test") == null) {
                    throw new Exception();
                }
            } catch (Exception e) {
                throw new FlagParseFailedException("Invalid generator string '%s'. See /mv gens for available generators.", input);
            }
            return input;
        }
    }.addAliases("-g");

    /**
     * Flag to toggle if world should generate structures.
     */
    public static final CommandFlag<Boolean> GENERATE_STRUCTURES = new RequiredCommandFlag<Boolean>
            ("GenerateStructures", "--structures", Boolean.class) {
        @Override
        public Collection<String> suggestValue() {
            return Arrays.asList("true", "false");
        }

        @Override
        public Boolean getValue(@NotNull String input) throws FlagParseFailedException {
            return input.equalsIgnoreCase("true");
        }

        @Override
        public Boolean getDefaultValue() {
            return true;
        }
    }.addAliases("--structure", "-a");

    /**
     * Flag to toggle if world spawn should be adjusted.
     */
    public static final CommandFlag<Boolean> SPAWN_ADJUST = new NoValueCommandFlag<Boolean>
            ("AdjustSpawn", "--dont-adjust-spawn", Boolean.class) {
        @Override
        public Boolean getValue() throws FlagParseFailedException {
            return false;
        }

        @Override
        public Boolean getDefaultValue() {
            return true;
        }
    }.addAliases("-n");

    /**
     * Flag to specify a paste service.
     */
    public static final CommandFlag<PasteServiceType> PASTE_SERVICE_TYPE = new OptionalCommandFlag<PasteServiceType>
            ("PasteServiceType", "--paste", PasteServiceType.class) {

        private final List<String> pasteTypes = Arrays.stream(PasteServiceType.values())
                .filter(pt -> pt != PasteServiceType.NONE)
                .map(p -> p.toString().toLowerCase())
                .collect(Collectors.toList());

        @Override
        public Collection<String> suggestValue() {
            return pasteTypes;
        }

        @Override
        public PasteServiceType getValue(@NotNull String input) throws FlagParseFailedException {
            try {
                return PasteServiceType.valueOf(input.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                throw new FlagParseFailedException(String.format("Invalid paste service type '%s'.", input));
            }
        }

        @Override
        public PasteServiceType getValue() throws FlagParseFailedException {
            return PasteServiceType.PASTEGG;
        }

        @Override
        public PasteServiceType getDefaultValue() {
            return PasteServiceType.NONE;
        }
    }.addAliases("-p");

    /**
     * Flag to toggle if plugin list should be included.
     */
    public static final CommandFlag<Boolean> INCLUDE_PLUGIN_LIST = new NoValueCommandFlag<Boolean>
            ("IncludePlugins", "--include-plugin-list", Boolean.class) {
        @Override
        public Boolean getValue() throws FlagParseFailedException {
            return true;
        }

        @Override
        public Boolean getDefaultValue() {
            return true;
        }
    }.addAliases("-pl");
}
