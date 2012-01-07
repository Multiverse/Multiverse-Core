/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.localization.MessageProvider;
import com.onarandombox.MultiverseCore.localization.MultiverseMessage;
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

    private MessageProvider provider;

    public ListCommand(MultiverseCore plugin) {
        super(plugin);
        provider = plugin.getMessageProvider();
        this.setName(provider.getMessage(MultiverseMessage.LIST_NAME));
        this.setCommandUsage("/mv list");
        this.setArgRange(0, 2);
        this.addKey("mvlist");
        this.addKey("mvl");
        this.addKey("mv list");
        this.setPermission("multiverse.core.list.worlds", provider.getMessage(MultiverseMessage.LIST_DESC), PermissionDefault.OP);
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
            String outputCache = world.getColoredWorldString() + ChatColor.WHITE + " - " + color + world.getEnvironment();
            if (world.isHidden()) {
                if (p == null || p.hasPermission("multiverse.core.modify")) {
                    // Prefix hidden worlds with an "[H]"
                    worldList.add(ChatColor.GRAY + "[H]" + outputCache);
                }
            } else {
                worldList.add(outputCache);
            }
        }
        for (String name : this.plugin.getMVWorldManager().getUnloadedWorlds()) {
            if (p == null || this.plugin.getMVPerms().hasPermission(p, "multiverse.access." + name, true)) {
                worldList.add(ChatColor.GRAY + name + " - " + provider.getMessage(MultiverseMessage.GENERIC_UNLOADED));
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
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "====[ " + this.provider.getMessage(MultiverseMessage.LIST_TITLE) + " ]====");
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }


        FilterObject filterObject = this.getPageAndFilter(args);

        List<String> availableWorlds = new ArrayList<String>(this.getFancyWorldList(p));
        if (filterObject.getFilter().length() > 0) {
            availableWorlds = this.getFilteredItems(availableWorlds, filterObject.getFilter());
            if (availableWorlds.size() == 0) {
                sender.sendMessage(ChatColor.RED + provider.getMessage(MultiverseMessage.GENERIC_SORRY) + " " + ChatColor.WHITE + provider.getMessage(MultiverseMessage.LIST_NO_MATCH) + " " + ChatColor.AQUA + filterObject.getFilter());
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

        sender.sendMessage(ChatColor.AQUA + " " + provider.getMessage(MultiverseMessage.GENERIC_PAGE) + " " + filterObject.getPage() + " " + provider.getMessage(MultiverseMessage.GENERIC_OF) + " " + totalPages);

        this.showPage(filterObject.getPage(), sender, availableWorlds);
    }
}
