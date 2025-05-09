package org.mvplugins.multiverse.core.commands;

import java.util.List;

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

import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.command.flags.PageFilterFlags;
import org.mvplugins.multiverse.core.display.ContentDisplay;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.handlers.PagedSendHandler;
import org.mvplugins.multiverse.core.display.parsers.ListContentProvider;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.StringFormatter;
import org.mvplugins.multiverse.core.world.generators.GeneratorPlugin;
import org.mvplugins.multiverse.core.world.generators.GeneratorProvider;

/**
 * List all gamerules in your current or specified world.
 */
@Service
class GeneratorsCommand extends CoreCommand {

    private final GeneratorProvider generatorProvider;
    private final PageFilterFlags flags;

    @Inject
    GeneratorsCommand(@NotNull GeneratorProvider generatorProvider, @NotNull PageFilterFlags flags) {
        this.generatorProvider = generatorProvider;
        this.flags = flags;
    }

    @Subcommand("generators list")
    @CommandPermission("multiverse.core.generator")
    @CommandCompletion("@flags:groupName=" + PageFilterFlags.NAME)
    @Syntax("[--page <page>] [--filter <filter>]")
    @Description("{@@mv-core.generators.description}")
    void onGeneratorsListCommand(
            @NotNull MVCommandIssuer issuer,

            @Optional
            @Syntax("[--page <page>] [--filter <filter>]")
            @Description("{@@mv-core.generators.description.flags}")
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);

        // Get the generators loaded using the command suggestions
        List<String> generators = (List<String>) generatorProvider.suggestGeneratorString("");

        // Tell the user if we cannot find any generator plugins, then abort
        if (generators.isEmpty()) {
            issuer.sendMessage(MVCorei18n.GENERATORS_EMPTY);
            return;
        }

        ContentDisplay.create()
                .addContent(ListContentProvider.forContent(generators))
                .withSendHandler(PagedSendHandler.create()
                        .withHeader("%s====[ Multiverse Generator List ]====", ChatColor.AQUA)
                        .doPagination(true)
                        .withTargetPage(parsedFlags.flagValue(flags.page, 1))
                        .withFilter(parsedFlags.flagValue(flags.filter, DefaultContentFilter.get())))
                .send(issuer);
    }

    @Subcommand("generators info")
    @CommandPermission("multiverse.core.generator")
    @CommandCompletion("@generatorplugins")
    @Syntax("<generator>")
    void onGeneratorsInfoCommand(
            @NotNull MVCommandIssuer issuer,

            @Syntax("<generator>")
            GeneratorPlugin generatorPlugin
    ) {
        issuer.sendMessage(ChatColor.RESET + "Generator Plugin: " + generatorPlugin.getPluginName());
        issuer.sendMessage(ChatColor.RESET + "Example usages: ");
        issuer.sendMessage(ChatColor.RESET + StringFormatter.join(generatorPlugin.getExampleUsages(), "\n"));
        issuer.sendMessage(ChatColor.RESET + "Link to more info: " + generatorPlugin.getInfoLink());
    }

    @Service
    private static final class LegacyAlias extends GeneratorsCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(@NotNull GeneratorProvider generatorProvider, @NotNull PageFilterFlags flags) {
            super(generatorProvider, flags);
        }

        @Override
        @CommandAlias("mvgenerators|mvgens")
        void onGeneratorsListCommand(@NotNull MVCommandIssuer issuer, String[] flags) {
            super.onGeneratorsListCommand(issuer, flags);
        }
    }
}
