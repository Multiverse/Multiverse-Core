package com.onarandombox.MultiverseCore.commandtools;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandContexts;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * Class to parse command arguments into its object and validate them.
 */
public class MVCommandParser extends PaperCommandContexts {

    public MVCommandParser(MVCommandManager manager) {
        super(manager);

        // Contexts

        // Conditions
        manager.getCommandConditions().addCondition(int.class, "debuglevel", this::checkDebugLevel);
    }

    private void checkDebugLevel(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                 @NotNull BukkitCommandExecutionContext executionContext,
                                 int level) {

        if (level < 0 || level > 3) {
            throw new ConditionFailedException("Invalid debug mode level. Please use a number 0-3 "
                + ChatColor.AQUA + "(3 being many many messages!)");
        }
    }
}
