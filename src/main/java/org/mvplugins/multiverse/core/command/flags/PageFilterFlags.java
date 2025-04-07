package org.mvplugins.multiverse.core.command.flags;

import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.command.flag.CommandFlagsManager;
import org.mvplugins.multiverse.core.command.flag.CommandValueFlag;
import org.mvplugins.multiverse.core.command.flag.FlagBuilder;
import org.mvplugins.multiverse.core.display.filters.ContentFilter;

@Service
public class PageFilterFlags extends FlagBuilder {

    public static final String NAME = "pagefilter";

    protected PageFilterFlags(@NotNull String name, @NotNull CommandFlagsManager flagsManager) {
        super(name, flagsManager);
    }

    @Inject
    protected PageFilterFlags(@NotNull CommandFlagsManager flagsManager) {
        super(NAME, flagsManager);
    }

    public final CommandValueFlag<Integer> page = flag(PageCommandFlag.create());

    public final CommandValueFlag<ContentFilter> filter = flag(FilterCommandFlag.create());
}
