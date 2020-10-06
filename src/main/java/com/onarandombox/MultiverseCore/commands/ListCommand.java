/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a listing of all worlds that a player can enter.
 */
public class ListCommand extends PaginatedCoreCommand<String> {

    public ListCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("World Listing");
        this.setCommandUsage("/mv list [page]");
        this.setArgRange(0, 2);
        this.addKey("mvlist");
        this.addKey("mvl");
        this.addKey("mv list");
        this.setPermission("multiverse.core.list.worlds", "Displays a listing of all worlds that you can enter.", PermissionDefault.OP);
        this.setItemsPerPage(8); // SUPPRESS CHECKSTYLE: MagicNumberCheck
    }

    private List<String> getFancyWorldList(Player p) {
        List<String> worldList = new ArrayList<String>();
        for (MultiverseWorld world : this.plugin.getMVWorldManager().getMVWorlds()) {

            if (p != null && (!this.plugin.getMVPerms().canEnterWorld(p, world))) {
                continue;
            }

            ChatColor color = ChatColor.GOLD;
            Environment env = world.getEnvironment();
            if (env == Environment.NETHER) {
                color = ChatColor.RED;
            } else if (env == Environment.NORMAL) {
                color = ChatColor.GREEN;
            } else if (env == Environment.THE_END) {
                color = ChatColor.AQUA;
            }
            StringBuilder builder = new StringBuilder();
            builder.append(world.getColoredWorldString()).append(ChatColor.WHITE);
            builder.append(" - ").append(color).append(world.getEnvironment());
            if (world.isHidden()) {
                if (p == null || this.plugin.getMVPerms().hasPermission(p, "multiverse.core.modify", true)) {
                    // Prefix hidden worlds with an "[H]"
                    worldList.add(ChatColor.GRAY + "[H]" + builder.toString());
                }
            } else {
                worldList.add(builder.toString());
            }
        }
        for (String name : this.plugin.getMVWorldManager().getUnloadedWorlds()) {
            if (p == null || this.plugin.getMVPerms().hasPermission(p, "multiverse.access." + name, true)) {
                worldList.add(ChatColor.GRAY + name + " - UNLOADED");
            }
        }
        return worldList;
    }

    @Override
    protected List<String> getFilteredItems(List<String> availableItems, String filter) {
        List<String> filtered = new ArrayList<String>();

        for (String s : availableItems) {
            if (s.matches("(?i).*" + filter + ".*")) {
                filtered.add(s);
            }
        }
        return filtered;
    }

    @Override
    protected String getItemText(String item) {
        return item;
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "====[ Multiverse World List ]====");
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }


        FilterObject filterObject = this.getPageAndFilter(args);

        List<String> availableWorlds = new ArrayList<String>(this.getFancyWorldList(p));
        if (filterObject.getFilter().length() > 0) {
            availableWorlds = this.getFilteredItems(availableWorlds, filterObject.getFilter());
            if (availableWorlds.size() == 0) {
                sender.sendMessage(ChatColor.RED + "Sorry... " + ChatColor.WHITE
                        + "No worlds matched your filter: " + ChatColor.AQUA + filterObject.getFilter());
                return;
            }
        }

        if (!(sender instanceof Player)) {
            for (String c : availableWorlds) {
                sender.sendMessage(c);
            }
            return;
        }

        int totalPages = (int) Math.ceil(availableWorlds.size() / (this.itemsPerPage + 0.0));

        if (filterObject.getPage() > totalPages) {
            filterObject.setPage(totalPages);
        }

        sender.sendMessage(ChatColor.AQUA + " Page " + filterObject.getPage() + " of " + totalPages);

        this.showPage(filterObject.getPage(), sender, availableWorlds);
    }
}
