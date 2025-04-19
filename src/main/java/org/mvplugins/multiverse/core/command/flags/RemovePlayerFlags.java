package org.mvplugins.multiverse.core.command.flags;

import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.command.flag.CommandFlag;
import org.mvplugins.multiverse.core.command.flag.CommandFlagsManager;
import org.mvplugins.multiverse.core.command.flag.FlagBuilder;

@Service
public class RemovePlayerFlags extends FlagBuilder {

    public static final String NAME = "removeplayer";

    protected RemovePlayerFlags(@NotNull String name, @NotNull CommandFlagsManager flagsManager) {
        super(name, flagsManager);
    }

    @Inject
    protected RemovePlayerFlags(@NotNull CommandFlagsManager flagsManager) {
        super(NAME, flagsManager);
    }

    public final CommandFlag removePlayers = flag(CommandFlag.builder("--remove-players")
            .addAlias("-r")
            .build());
}
