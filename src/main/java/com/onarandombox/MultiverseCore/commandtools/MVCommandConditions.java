package com.onarandombox.MultiverseCore.commandtools;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.BukkitConditionContext;
import co.aikar.commands.CommandConditions;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.world.WorldNameChecker;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;

public class MVCommandConditions {
    static void load(MVCommandManager commandManager, MVWorldManager worldManager) {
        new MVCommandConditions(commandManager, worldManager);
    }

    private final MVWorldManager worldManager;
    private final MVCommandManager commandManager;

    private MVCommandConditions(@NotNull MVCommandManager commandManager, @NotNull MVWorldManager worldManager) {
        this.worldManager = worldManager;
        this.commandManager = commandManager;
    }

    @PostConstruct
    private void registerConditions() {
        CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> conditions
                = commandManager.getCommandConditions();

        conditions.addCondition(String.class, "validWorldName", this::checkValidWorldName);
    }

    private void checkValidWorldName(ConditionContext<BukkitCommandIssuer> context,
                                     BukkitCommandExecutionContext executionContext,
                                     String worldName
    ) {
        String scope = context.getConfigValue("scope", "loaded");

        switch (scope) {
            // Worlds that are loaded
            case "loaded":
                if (!this.worldManager.isMVWorld(worldName)) {
                    throw new ConditionFailedException("World with name '" + worldName + "' does not exist or is not loaded!");
                }
                break;
            // Worlds that are unloaded
            case "unloaded":
                if (!this.worldManager.hasUnloadedWorld(worldName, false)) {
                    if (this.worldManager.isMVWorld(worldName)) {
                        throw new ConditionFailedException("World with name '" + worldName + "' is loaded already!");
                    }
                    throw new ConditionFailedException("World with name '" + worldName + "' does not exist!");
                }
                break;
            // World that are loaded or unloaded
            case "both":
                if (!this.worldManager.hasUnloadedWorld(worldName, true)) {
                    throw new ConditionFailedException("World with name '" + worldName + "' does not exist!");
                }
                break;
            // World that are does not exist
            case "new":
                if (this.worldManager.hasUnloadedWorld(worldName, true)) {
                    throw new ConditionFailedException("World with name '" + worldName + "' already exists!");
                }
                switch (WorldNameChecker.checkName(worldName)) {
                    case INVALID_CHARS:
                        throw new ConditionFailedException("World name '" + worldName + "' contains invalid characters!");
                    case BLACKLISTED:
                        throw new ConditionFailedException("World name '" + worldName + "' is used for critical server operations and is blacklisted!");
                }
                break;
            // Probably a typo happened here
            default:
                throw new ConditionFailedException("Unknown scope '" + scope + "'!");
        }
    }
}
