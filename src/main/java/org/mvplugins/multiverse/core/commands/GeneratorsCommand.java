package org.mvplugins.multiverse.core.commands;

import java.util.List;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.commandtools.flags.CommandValueFlag;
import org.mvplugins.multiverse.core.commandtools.flags.ParsedCommandFlags;
import org.mvplugins.multiverse.core.display.ContentDisplay;
import org.mvplugins.multiverse.core.display.filters.ContentFilter;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.filters.RegexContentFilter;
import org.mvplugins.multiverse.core.display.handlers.PagedSendHandler;
import org.mvplugins.multiverse.core.display.parsers.ListContentProvider;
import org.mvplugins.multiverse.core.utils.MVCorei18n;
import org.mvplugins.multiverse.core.world.generators.GeneratorProvider;

/**
 * List all gamerules in your current or specified world.
 */
@Service
@CommandAlias("mv")
class GeneratorsCommand extends CoreCommand {

    private final GeneratorProvider generatorProvider;

    private final CommandValueFlag<Integer> PAGE_FLAG = flag(CommandValueFlag
            .builder("--page", Integer.class)
            .addAlias("-p")
            .context(value -> {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new InvalidCommandArgument("Invalid page number: " + value);
                }
            })
            .build());

    private final CommandValueFlag<ContentFilter> FILTER_FLAG = flag(CommandValueFlag
            .builder("--filter", ContentFilter.class)
            .addAlias("-f")
            .context(value -> {
                try {
                    return RegexContentFilter.fromString(value);
                } catch (IllegalArgumentException e) {
                    throw new InvalidCommandArgument("Invalid filter: " + value);
                }
            })
            .build());

    @Inject
    GeneratorsCommand(@NotNull MVCommandManager commandManager, @NotNull GeneratorProvider generatorProvider) {
        super(commandManager);
        this.generatorProvider = generatorProvider;
    }

    @CommandAlias("mvgenerators|mvgens")
    @Subcommand("generators|gens")
    @CommandPermission("multiverse.core.generator")
    @CommandCompletion("@flags:groupName=mvgeneratorscommand @flags:groupName=mvgeneratorscommand")
    @Syntax("")
    @Description("{@@mv-core.generators.description}")
    void onGamerulesCommand(
            @NotNull MVCommandIssuer issuer,

            @Optional
            @Syntax("[--page <page>] [--filter <filter>]")
            @Description("{@@mv-core.generators.description.flags}")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        // Get the generators loaded using the command suggestions
        List<String> generators = (List<String>) generatorProvider.suggestGeneratorString("");

        // Tell the user if we cannot find any generator plugins, then abort
        if (generators.isEmpty()) {
            issuer.sendMessage(commandManager.formatMessage(issuer, MessageType.INFO, MVCorei18n.GENERATORS_EMPTY));
            return;
        }

        ContentDisplay.create()
                .addContent(ListContentProvider.forContent(generators))
                .withSendHandler(PagedSendHandler.create()
                        .withHeader("%s====[ Multiverse Generator List ]====", ChatColor.AQUA)
                        .doPagination(true)
                        .withTargetPage(parsedFlags.flagValue(PAGE_FLAG, 1))
                        .withFilter(parsedFlags.flagValue(FILTER_FLAG, DefaultContentFilter.get())))
                .send(issuer);
    }
}
