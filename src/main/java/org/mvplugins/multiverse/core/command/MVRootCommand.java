package org.mvplugins.multiverse.core.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.BukkitRootCommand;
import co.aikar.commands.CommandIssuer;
import org.mvplugins.multiverse.core.utils.StringFormatter;

import java.util.List;

public class MVRootCommand extends BukkitRootCommand {
    protected MVRootCommand(BukkitCommandManager manager, String name) {
        super(manager, name);
    }

    @Override
    public BaseCommand execute(CommandIssuer sender, String commandLabel, String[] args) {
        String[] quoteFormatedArgs = StringFormatter.parseQuotesInArgs(args).toArray(String[]::new);
        return super.execute(sender, commandLabel, quoteFormatedArgs);
    }

    @Override
    public List<String> getTabCompletions(CommandIssuer sender, String alias, String[] args, boolean commandsOnly, boolean isAsync) {
        String[] quoteFormatedArgs = StringFormatter.parseQuotesInArgs(args).toArray(String[]::new);
        return super.getTabCompletions(sender, alias, quoteFormatedArgs, commandsOnly, isAsync);
    }
}
