package org.mvplugins.multiverse.core.commands;

import java.util.ArrayList;
import java.util.List;

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
import org.mvplugins.multiverse.core.commandtools.flag.CommandFlag;
import org.mvplugins.multiverse.core.commandtools.flag.CommandValueFlag;
import org.mvplugins.multiverse.core.commandtools.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.commandtools.flags.FilterCommandFlag;
import org.mvplugins.multiverse.core.commandtools.flags.PageCommandFlag;
import org.mvplugins.multiverse.core.display.ContentDisplay;
import org.mvplugins.multiverse.core.display.filters.ContentFilter;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.handlers.PagedSendHandler;
import org.mvplugins.multiverse.core.display.parsers.ListContentProvider;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.entrycheck.WorldEntryChecker;
import org.mvplugins.multiverse.core.world.entrycheck.WorldEntryCheckerProvider;

@Service
@CommandAlias("mv")
final class ListCommand extends CoreCommand {

    private final WorldManager worldManager;
    private final WorldEntryCheckerProvider worldEntryCheckerProvider;

    private final CommandValueFlag<Integer> pageFlag = flag(PageCommandFlag.create());

    private final CommandValueFlag<ContentFilter> filterFlag = flag(FilterCommandFlag.create());

    private final CommandFlag rawFlag = flag(CommandFlag.builder("--raw")
            .addAlias("-r")
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

    @CommandAlias("mvlist|mvl")
    @Subcommand("list")
    @CommandPermission("multiverse.core.list.worlds")
    @CommandCompletion("@flags:groupName=mvlistcommand")
    @Syntax("--filter [filter] --page [page] --raw")
    @Description("Displays a listing of all worlds that you can enter.")
    public void onListCommand(
            MVCommandIssuer issuer,

            @Syntax("[--filter <filter>] [--page <page>]")
            @Description("Filters the list of worlds by the given regex and displays the given page.")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);
        ContentDisplay.create()
                .addContent(ListContentProvider.forContent(getListContents(issuer, parsedFlags.hasFlag(rawFlag))))
                .withSendHandler(PagedSendHandler.create()
                        .withHeader("%s====[ Multiverse World List ]====", ChatColor.GOLD)
                        .withTargetPage(parsedFlags.flagValue(pageFlag, 1))
                        .withFilter(parsedFlags.flagValue(filterFlag, DefaultContentFilter.get())))
                .send(issuer);
    }

    private List<String> getListContents(MVCommandIssuer issuer, boolean useRawNames) {
        List<String> worldList = new ArrayList<>();
        WorldEntryChecker worldEntryChecker = worldEntryCheckerProvider.forSender(issuer.getIssuer());

        worldManager.getLoadedWorlds().stream()
                .filter(world -> worldEntryChecker.canAccessWorld(world).isSuccess())
                .filter(world -> canSeeWorld(issuer, world))
                .map(world -> hiddenText(world) + getWorldName(world, useRawNames) + " - "
                        + parseColouredEnvironment(world.getEnvironment()))
                .sorted()
                .forEach(worldList::add);

        worldManager.getUnloadedWorlds().stream()
                .filter(world -> worldEntryChecker.canAccessWorld(world).isSuccess())
                .map(world -> ChatColor.GRAY + getWorldName(world, useRawNames) + " - UNLOADED")
                .sorted()
                .forEach(worldList::add);

        return worldList;
    }

    /**
     * Gets a world's name or alias.
     *
     * @param world The world to retrieve the name of
     * @param useRawNames True to return the name, false to return the alias
     * @return The name
     */
    private String getWorldName(MultiverseWorld world, boolean useRawNames) {
        if (useRawNames) {
            return world.getName();
        }

        return world.getAlias();
    }

    private boolean canSeeWorld(MVCommandIssuer issuer, MultiverseWorld world) {
        return !world.isHidden()
                // TODO: Refactor stray permission check
                || issuer.hasPermission("multiverse.core.modify");
    }

    private String hiddenText(MultiverseWorld world) {
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
