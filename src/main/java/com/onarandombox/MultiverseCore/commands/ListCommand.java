package com.onarandombox.MultiverseCore.commands;

import java.util.ArrayList;
import java.util.List;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlagGroup;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandValueFlag;
import com.onarandombox.MultiverseCore.commandtools.flags.ParsedCommandFlags;
import com.onarandombox.MultiverseCore.display.ContentDisplay;
import com.onarandombox.MultiverseCore.display.filters.ContentFilter;
import com.onarandombox.MultiverseCore.display.filters.DefaultContentFilter;
import com.onarandombox.MultiverseCore.display.filters.RegexContentFilter;
import com.onarandombox.MultiverseCore.display.handlers.PagedSendHandler;
import com.onarandombox.MultiverseCore.display.parsers.ListContentProvider;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class ListCommand extends MultiverseCoreCommand {
    public ListCommand(@NotNull MultiverseCore plugin) {
        super(plugin);

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
    public void onListCommand(BukkitCommandIssuer issuer,

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
                        .withFilter(parsedFlags.flagValue("--filter", DefaultContentFilter.get(), ContentFilter.class))
                        .withLinesPerPage(4)) //TODO Change back after testing
                .send(issuer);
    }

    private List<String> getListContents(BukkitCommandIssuer issuer) {
        Player player = issuer.isPlayer() ? issuer.getPlayer() : null;
        List<String> worldList =  new ArrayList<>();

        this.plugin.getMVWorldManager().getMVWorlds().stream()
                .filter(world -> player == null || plugin.getMVPerms().canEnterWorld(player, world))
                .filter(world -> canSeeWorld(player, world))
                .map(world -> hiddenText(world) + world.getColoredWorldString() + " - " + parseColouredEnvironment(world.getEnvironment()))
                .sorted()
                .forEach(worldList::add);

        this.plugin.getMVWorldManager().getUnloadedWorlds().stream()
                .filter(world -> plugin.getMVPerms().hasPermission(issuer.getIssuer(), "multiverse.access." + world, true))
                .map(world -> ChatColor.GRAY + world + " - UNLOADED")
                .sorted()
                .forEach(worldList::add);

        return worldList;
    }

    private boolean canSeeWorld(Player player, MVWorld world) {
        return !world.isHidden()
                || player == null
                || this.plugin.getMVPerms().hasPermission(player, "multiverse.core.modify", true);
    }

    private String hiddenText(MVWorld world) {
        return (world.isHidden()) ? String.format("%s[H] ", ChatColor.GRAY) : "";
    }

    private String parseColouredEnvironment(World.Environment env) {
        ChatColor color = ChatColor.GOLD;
        switch (env) {
            case NETHER:
                color = ChatColor.RED;
                break;
            case NORMAL:
                color = ChatColor.GREEN;
                break;
            case THE_END:
                color = ChatColor.AQUA;
                break;
        }
        return color + env.toString();
    }
}
