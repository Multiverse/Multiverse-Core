/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.PaperCommandCompletions;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.enums.AddProperties;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteServiceType;
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
import java.util.Set;
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
        registerAsyncCompletion("location", this::suggestLocation);
        registerAsyncCompletion("destinations", this::suggestDestinations);
        registerAsyncCompletion("anchors", this::suggestAnchors);
        registerStaticCompletion("MVConfigs", this::suggestMVConfig);
        registerStaticCompletion("gameRules", this::suggestGameRules);
        registerStaticCompletion("environments", this::suggestEnvironments);
        registerStaticCompletion("setProperties", this::suggestSetProperties);
        registerStaticCompletion("addProperties", this::suggestAddProperties);
        registerStaticCompletion("livingEntities", this::suggestEntities);
        registerStaticCompletion("pasteTypes", this::suggestPasteTypes);
        registerStaticCompletion("toggles", this::suggestToggles);
    }

    @NotNull
    private Collection<String> suggestToggles() {
        return Arrays.asList("on", "off");
    }

    @NotNull
    private Collection<String> suggestMVWorlds(@NotNull BukkitCommandCompletionContext context) {
        return this.worldManager.getMVWorlds().parallelStream()
                .unordered()
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
        List<String> knownWorlds = this.worldManager.getMVWorlds().parallelStream()
                .unordered()
                .map(MultiverseWorld::getName)
                .collect(Collectors.toList());

        return Arrays.stream(this.plugin.getServer().getWorldContainer().listFiles()).parallel()
                .unordered()
                .filter(File::isDirectory)
                .filter(file -> !knownWorlds.contains(file.getName()))
                .map(File::getName)
                .filter(this.worldManager::isValidWorld)
                .collect(Collectors.toList());
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

    private Collection<String> suggestDestinations(@NotNull BukkitCommandCompletionContext context) {
        //TODO: There is one empty dest need to remove.
        return this.plugin.getDestFactory().getIdentifiers().parallelStream()
                .unordered()
                .filter(id -> !id.isEmpty())
                .map(id -> id + ":")
                .collect(Collectors.toList());
    }

    private Collection<String> suggestAnchors(@NotNull BukkitCommandCompletionContext context) {
        return this.plugin.getAnchorManager().getAnchors(context.getPlayer());
    }

    @NotNull
    private Collection<String> suggestMVConfig() {
        final Set<String> configOptions = this.plugin.getMVConfig().serialize().keySet();
        configOptions.remove("version");
        return configOptions;
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

    @NotNull
    private Collection<String> suggestPasteTypes() {
        return Arrays.stream(PasteServiceType.values())
                .filter(pt -> pt != PasteServiceType.GITHUB && pt != PasteServiceType.NONE)
                .map(p -> p.toString().toLowerCase())
                .collect(Collectors.toList());
    }
}
