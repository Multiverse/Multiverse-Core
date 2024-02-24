package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.flags.CommandValueFlag;
import org.mvplugins.multiverse.core.commandtools.flags.ParsedCommandFlags;
import org.mvplugins.multiverse.core.display.ContentDisplay;
import org.mvplugins.multiverse.core.display.filters.ContentFilter;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.filters.RegexContentFilter;
import org.mvplugins.multiverse.core.display.handlers.PagedSendHandler;
import org.mvplugins.multiverse.core.display.parsers.MapContentProvider;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@CommandAlias("mv")
public class WhoCommand extends MultiverseCommand {

    private final WorldManager worldManager;

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
    WhoCommand(@NotNull MVCommandManager commandManager, @NotNull WorldManager worldManager) {
        super(commandManager);
        this.worldManager = worldManager;
    }

    @Subcommand("whoall")
    @CommandPermission("multiverse.core.list.who.all")
    @CommandCompletion("@flags:groupName=mvwhocommand")
    @Syntax("[--page <page>] [--filter <filter>]")
    @Description("{@@mv-core.who.all.description}")
    void onWhoAllCommand(
            MVCommandIssuer issuer,

            @Optional
            @Syntax("[--page <page>] [--filter <filter>]")
            @Description("{@@mv-core.who.flags}")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        // Send the display
        getListDisplay(worldManager.getLoadedWorlds(), parsedFlags.flagValue(PAGE_FLAG, 1), parsedFlags.flagValue(FILTER_FLAG, DefaultContentFilter.get())).send(issuer);

    }

    @Subcommand("who")
    @CommandPermission("multiverse.core.list.who")
    @CommandCompletion("@mvworlds:scope=both @flags:groupName=mvwhocommand")
    @Syntax("<world> [--page <page>] [--filter <filter>]")
    @Description("{@@mv-core.who.description}")
    void onWhoCommand(
            MVCommandIssuer issuer,

            @Syntax("<world>")
            @Description("{@@mv-core.who.world.description}")
            LoadedMultiverseWorld inputtedWorld,

            @Optional
            @Syntax("[--page <page>] [--filter <filter>]")
            @Description("{@@mv-core.who.flags}")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        // Send the display
        getListDisplay(inputtedWorld, parsedFlags.flagValue(PAGE_FLAG, 1), parsedFlags.flagValue(FILTER_FLAG, DefaultContentFilter.get())).send(issuer);
    }

    private String phrasePlayerList(List<Player> players) {
        return players.stream().map(Player::getName).collect(Collectors.joining(", "));
    }

    private ContentDisplay getListDisplay(LoadedMultiverseWorld world, int page, ContentFilter filter) {
        Collection<LoadedMultiverseWorld> listingWorlds = new ArrayList<>();
        listingWorlds.add(world);
        return getListDisplay(listingWorlds, page, filter);
    }

    private ContentDisplay getListDisplay(Collection<LoadedMultiverseWorld> worlds, int page, ContentFilter filter) {
        HashMap<String, String> outMap = new HashMap<>();

        // Add all the worlds to our hashmap
        for (LoadedMultiverseWorld world : worlds) {
            List<Player> players = world.getPlayers().getOrNull();

            // If the world has 0 players in it, ignore it
            if (players.isEmpty()) {
                continue;
            }
            outMap.put(world.getAlias(), phrasePlayerList(players));
        }

        return ContentDisplay.create()
                .addContent(MapContentProvider.forContent(outMap))
                .withSendHandler(PagedSendHandler.create()
                        .withHeader("%s====[ Multiverse World Players List ]====", ChatColor.AQUA)
                        .doPagination(true)
                        .withTargetPage(page)
                        .withFilter(filter));
    }
}
