package org.mvplugins.multiverse.core.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.command.flags.PageFilterFlags;
import org.mvplugins.multiverse.core.display.ContentDisplay;
import org.mvplugins.multiverse.core.display.filters.ContentFilter;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.handlers.PagedSendHandler;
import org.mvplugins.multiverse.core.display.parsers.MapContentProvider;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
class WhoCommand extends CoreCommand {

    private final WorldManager worldManager;
    private final PageFilterFlags flags;

    @Inject
    WhoCommand(@NotNull WorldManager worldManager, @NotNull PageFilterFlags flags) {
        this.worldManager = worldManager;
        this.flags = flags;
    }

    @Subcommand("whoall")
    @CommandPermission("multiverse.core.list.who.all")
    @CommandCompletion("@flags:groupName=" + PageFilterFlags.NAME)
    @Syntax("[--page <page>] [--filter <filter>]")
    @Description("{@@mv-core.who.all.description}")
    void onWhoAllCommand(
            MVCommandIssuer issuer,

            @Optional
            @Syntax("[--page <page>] [--filter <filter>]")
            @Description("{@@mv-core.who.flags.description}")
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);

        // Send the display
        getListDisplay(worldManager.getLoadedWorlds(),
                        parsedFlags.flagValue(flags.page, 1),
                        parsedFlags.flagValue(flags.filter, DefaultContentFilter.get()),
                        true)
                .send(issuer);

    }

    @Subcommand("who")
    @CommandPermission("multiverse.core.list.who")
    @CommandCompletion("@mvworlds:scope=both @flags:groupName=" + PageFilterFlags.NAME)
    @Syntax("<world> [--page <page>] [--filter <filter>]")
    @Description("{@@mv-core.who.description}")
    void onWhoCommand(
            MVCommandIssuer issuer,
            @Flags("resolve=issuerAware")
            @Optional
            @Syntax("<world>")
            @Description("{@@mv-core.who.world.description}")
            LoadedMultiverseWorld inputtedWorld,

            @Optional
            @Syntax("[--page <page>] [--filter <filter>]")
            @Description("{@@mv-core.who.flags.description}")
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);

        // Send the display
        getListDisplay(inputtedWorld,
                        parsedFlags.flagValue(flags.page, 1),
                        parsedFlags.flagValue(flags.filter, DefaultContentFilter.get()),
                        false)
                .send(issuer);
    }

    private Message phrasePlayerList(List<Player> players) {
        return Message.of(players.stream().map(Player::getName).collect(Collectors.joining(", ")));
    }

    private ContentDisplay getListDisplay(LoadedMultiverseWorld world, int page,
                                          ContentFilter filter, boolean ignoreEmptyWorlds) {
        Collection<LoadedMultiverseWorld> listingWorlds = new ArrayList<>();
        listingWorlds.add(world);
        return getListDisplay(listingWorlds, page, filter, ignoreEmptyWorlds);
    }

    private ContentDisplay getListDisplay(Collection<LoadedMultiverseWorld> worlds, int page,
                                          ContentFilter filter, boolean ignoreEmptyWorlds) {
        Map<String, Message> outMap = new HashMap<>();

        // Add all the worlds to our hashmap
        for (LoadedMultiverseWorld world : worlds) {
            @Nullable List<Player> players = world.getPlayers().getOrNull();

            if (players != null && !players.isEmpty()) {
                outMap.put(world.getAliasOrName(), phrasePlayerList(players));
            } else if (!ignoreEmptyWorlds) {
                // If the world has 0 players in it, say that it is empty
                outMap.put(world.getAliasOrName(), Message.of(MVCorei18n.WHO_EMPTY));
            }
        }

        return ContentDisplay.create()
                .addContent(MapContentProvider.forContent(outMap))
                .withSendHandler(PagedSendHandler.create()
                        .withHeader(Message.of(MVCorei18n.WHO_HEADER))
                        .doPagination(true)
                        .withTargetPage(page)
                        .withFilter(filter));
    }

    @Service
    private static final class LegacyAlias extends WhoCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(@NotNull WorldManager worldManager, @NotNull PageFilterFlags flags) {
            super(worldManager, flags);
        }

        @Override
        @CommandAlias("mvwhoall")
        void onWhoAllCommand(MVCommandIssuer issuer, String[] flags) {
            super.onWhoAllCommand(issuer, flags);
        }

        @Override
        @CommandAlias("mvwho|mvw")
        void onWhoCommand(MVCommandIssuer issuer, LoadedMultiverseWorld inputtedWorld, String[] flags) {
            super.onWhoCommand(issuer, inputtedWorld, flags);
        }
    }
}
