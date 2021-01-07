package com.onarandombox.MultiverseCore.commandTools.contexts;

import co.aikar.commands.InvalidCommandArgument;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commands.EnvironmentCommand;
import com.onarandombox.MultiverseCore.commands.GeneratorCommand;
import com.onarandombox.MultiverseCore.enums.FlagValue;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Flag<T> {

    private static final Map<String, Flag<?>> flagMap = new HashMap<>();

    public static final Flag<String> GENERATOR = new Flag<String>("-g", String.class) {
        @Override
        public @NotNull Collection<String> suggestValue() {
            //TODO ACF: Put in WorldManager.
            return Arrays.stream(Bukkit.getServer().getPluginManager().getPlugins())
                    .filter(Plugin::isEnabled)
                    .filter(plugin -> plugin.getDefaultWorldGenerator("world", "") != null)
                    .map(plugin -> plugin.getDescription().getName())
                    .collect(Collectors.toList());
        }

        @Override
        public String parseValue(@Nullable String genString,
                                 @NotNull MultiverseCore plugin,
                                 @NotNull CommandSender sender) {

            if (genString == null) {
                return null;
            }

            String[] genArray = genString.split(":");
            if (genArray.length == 0) {
                return null;
            }

            String generator = genArray[0];
            String generatorId = (genArray.length > 1) ? genArray[1] : "";
            try {
                if (plugin.getMVWorldManager().getChunkGenerator(generator, generatorId, "test") == null) {
                    sender.sendMessage(String.format("%sInvalid generator string '%s'.", ChatColor.RED, genString));
                    GeneratorCommand.showAvailableGenerator(sender);
                    throw new InvalidCommandArgument(false);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Logging.severe("Error occurred when trying to create your world with generator '%s'! Reason: %s",
                        genString, e.getCause());
                throw new InvalidCommandArgument(false);
            }

            return genString;
        }

        @Override
        public String getDefault() {
            return null;
        }
    };

    public static final Flag<WorldType> WORLD_TYPE = new Flag<WorldType>("-t", WorldType.class) {

        private final Map<String, String> typeAlias = new HashMap<String, String>(){{
            put("normal", "NORMAL");
            put("flat", "FLAT");
            put("largebiomes", "LARGE_BIOMES");
            put("amplified", "AMPLIFIED");
        }};

        @Override
        public @NotNull Collection<String> suggestValue() {
            return typeAlias.keySet();
        }

        @Override
        public WorldType parseValue(@Nullable String type,
                                    @NotNull MultiverseCore plugin,
                                    @NotNull CommandSender sender) {

            String typeFromAlias = typeAlias.get(type);
            try {
                return WorldType.valueOf((typeFromAlias == null) ? type : typeFromAlias);
            }
            catch (IllegalArgumentException e) {
                sender.sendMessage(String.format("%s'%s' is not a valid World Type.", ChatColor.RED, type));
                EnvironmentCommand.showWorldTypes(sender);
                throw new InvalidCommandArgument(false);
            }
        }

        @Override
        public WorldType getDefault() {
            return WorldType.NORMAL;
        }
    };

    public static final Flag<String> SEED = new Flag<String>("-s", String.class, FlagValue.OPTIONAL) {
        @Override
        public @NotNull Collection<String> suggestValue() {
            return Collections.emptyList();
        }

        @Override
        public String parseValue(@Nullable String seed,
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
        public @NotNull Collection<String> suggestValue() {
            return Arrays.asList("true", "false");
        }

        @Override
        public Boolean parseValue(@Nullable String value,
                                  @NotNull MultiverseCore plugin,
                                  @NotNull CommandSender sender) {

            return value == null || value.equalsIgnoreCase("true");
        }

        @Override
        public Boolean getDefault() {
            return true;
        }
    };

    public static final Flag<Boolean> SPAWN_ADJUST = new Flag<Boolean>("-n", Boolean.class, FlagValue.NONE) {
        @Override
        public @NotNull Collection<String> suggestValue() {
            return Collections.emptyList();
        }

        @Override
        public Boolean parseValue(@Nullable String value,
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

    private final String key;
    private final FlagValue valueRequirement;
    private final Class<T> type;

    public Flag(@NotNull String key,
                @NotNull Class<T> type) {

        this(key, type, FlagValue.REQUIRED);
    }

    protected Flag(@NotNull String key,
                   @NotNull Class<T> type,
                   @NotNull FlagValue requiresValue) {

        this.key = key;
        this.valueRequirement = requiresValue;
        this.type = type;
        flagMap.put(this.key, this);
    }

    public abstract @NotNull Collection<String> suggestValue();

    public abstract T parseValue(@Nullable String value,
                                 @NotNull MultiverseCore plugin,
                                 @NotNull CommandSender sender);

    public abstract T getDefault();

    public @NotNull String getKey() {
        return key;
    }

    public FlagValue getValueRequirement() {
        return valueRequirement;
    }

    public @NotNull Class<T> getType() {
        return type;
    }
}
