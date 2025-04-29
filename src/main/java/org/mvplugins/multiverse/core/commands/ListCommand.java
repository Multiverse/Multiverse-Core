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

import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.flag.CommandFlag;
import org.mvplugins.multiverse.core.command.flag.CommandFlagsManager;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.command.flags.PageFilterFlags;
import org.mvplugins.multiverse.core.display.ContentDisplay;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.handlers.PagedSendHandler;
import org.mvplugins.multiverse.core.display.parsers.ListContentProvider;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.entrycheck.WorldEntryChecker;
import org.mvplugins.multiverse.core.world.entrycheck.WorldEntryCheckerProvider;

@Service
class ListCommand extends CoreCommand {

    private final WorldManager worldManager;
    private final WorldEntryCheckerProvider worldEntryCheckerProvider;
    private final ListCommand.Flags flags;

    @Inject
    ListCommand(
            @NotNull WorldManager worldManager,
            @NotNull WorldEntryCheckerProvider worldEntryCheckerProvider,
            @NotNull Flags flags
    ) {
        this.worldManager = worldManager;
        this.worldEntryCheckerProvider = worldEntryCheckerProvider;
        this.flags = flags;
    }

    @Subcommand("list")
    @CommandPermission("multiverse.core.list.worlds")
    @CommandCompletion("@flags:groupName=" + Flags.NAME)
    @Syntax("--filter [filter] --page [page] --raw")
    @Description("{{@mv-core.list.description}}")
    public void onListCommand(
            MVCommandIssuer issuer,

            @Syntax("[--filter <filter>] [--page <page>]")
            @Description("Filters the list of worlds by the given regex and displays the given page.")
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);
        ContentDisplay.create()
                .addContent(ListContentProvider.forContent(getListContents(issuer, parsedFlags.hasFlag(flags.raw))))
                .withSendHandler(PagedSendHandler.create()
                        .noContentMessage(Message.of(MVCorei18n.LIST_NOCONTENT))
                        .withHeader(Message.of(MVCorei18n.LIST_HEADER))
                        .withTargetPage(parsedFlags.flagValue(flags.page, 1))
                        .withFilter(parsedFlags.flagValue(flags.filter, DefaultContentFilter.get())))
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

        return world.getAliasOrName();
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

    @Service
    private static final class Flags extends PageFilterFlags {

        private static final String NAME = "mvlist";

        @Inject
        private Flags(@NotNull CommandFlagsManager flagsManager) {
            super(NAME, flagsManager);
        }

        private final CommandFlag raw = flag(CommandFlag.builder("--raw")
                .addAlias("-r")
                .build());
    }

    @Service
    private static final class LegacyAlias extends ListCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(
                @NotNull WorldManager worldManager,
                @NotNull WorldEntryCheckerProvider worldEntryCheckerProvider,
                @NotNull Flags flags
        ) {
            super(worldManager, worldEntryCheckerProvider, flags);
        }

        @Override
        @CommandAlias("mvlist|mvl")
        public void onListCommand(MVCommandIssuer issuer, String[] flags) {
            super.onListCommand(issuer, flags);
        }
    }
}
