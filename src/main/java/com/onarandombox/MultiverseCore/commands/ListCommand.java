package com.onarandombox.MultiverseCore.commands;

import java.util.ArrayList;
import java.util.List;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.commandtools.MVCommandIssuer;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlagGroup;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandValueFlag;
import com.onarandombox.MultiverseCore.commandtools.flags.ParsedCommandFlags;
import com.onarandombox.MultiverseCore.display.ContentDisplay;
import com.onarandombox.MultiverseCore.display.filters.ContentFilter;
import com.onarandombox.MultiverseCore.display.filters.DefaultContentFilter;
import com.onarandombox.MultiverseCore.display.filters.RegexContentFilter;
import com.onarandombox.MultiverseCore.display.handlers.PagedSendHandler;
import com.onarandombox.MultiverseCore.display.parsers.ListContentProvider;
import com.onarandombox.MultiverseCore.worldnew.LoadedMultiverseWorld;
import com.onarandombox.MultiverseCore.worldnew.WorldManager;
import com.onarandombox.MultiverseCore.worldnew.entrycheck.WorldEntryChecker;
import com.onarandombox.MultiverseCore.worldnew.entrycheck.WorldEntryCheckerProvider;
import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
@CommandAlias("mv")
public class ListCommand extends MultiverseCommand {

    private final WorldManager worldManager;
    private final WorldEntryCheckerProvider worldEntryCheckerProvider;

    @Inject
    public ListCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull WorldManager worldManager,
            @NotNull WorldEntryCheckerProvider worldEntryCheckerProvider
    ) {
        super(commandManager);
        this.worldManager = worldManager;
        this.worldEntryCheckerProvider = worldEntryCheckerProvider;

        registerFlagGroup(CommandFlagGroup.builder("mvlist")
                .add(CommandValueFlag.builder("--filter", ContentFilter.class)
                        .addAlias("-f")
                        .context((value) -> {
                            try {
                                return RegexContentFilter.fromString(value);
                            } catch (IllegalArgumentException e) {
                                throw new InvalidCommandArgument("Invalid filter: " + value);
                            }
                        })
                        .build())
                .add(CommandValueFlag.builder("--page", Integer.class)
                        .addAlias("-p")
                        .context((value) -> {
                            try {
                                return Integer.parseInt(value);
                            } catch (NumberFormatException e) {
                                throw new InvalidCommandArgument("Invalid page number: " + value);
                            }
                        })
                        .build())
                .build());
    }

    @Subcommand("list")
    @CommandPermission("multiverse.core.list.worlds")
    @CommandCompletion("@flags:groupName=mvlist")
    @Syntax("--filter [filter] --page [page]")
    @Description("Displays a listing of all worlds that you can enter.")
    public void onListCommand(MVCommandIssuer issuer,

                              @Syntax("--filter [filter] --page [page]")
                              @Description("Filters the list of worlds by the given regex and displays the given page.")
                              String[] flags
    ) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);
        ContentDisplay.create()
                .addContent(ListContentProvider.forContent(getListContents(issuer)))
                .withSendHandler(PagedSendHandler.create()
                        .withHeader("%s====[ Multiverse World List ]====", ChatColor.GOLD)
                        .withTargetPage(parsedFlags.flagValue("--page", 1, Integer.class))
                        .withFilter(parsedFlags.flagValue("--filter", DefaultContentFilter.get(), ContentFilter.class)))
                .send(issuer);
    }

    private List<String> getListContents(MVCommandIssuer issuer) {
        List<String> worldList =  new ArrayList<>();
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
