package org.mvplugins.multiverse.core.commandtools;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.BukkitConditionContext;
import co.aikar.commands.CommandConditions;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.world.WorldNameChecker;
import org.mvplugins.multiverse.core.worldnew.WorldManager;

public class MVCommandConditions {
    static void load(MVCommandManager commandManager, WorldManager worldManager) {
        new MVCommandConditions(commandManager, worldManager);
    }

    private final WorldManager worldManager;
    private final MVCommandManager commandManager;

    private MVCommandConditions(@NotNull MVCommandManager commandManager, @NotNull WorldManager worldManager) {
        this.worldManager = worldManager;
        this.commandManager = commandManager;
        registerConditions();
    }

    private void registerConditions() {
        CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> conditions
                = commandManager.getCommandConditions();

        conditions.addCondition(String.class, "worldname", this::checkWorldname);
    }

    private void checkWorldname(ConditionContext<BukkitCommandIssuer> context,
                                BukkitCommandExecutionContext executionContext,
                                String worldName
    ) {
        String scope = context.getConfigValue("scope", "loaded");

        switch (scope) {
            // Worlds that are loaded
            case "loaded":
                if (!this.worldManager.isLoadedWorld(worldName)) {
                    throw new ConditionFailedException("World with name '" + worldName + "' does not exist or is not loaded!");
                }
                break;
            // Worlds that are unloaded
            case "unloaded":
                if (!this.worldManager.isUnloadedWorld(worldName)) {
                    if (this.worldManager.isLoadedWorld(worldName)) {
                        throw new ConditionFailedException("World with name '" + worldName + "' is loaded already!");
                    }
                    throw new ConditionFailedException("World with name '" + worldName + "' does not exist!");
                }
                break;
            // World that are loaded or unloaded
            case "both":
                if (!this.worldManager.isWorld(worldName)) {
                    throw new ConditionFailedException("World with name '" + worldName + "' does not exist!");
                }
                break;
            // World that are does not exist
            case "new":
                if (this.worldManager.isWorld(worldName)) {
                    throw new ConditionFailedException("World with name '" + worldName + "' already exists!");
                }
                switch (WorldNameChecker.checkName(worldName)) {
                    case INVALID_CHARS ->
                            throw new ConditionFailedException("World name '" + worldName + "' contains invalid characters!");
                    case BLACKLISTED ->
                            throw new ConditionFailedException("World name '" + worldName + "' is used for critical server operations and is blacklisted!");
                }
                break;
            // Probably a typo happened here
            default:
                throw new ConditionFailedException("Unknown scope '" + scope + "'!");
        }
    }
}
