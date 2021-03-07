/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commandtools.contexts.PageFilter;
import com.onarandombox.MultiverseCore.displaytools.ColorAlternator;
import com.onarandombox.MultiverseCore.displaytools.ContentDisplay;
import com.onarandombox.MultiverseCore.displaytools.ContentFilter;
import com.onarandombox.MultiverseCore.displaytools.DisplayHandlers;
import com.onarandombox.MultiverseCore.displaytools.DisplaySettings;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@CommandAlias("mv")
public class ListCommand extends MultiverseCoreCommand {

    public ListCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("list")
    @CommandPermission("multiverse.core.list.worlds")
    @Syntax("[filter] [page]")
    @Description("Displays a listing of all worlds that you can enter.")
    public void onListCommand(@NotNull CommandSender sender,

                              @NotNull PageFilter pageFilter) {

        new ContentDisplay.Builder<Collection<String>>()
                .sender(sender)
                .header("%s====[ Multiverse World List ]====", ChatColor.GOLD)
                .contents(getListContents(sender))
                .displayHandler(DisplayHandlers.PAGE_LIST)
                .colorTool(ColorAlternator.with(ChatColor.AQUA, ChatColor.GOLD))
                .filter(ContentFilter.getDefault())
                .setting(DisplaySettings.SHOW_PAGE, pageFilter.getPage())
                .display(this.plugin);
    }

    private List<String> getListContents(@NotNull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player) sender : null;
        List<String> worldList =  new ArrayList<>();

        this.plugin.getMVWorldManager().getMVWorlds().stream()
                .filter(world -> player == null || plugin.getMVPerms().canEnterWorld(player, world))
                .filter(world -> canSeeWorld(player, world))
                .map(world -> hiddenText(world) + world.getColoredWorldString() + " - " + parseColouredEnvironment(world.getEnvironment()))
                .sorted()
                .forEach(worldList::add);

        this.plugin.getMVWorldManager().getUnloadedWorlds().stream()
                .filter(world -> plugin.getMVPerms().hasPermission(sender, "multiverse.access." + world, true))
                .map(world -> ChatColor.GRAY + world + " - UNLOADED")
                .sorted()
                .forEach(worldList::add);

        return worldList;
    }

    private boolean canSeeWorld(Player player, MultiverseWorld world) {
        return !world.isHidden()
                || player == null
                || this.plugin.getMVPerms().hasPermission(player, "multiverse.core.modify", true);
    }

    private String hiddenText(MultiverseWorld world) {
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
