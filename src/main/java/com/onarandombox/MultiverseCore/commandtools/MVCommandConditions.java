package com.onarandombox.MultiverseCore.commandtools;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.BukkitConditionContext;
import co.aikar.commands.CommandConditions;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.world.WorldNameChecker;

public class MVCommandConditions {
    static void load(MVCommandManager commandManager, MultiverseCore plugin) {
        new MVCommandConditions(commandManager, plugin);
    }

    private final MVCommandManager commandManager;
    private final MultiverseCore plugin;

    public MVCommandConditions(MVCommandManager commandManager, MultiverseCore plugin) {
        this.commandManager = commandManager;
        this.plugin = plugin;

        CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> conditions
                = commandManager.getCommandConditions();

        conditions.addCondition(String.class, "worldname", this::checkWorldName);
    }

    private void checkWorldName(ConditionContext<BukkitCommandIssuer> context,
                                BukkitCommandExecutionContext executionContext,
                                String worldName
    ) {
        String scope = context.getConfigValue("scope", "loaded");

        switch (scope) {
            // Worlds that are loaded
            case "loaded":
                if (!this.plugin.getMVWorldManager().isMVWorld(worldName)) {
                    throw new ConditionFailedException("World with name '" + worldName + "' does not exist or is not loaded!");
                }
                break;
            // Worlds that are unloaded
            case "unloaded":
                if (!this.plugin.getMVWorldManager().hasUnloadedWorld(worldName, false)) {
                    if (this.plugin.getMVWorldManager().isMVWorld(worldName)) {
                        throw new ConditionFailedException("World with name '" + worldName + "' is loaded already!");
                    }
                    throw new ConditionFailedException("World with name '" + worldName + "' does not exist!");
                }
                break;
            // World that are loaded or unloaded
            case "both":
                if (!this.plugin.getMVWorldManager().hasUnloadedWorld(worldName, true)) {
                    throw new ConditionFailedException("World with name '" + worldName + "' does not exist!");
                }
                break;
            // World that are does not exist
            case "new":
                if (this.plugin.getMVWorldManager().isMVWorld(worldName)) {
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
