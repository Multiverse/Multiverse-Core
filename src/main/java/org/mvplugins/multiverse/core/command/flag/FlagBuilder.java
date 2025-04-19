package org.mvplugins.multiverse.core.command.flag;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds and registers a flag group on initialization. Flags should be final fields that calls {@link #flag(CommandFlag)}.
 */
@Contract
public abstract class FlagBuilder {

    private final String name;
    private final CommandFlagsManager flagsManager;
    private final List<CommandFlag> flags;

    protected FlagBuilder(@NotNull String name, @NotNull CommandFlagsManager flagsManager) {
        this.name = name;
        this.flagsManager = flagsManager;
        this.flags = new ArrayList<>();
    }

    @PostConstruct
    private void postConstruct() {
        CommandFlagGroup.Builder flagGroupBuilder = CommandFlagGroup.builder(name);
        flags.forEach(flagGroupBuilder::add);
        registerFlagGroup(flagGroupBuilder.build());
    }

    private void registerFlagGroup(@NotNull CommandFlagGroup flagGroup) {
        flagsManager.registerFlagGroup(flagGroup);
        Logging.finer("Registered flag group: " + flagGroup.getName());
    }

    /**
     * Add a new flag to the flag builder.
     *
     * @param flag  The flag to add.
     * @param <T>   The type of the flag.
     * @return The flag.
     */
    protected <T extends CommandFlag> T flag(T flag) {
        flags.add(flag);
        Logging.finer("Added flag: " + flag);
        return flag;
    }

    /**
     * Parses flags.
     *
     * @param flags The raw string array to parse into flags.
     * @return The parsed flags.
     */
    public @NotNull ParsedCommandFlags parse(@NotNull String[] flags) {
        return flagsManager.parse(name, flags);
    }
}
