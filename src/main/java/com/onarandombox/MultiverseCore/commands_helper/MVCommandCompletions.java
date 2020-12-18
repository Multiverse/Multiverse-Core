package com.onarandombox.MultiverseCore.commands_helper;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.PaperCommandCompletions;
import co.aikar.commands.PaperCommandManager;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.enums.AddProperties;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MVCommandCompletions extends PaperCommandCompletions {

    private final MultiverseCore plugin;
    private final MVWorldManager worldManager;

    private static final Set<String> BLACKLIST_WORLD_FOLDER = Stream.of("plugins", "cache", "logs", "crash-reports").collect(Collectors.toCollection(HashSet::new));

    public MVCommandCompletions(MVCommandManager manager, MultiverseCore plugin) {
        super(manager);
        this.plugin = plugin;
        this.worldManager = plugin.getMVWorldManager();

        registerAsyncCompletion("MVWorlds", this::suggestMVWorlds);
        registerAsyncCompletion("unloadedWorlds", this::suggestUnloadedWorlds);
        registerAsyncCompletion("potentialWorlds", this::suggestPotentialWorlds);
        registerAsyncCompletion("MVConfigs", this::suggestMVConfig); //TODO: Change to static
        registerStaticCompletion("gameRules", suggestGameRules());
        registerStaticCompletion("environments", suggestEnvironments());
        registerStaticCompletion("addProperties", suggestAddProperties());
    }

    @NotNull
    private List<String> suggestMVWorlds(@NotNull BukkitCommandCompletionContext context) {
        return worldManager.getMVWorlds().stream()
                .map(MultiverseWorld::getName)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<String> suggestUnloadedWorlds(@NotNull BukkitCommandCompletionContext context) {
        return this.worldManager.getUnloadedWorlds();
    }

    @NotNull
    private List<String> suggestPotentialWorlds(@NotNull BukkitCommandCompletionContext context) {
        //TODO: Should be more efficient
        //TODO: this should be in WorldManager API
        List<String> knownWorlds = this.worldManager.getMVWorlds().stream()
                .map(MultiverseWorld::getName)
                .collect(Collectors.toList());

        return Arrays.stream(this.plugin.getServer().getWorldContainer().listFiles())
                .filter(File::isDirectory)
                .filter(file -> !knownWorlds.contains(file.getName()))
                .filter(this::validateWorldFolder)
                .map(File::getName)
                .collect(Collectors.toList());
    }

    private boolean validateWorldFolder(@NotNull File worldFolder) {
        if (!worldFolder.isDirectory()) {
            return false;
        }
        if (BLACKLIST_WORLD_FOLDER.contains(worldFolder.getName())) {
            return false;
        }
        return folderHasDat(worldFolder);
    }

    private boolean folderHasDat(@NotNull File worldFolder) {
        File[] files = worldFolder.listFiles((file, name) -> name.equalsIgnoreCase("level.dat"));
        return files != null && files.length > 0;
    }

    @NotNull
    private Set<String> suggestMVConfig(@NotNull BukkitCommandCompletionContext context) {
        return this.plugin.getMVConfig().serialize().keySet();
    }

    @NotNull
    private List<String> suggestGameRules() {
        return Arrays.stream(GameRule.values())
                .map(GameRule::getName)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<String> suggestEnvironments() {
        return Arrays.stream(World.Environment.values())
                .map(e -> e.toString().toLowerCase())
                .collect(Collectors.toList());
    }

    @NotNull
    private List<String> suggestAddProperties() {
        return Arrays.stream(AddProperties.values())
                .map(p -> p.toString().toLowerCase())
                .collect(Collectors.toList());
    }
}
