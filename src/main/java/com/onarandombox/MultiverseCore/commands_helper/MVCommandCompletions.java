package com.onarandombox.MultiverseCore.commands_helper;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.PaperCommandCompletions;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.enums.AddProperties;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MVCommandCompletions extends PaperCommandCompletions {

    private final MultiverseCore plugin;
    private final MVWorldManager worldManager;

    public MVCommandCompletions(MVCommandManager manager, MultiverseCore plugin) {
        super(manager);
        this.plugin = plugin;
        this.worldManager = plugin.getMVWorldManager();

        registerAsyncCompletion("MVWorlds", this::suggestMVWorlds);
        registerAsyncCompletion("unloadedWorlds", this::suggestUnloadedWorlds);
        registerAsyncCompletion("potentialWorlds", this::suggestPotentialWorlds);
        registerCompletion("location", this::suggestLocation);
        registerAsyncCompletion("MVConfigs", this::suggestMVConfig); //TODO: Change to static
        registerStaticCompletion("gameRules", suggestGameRules());
        registerStaticCompletion("environments", suggestEnvironments());
        registerStaticCompletion("setProperties", suggestSetProperties());
        registerStaticCompletion("addProperties", suggestAddProperties());
        registerStaticCompletion("livingEntities", suggestEntities());

        //TODO: Destinations
    }

    @NotNull
    private Collection<String> suggestMVWorlds(@NotNull BukkitCommandCompletionContext context) {
        return this.worldManager.getMVWorlds().stream()
                .map(MultiverseWorld::getName)
                .collect(Collectors.toList());
    }

    @NotNull
    private Collection<String> suggestUnloadedWorlds(@NotNull BukkitCommandCompletionContext context) {
        return this.worldManager.getUnloadedWorlds();
    }

    @NotNull
    private Collection<String> suggestPotentialWorlds(@NotNull BukkitCommandCompletionContext context) {
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
        if (MVCommandManager.BLACKLIST_WORLD_FOLDER.contains(worldFolder.getName())) {
            return false;
        }
        return folderHasDat(worldFolder);
    }

    private boolean folderHasDat(@NotNull File worldFolder) {
        File[] files = worldFolder.listFiles((file, name) -> name.equalsIgnoreCase(".dat"));
        return files != null && files.length > 0;
    }

    @NotNull
    private Collection<String> suggestLocation(@NotNull BukkitCommandCompletionContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return Collections.singletonList("0");
        }

        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(2);

        Location playerLocation = player.getLocation();
        double coordValue;
        switch (context.getConfig()) {
            case "x":
                coordValue = playerLocation.getX();
                break;
            case "y":
                coordValue = playerLocation.getY();
                break;
            case "z":
                coordValue = playerLocation.getZ();
                break;
            case "yaw":
                coordValue = playerLocation.getYaw();
                break;
            case "pitch":
                coordValue = playerLocation.getPitch();
                break;
            default:
                return Collections.emptyList();
        }

        return Arrays.asList("~", String.valueOf(coordValue));
    }

    @NotNull
    private Collection<String> suggestMVConfig(@NotNull BukkitCommandCompletionContext context) {
        return this.plugin.getMVConfig().serialize().keySet();
    }

    @NotNull
    private Collection<String> suggestGameRules() {
        return Arrays.stream(GameRule.values())
                .map(GameRule::getName)
                .collect(Collectors.toList());
    }

    @NotNull
    private Collection<String> suggestEnvironments() {
        return Arrays.stream(World.Environment.values())
                .map(e -> e.toString().toLowerCase())
                .collect(Collectors.toList());
    }

    private Collection<String> suggestSetProperties() {
        //TODO: Will need api change to getAllPropertyValues as a List<String>.
        return Collections.singletonList("null");
    }

    @NotNull
    private Collection<String> suggestAddProperties() {
        return Arrays.stream(AddProperties.values())
                .map(p -> p.toString().toLowerCase())
                .collect(Collectors.toList());
    }

    @NotNull
    private Collection<String> suggestEntities() {
        return Arrays.stream(EntityType.values())
                .filter(e -> e.isAlive() && e.isSpawnable())
                .map(e -> e.toString().toLowerCase())
                .collect(Collectors.toList());
    }
}
