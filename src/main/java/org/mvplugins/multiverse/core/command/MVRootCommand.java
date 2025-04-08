package org.mvplugins.multiverse.core.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.OpenBukkitRootCommand;
import org.mvplugins.multiverse.core.utils.StringFormatter;

public class MVRootCommand extends OpenBukkitRootCommand {
    protected MVRootCommand(BukkitCommandManager manager, String name) {
        super(manager, name);
    }

    @Override
    public BaseCommand execute(CommandIssuer sender, String commandLabel, String[] args) {
        String[] quoteFormatedArgs = StringFormatter.parseQuotesInArgs(args).toArray(String[]::new);
        return super.execute(sender, commandLabel, quoteFormatedArgs);
    }
}
