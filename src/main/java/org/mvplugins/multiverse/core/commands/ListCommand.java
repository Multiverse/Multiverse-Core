package org.mvplugins.multiverse.core.commands;

import java.util.ArrayList;
import java.util.List;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.World;
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
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.entrycheck.WorldEntryChecker;
import org.mvplugins.multiverse.core.world.entrycheck.WorldEntryCheckerProvider;

@Service
@CommandAlias("mv")
class ListCommand extends MultiverseCommand {

    private final WorldManager worldManager;
    private final WorldEntryCheckerProvider worldEntryCheckerProvider;

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
    ListCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull WorldManager worldManager,
            @NotNull WorldEntryCheckerProvider worldEntryCheckerProvider) {
        super(commandManager);
        this.worldManager = worldManager;
        this.worldEntryCheckerProvider = worldEntryCheckerProvider;
    }

    @Subcommand("list")
    @CommandPermission("multiverse.core.list.worlds")
    @CommandCompletion("@flags:groupName=mvlistcommand")
    @Syntax("--filter [filter] --page [page]")
    @Description("Displays a listing of all worlds that you can enter.")
    public void onListCommand(
            MVCommandIssuer issuer,

            @Syntax("[--filter <filter>] [--page <page>]")
            @Description("Filters the list of worlds by the given regex and displays the given page.")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);
        ContentDisplay.create()
                .addContent(ListContentProvider.forContent(getListContents(issuer)))
                .withSendHandler(PagedSendHandler.create()
                        .withHeader("%s====[ Multiverse World List ]====", ChatColor.GOLD)
                        .withTargetPage(parsedFlags.flagValue(PAGE_FLAG, 1))
                        .withFilter(parsedFlags.flagValue(FILTER_FLAG, DefaultContentFilter.get())))
                .send(issuer);
    }

    private List<String> getListContents(MVCommandIssuer issuer) {
        List<String> worldList = new ArrayList<>();
        WorldEntryChecker worldEntryChecker = worldEntryCheckerProvider.forSender(issuer.getIssuer());

        worldManager.getLoadedWorlds().stream()
                .filter(world -> worldEntryChecker.canAccessWorld(world).isSuccess())
                .filter(world -> canSeeWorld(issuer, world))
                .map(world -> hiddenText(world) + world.getAlias() + " - " + parseColouredEnvironment(world.getEnvironment()))
                .sorted()
                .forEach(worldList::add);

        worldManager.getUnloadedWorlds().stream()
                .filter(world -> worldEntryChecker.canAccessWorld(world).isSuccess())
                .map(world -> ChatColor.GRAY + world.getAlias() + " - UNLOADED")
                .sorted()
                .forEach(worldList::add);

        return worldList;
    }

    private boolean canSeeWorld(MVCommandIssuer issuer, LoadedMultiverseWorld world) {
        return !world.isHidden()
                || issuer.hasPermission("multiverse.core.modify"); // TODO: Refactor stray permission check
    }

    private String hiddenText(LoadedMultiverseWorld world) {
        return (world.isHidden()) ? String.format("%s[H] ", ChatColor.GRAY) : "";
    }

    private String parseColouredEnvironment(World.Environment env) {
        ChatColor color = switch (env) {
            case NETHER -> ChatColor.RED;
            case NORMAL -> ChatColor.GREEN;
            case THE_END -> ChatColor.AQUA;
            default -> ChatColor.GOLD;
        };
        return color + env.toString();
    }
}
