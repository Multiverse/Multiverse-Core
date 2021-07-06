/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.displaytools.ColorAlternator;
import com.onarandombox.MultiverseCore.displaytools.ContentDisplay;
import com.onarandombox.MultiverseCore.displaytools.ContentFilter;
import com.onarandombox.MultiverseCore.displaytools.DisplayHandlers;
import com.onarandombox.MultiverseCore.displaytools.DisplaySettings;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Displays a listing of all worlds that a player can enter.
 */
public class ListCommand extends MultiverseCommand {

    public ListCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("World Listing");
        this.setCommandUsage("/mv list [filter] [page]");
        this.setArgRange(0, 2);
        this.addKey("mvlist");
        this.addKey("mvl");
        this.addKey("mv list");
        this.setPermission("multiverse.core.list.worlds", "Displays a listing of all worlds that you can enter.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        ContentFilter filter = ContentFilter.DEFAULT;
        int page = 1;

        // Either page or filter.
        if (args.size() == 1) {
            try {
                page = Integer.parseInt(args.get(0));
            } catch (NumberFormatException ignore) {
                filter = new ContentFilter(args.get(0));
            }
        }

        // Filter then page.
        if (args.size() == 2) {
            filter = new ContentFilter(args.get(0));
            try {
                page = Integer.parseInt(args.get(1));
            } catch (NumberFormatException ignore) {
                sender.sendMessage(ChatColor.RED + args.get(1) + " is not valid number!");
            }
        }

        ContentDisplay.forContent(getListContents(sender))
                .header("%s====[ Multiverse World List ]====", ChatColor.GOLD)
                .displayHandler(DisplayHandlers.PAGE_LIST)
                .colorTool(ColorAlternator.with(ChatColor.AQUA, ChatColor.GOLD))
                .filter(filter)
                .setting(DisplaySettings.SHOW_PAGE, page)
                .show(sender);
    }

    private Collection<String> getListContents(@NotNull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player) sender : null;

        List<String> worldList = this.plugin.getMVWorldManager().getMVWorlds().stream()
                .filter(world -> player == null || plugin.getMVPerms().canEnterWorld(player, world))
                .filter(world -> canSeeWorld(player, world))
                .map(world -> hiddenText(world) + world.getColoredWorldString() + " - " + parseColouredEnvironment(world.getEnvironment()))
                .collect(Collectors.toList());

        this.plugin.getMVWorldManager().getUnloadedWorlds().stream()
                .filter(world -> plugin.getMVPerms().hasPermission(sender, "multiverse.access." + world, true))
                .map(world -> ChatColor.GRAY + world + " - UNLOADED")
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
