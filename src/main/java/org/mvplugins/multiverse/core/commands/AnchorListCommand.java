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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.anchor.AnchorManager;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.flag.CommandValueFlag;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.command.flags.FilterCommandFlag;
import org.mvplugins.multiverse.core.command.flags.PageCommandFlag;
import org.mvplugins.multiverse.core.display.ContentDisplay;
import org.mvplugins.multiverse.core.display.filters.ContentFilter;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.handlers.PagedSendHandler;
import org.mvplugins.multiverse.core.display.parsers.ListContentProvider;
import org.mvplugins.multiverse.core.teleportation.LocationManipulation;

@Service
final class AnchorListCommand extends CoreCommand {

    private final AnchorManager anchorManager;
    private final LocationManipulation locationManipulation;

    private final CommandValueFlag<Integer> pageFlag = flag(PageCommandFlag.create());

    private final CommandValueFlag<ContentFilter> filterFlag = flag(FilterCommandFlag.create());

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
                        .withTargetPage(parsedFlags.flagValue(pageFlag, 1))
                        .withFilter(parsedFlags.flagValue(filterFlag, DefaultContentFilter.get())))
                .send(issuer);
    }

    private List<String> getAnchors(Player player) {
        return anchorManager.getAnchors(player).stream()
                .map(anchor ->
                        "&a%s&7 - &f%s".formatted(
                                anchor.getName(), locationManipulation.locationToString(anchor.getLocation())))
                .toList();
    }
}
