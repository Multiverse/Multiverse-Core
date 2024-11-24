package org.mvplugins.multiverse.core.commands;

import java.util.List;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.anchor.AnchorManager;
import org.mvplugins.multiverse.core.api.LocationManipulation;
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

@Service
@CommandAlias("mv")
class AnchorListCommand extends MultiverseCommand {

    private final AnchorManager anchorManager;
    private final LocationManipulation locationManipulation;

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
    AnchorListCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull AnchorManager anchorManager,
            @NotNull LocationManipulation locationManipulation) {
        super(commandManager);
        this.anchorManager = anchorManager;
        this.locationManipulation = locationManipulation;
    }

    @Subcommand("anchor list")
    @CommandPermission("multiverse.core.anchor.list")
    @CommandCompletion("@flags:groupName=mvanchorlistcommand")
    @Syntax("[--page <page>] [--filter <filter>]")
    @Description("")
    void onAnchorListCommand(
            MVCommandIssuer issuer,

            @Optional
            @Syntax("[--page <page>] [--filter <filter>]")
            @Description("")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);
        ContentDisplay.create()
                .addContent(ListContentProvider.forContent(getAnchors(issuer.getPlayer())))
                .withSendHandler(PagedSendHandler.create()
                        .withHeader("&3==== [ Multiverse Anchors ] ====")
                        .doPagination(true)
                        .withTargetPage(parsedFlags.flagValue(PAGE_FLAG, 1))
                        .withFilter(parsedFlags.flagValue(FILTER_FLAG, DefaultContentFilter.get())))
                .send(issuer);
    }

    private List<String> getAnchors(Player player) {
        return anchorManager.getAnchors(player).stream().map(anchorName -> {
            Location anchorLocation = anchorManager.getAnchorLocation(anchorName);
            return "&a" + anchorName + "&7 - &f" + locationManipulation.locationToString(anchorLocation);
        }).toList();
    }
}