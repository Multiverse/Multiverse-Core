package org.mvplugins.multiverse.core.commandtools;

import co.aikar.commands.BaseCommand;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Contract;

import org.mvplugins.multiverse.core.commandtools.flag.CommandFlag;
import org.mvplugins.multiverse.core.commandtools.flag.CommandFlagGroup;
import org.mvplugins.multiverse.core.commandtools.flag.CommandFlagsManager;
import org.mvplugins.multiverse.core.commandtools.flag.ParsedCommandFlags;

/**
 * Base class for all Multiverse commands.
 */
@Contract
public abstract class MultiverseCommand extends BaseCommand {

    /**
     * The command manager with multiverse specific methods.
     */
    protected final MVCommandManager commandManager;
    /**
     * The flags manager for the above command manager.
     */
    protected final CommandFlagsManager flagsManager;
    private final String flagGroupPrefix;
    private String flagGroupName;
    private CommandFlagGroup.Builder flagGroupBuilder;

    protected MultiverseCommand(@NotNull MVCommandManager commandManager, @NotNull String flagGroupPrefix) {
        this.commandManager = commandManager;
        this.flagsManager = commandManager.getFlagsManager();
        this.flagGroupPrefix = flagGroupPrefix;
    }

    @PostConstruct
    private void postConstruct() {
        if (flagGroupBuilder != null) {
            registerFlagGroup(flagGroupBuilder.build());
            flagGroupBuilder = null;
        }
    }

    private void registerFlagGroup(@NotNull CommandFlagGroup flagGroup) {
        if (flagGroupName != null) {
            throw new IllegalStateException("Flag group already registered! (name: " + flagGroupName + ")");
        }
        flagsManager.registerFlagGroup(flagGroup);
        flagGroupName = flagGroup.getName();
        Logging.finest("Registered flag group: " + flagGroupName);
    }

    /**
     * Add a new flag to the flag builder.
     *
     * @param flag  The flag to add.
     * @param <T>   The type of the flag.
     * @return The flag.
     */
    protected <T extends CommandFlag> T flag(T flag) {
        if (flagGroupBuilder == null) {
            flagGroupBuilder = CommandFlagGroup.builder(flagGroupPrefix + getClass().getSimpleName().toLowerCase());
        }
        flagGroupBuilder.add(flag);
        Logging.finest("Registered flag: " + flag);
        return flag;
    }

    /**
     * Parses flags.
     *
     * @param flags The raw string array to parse into flags.
     * @return The parsed flags.
     */
    protected @NotNull ParsedCommandFlags parseFlags(@NotNull String[] flags) {
        return flagsManager.parse(flagGroupName, flags);
    }
}
