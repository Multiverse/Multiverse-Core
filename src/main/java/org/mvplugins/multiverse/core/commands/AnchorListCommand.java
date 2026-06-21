package org.mvplugins.multiverse.core.commands;

import java.util.List;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.anchor.AnchorManager;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.command.flags.PageFilterFlags;
import org.mvplugins.multiverse.core.display.ContentDisplay;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.handlers.PagedSendHandler;
import org.mvplugins.multiverse.core.display.parsers.ListContentProvider;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.teleportation.LocationManipulation;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

@Service
final class AnchorListCommand extends CoreCommand {

    private final AnchorManager anchorManager;
    private final LocationManipulation locationManipulation;
    private final PageFilterFlags flags;

    @Inject
    AnchorListCommand(
            @NotNull AnchorManager anchorManager,
            @NotNull LocationManipulation locationManipulation,
            @NotNull PageFilterFlags flags
    ) {
        this.anchorManager = anchorManager;
        this.locationManipulation = locationManipulation;
        this.flags = flags;
    }

    @Subcommand("anchor list")
    @CommandPermission("multiverse.core.anchor.list")
    @CommandCompletion("@flags:groupName=" + PageFilterFlags.NAME)
    @Syntax("[--page <page>] [--filter <filter>]")
    @Description("")
    void onAnchorListCommand(
            MVCommandIssuer issuer,

            @Optional
            @Syntax("[--page <page>] [--filter <filter>]")
            @Description("")
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);
        ContentDisplay.create()
                .addContent(ListContentProvider.forContent(getAnchors(issuer.getPlayer())))
                .withSendHandler(PagedSendHandler.create()
                        .withHeader(Message.of(MVCorei18n.ANCHOR_LIST_HEADER))
                        .doPagination(true)
                        .withTargetPage(parsedFlags.flagValue(flags.page, 1))
                        .withFilter(parsedFlags.flagValue(flags.filter, DefaultContentFilter.get())))
                .send(issuer);
    }

    private List<? extends Message> getAnchors(Player player) {
        return anchorManager.getAnchors(player).stream()
                .map(anchor ->
                        Message.of(MVCorei18n.ANCHOR_LIST_ENTRY,
                                replace("{anchor}").with(anchor.getName()),
                                replace("{location}").with(locationManipulation.locationToString(anchor.getLocation()))))
                .toList();
    }
}
