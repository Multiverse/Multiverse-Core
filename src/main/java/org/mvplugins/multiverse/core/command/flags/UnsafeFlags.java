package org.mvplugins.multiverse.core.command.flags;

import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.command.flag.CommandFlag;
import org.mvplugins.multiverse.core.command.flag.CommandFlagsManager;
import org.mvplugins.multiverse.core.command.flag.FlagBuilder;

@Service
public class UnsafeFlags extends FlagBuilder {

    public static final String NAME = "unsafe";

    protected UnsafeFlags(@NotNull String name, @NotNull CommandFlagsManager flagsManager) {
        super(name, flagsManager);
    }

    @Inject
    protected UnsafeFlags(@NotNull CommandFlagsManager flagsManager) {
        super(NAME, flagsManager);
    }

    public final CommandFlag unsafe = flag(CommandFlag.builder("--unsafe")
            .addAlias("-u")
            .build());
}
