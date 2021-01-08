package com.onarandombox.MultiverseCore.commandTools.flag;

import co.aikar.commands.InvalidCommandArgument;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commands.EnvironmentCommand;
import com.onarandombox.MultiverseCore.commands.GeneratorCommand;
import org.bukkit.ChatColor;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MVFlags {

    protected static final Map<String, Flag<?>> flagMap = new HashMap<>();

    public static final Flag<String> GENERATOR = new Flag<String>("-g", String.class) {
        @Override
        public @NotNull Collection<String> suggestValue(@NotNull MultiverseCore plugin) {
            return plugin.getMVWorldManager().getAvailableWorldGenerators();
        }

        @Override
        public String calculateValue(@NotNull String genString,
                                     @NotNull MultiverseCore plugin,
                                     @NotNull CommandSender sender) {

            String[] genArray = genString.split(":");
            String generator = genArray[0];
            String generatorId = (genArray.length > 1) ? genArray[1] : "";
            try {
                if (plugin.getMVWorldManager().getChunkGenerator(generator, generatorId, "test") == null) {
                    sender.sendMessage(String.format("%sInvalid generator string '%s'.", ChatColor.RED, genString));
                    GeneratorCommand.showAvailableGenerator(plugin, sender);
                    throw new InvalidCommandArgument();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Logging.severe("Error occurred when trying to create your world with generator '%s'! Reason: %s",
                        genString, e.getCause());
                throw new InvalidCommandArgument();
            }

            return genString;
        }

        @Override
        public String getDefault() {
            return null;
        }
    };

    public static final Flag<WorldType> WORLD_TYPE = new Flag<WorldType>("-t", WorldType.class) {

        private final Map<String, String> typeAlias = new HashMap<String, String>(4){{
            put("normal", "NORMAL");
            put("flat", "FLAT");
            put("largebiomes", "LARGE_BIOMES");
            put("amplified", "AMPLIFIED");
        }};

        @Override
        public @NotNull Collection<String> suggestValue(@NotNull MultiverseCore plugin) {
            return typeAlias.keySet();
        }

        @Override
        public WorldType calculateValue(@NotNull String type,
                                        @NotNull MultiverseCore plugin,
                                        @NotNull CommandSender sender) {

            String typeFromAlias = typeAlias.get(type);
            try {
                return WorldType.valueOf((typeFromAlias == null) ? type : typeFromAlias);
            }
            catch (IllegalArgumentException e) {
                sender.sendMessage(String.format("%s'%s' is not a valid World Type.", ChatColor.RED, type));
                EnvironmentCommand.showWorldTypes(sender);
                throw new InvalidCommandArgument();
            }
        }

        @Override
        public WorldType getDefault() {
            return WorldType.NORMAL;
        }
    };

    public static final Flag<String> SEED = new Flag<String>("-s", String.class) {
        @Override
        public @NotNull Collection<String> suggestValue(@NotNull MultiverseCore plugin) {
            return Arrays.asList("seed", "random", String.valueOf(new Random().nextLong()));
        }

        @Override
        public String calculateValue(@NotNull String seed,
                                     @NotNull MultiverseCore plugin,
                                     @NotNull CommandSender sender) {

            return seed;
        }

        @Override
        public String getDefault() {
            return null;
        }
    };

    public static final Flag<Boolean> GENERATE_STRUCTURES = new Flag<Boolean>("-a", Boolean.class) {
        @Override
        public @NotNull Collection<String> suggestValue(@NotNull MultiverseCore plugin) {
            return Arrays.asList("true", "false");
        }

        @Override
        public Boolean calculateValue(@NotNull String value,
                                      @NotNull MultiverseCore plugin,
                                      @NotNull CommandSender sender) {

            return value.equalsIgnoreCase("true");
        }

        @Override
        public Boolean getDefault() {
            return true;
        }
    };

    public static final Flag<Boolean> SPAWN_ADJUST = new NoValueFlag<Boolean>("-n", Boolean.class) {
        @Override
        public Boolean calculateValue(@Nullable String empty,
                                      @NotNull MultiverseCore plugin,
                                      @NotNull CommandSender sender) {

            return false;
        }

        @Override
        public Boolean getDefault() {
            return true;
        }
    };

    public static @Nullable Flag<?> getByKey(String key) {
        return flagMap.get(key);
    }

    public static @NotNull Set<String> getKeys() {
        return flagMap.keySet();
    }

    public static @NotNull Collection<Flag<?>> all() {
        return flagMap.values();
    }
}
