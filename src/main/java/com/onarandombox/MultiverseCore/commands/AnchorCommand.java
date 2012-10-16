/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows management of Anchor Destinations.
 */
public class AnchorCommand extends PaginatedCoreCommand<String> {

    public AnchorCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Create, Delete and Manage Anchor Destinations.");
        this.setCommandUsage("/mv anchor " + ChatColor.GREEN + "{name}" + ChatColor.GOLD + " [-d]");
        this.setArgRange(0, 2);
        this.addKey("mv anchor");
        this.addKey("mv anchors");
        this.addKey("mvanchor");
        this.addKey("mvanchors");
        this.addCommandExample("/mv anchor " + ChatColor.GREEN + "awesomething");
        this.addCommandExample("/mv anchor " + ChatColor.GREEN + "otherthing");
        this.addCommandExample("/mv anchor " + ChatColor.GREEN + "awesomething " + ChatColor.RED + "-d");
        this.addCommandExample("/mv anchors ");
        this.setPermission("multiverse.core.anchor.list", "Allows a player to list all anchors.", PermissionDefault.OP);
        this.addAdditonalPermission(new Permission("multiverse.core.anchor.create",
                "Allows a player to create anchors.", PermissionDefault.OP));
        this.addAdditonalPermission(new Permission("multiverse.core.anchor.delete",
                "Allows a player to delete anchors.", PermissionDefault.OP));
        this.setItemsPerPage(8); // SUPPRESS CHECKSTYLE: MagicNumberCheck
    }

    private List<String> getFancyAnchorList(Player p) {
        List<String> anchorList = new ArrayList<String>();
        ChatColor color = ChatColor.GREEN;
        for (String anchor : this.plugin.getAnchorManager().getAnchors(p)) {
            anchorList.add(color + anchor);
            color = (color == ChatColor.GREEN) ? ChatColor.GOLD : ChatColor.GREEN;
        }
        return anchorList;
    }

    private void showList(CommandSender sender, List<String> args) {
        if (!this.plugin.getMVPerms().hasPermission(sender, "multiverse.core.anchor.list", true)) {
            sender.sendMessage(ChatColor.RED + "You don't have the permission to list anchors!");
            return;
        }

        sender.sendMessage(ChatColor.LIGHT_PURPLE + "====[ Multiverse Anchor List ]====");
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }


        FilterObject filterObject = this.getPageAndFilter(args);

        List<String> availableAnchors = new ArrayList<String>(this.getFancyAnchorList(p));
        if (filterObject.getFilter().length() > 0) {
            availableAnchors = this.getFilteredItems(availableAnchors, filterObject.getFilter());
            if (availableAnchors.size() == 0) {
                sender.sendMessage(ChatColor.RED + "Sorry... " + ChatColor.WHITE
                        + "No anchors matched your filter: " + ChatColor.AQUA + filterObject.getFilter());
                return;
            }
        } else {
            if (availableAnchors.size() == 0) {
                sender.sendMessage(ChatColor.RED + "Sorry... " + ChatColor.WHITE + "No anchors were defined.");
                return;
            }
        }


        if (!(sender instanceof Player)) {
            for (String c : availableAnchors) {
                sender.sendMessage(c);
            }
            return;
        }

        int totalPages = (int) Math.ceil(availableAnchors.size() / (this.itemsPerPage + 0.0));

        if (filterObject.getPage() > totalPages) {
            filterObject.setPage(totalPages);
        } else if (filterObject.getPage() < 1) {
            filterObject.setPage(1);
        }

        sender.sendMessage(ChatColor.AQUA + " Page " + filterObject.getPage() + " of " + totalPages);

        this.showPage(filterObject.getPage(), sender, availableAnchors);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (args.size() == 0) {
            this.showList(sender, args);
            return;
        }
        if (args.size() == 1 && (this.getPageAndFilter(args).getPage() != 1 || args.get(0).equals("1"))) {
            this.showList(sender, args);
            return;
        }
        if (args.size() == 2 && args.get(1).equalsIgnoreCase("-d")) {
            if (!this.plugin.getMVPerms().hasPermission(sender, "multiverse.core.anchor.delete", true)) {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to delete anchors!");
            } else {
                if (this.plugin.getAnchorManager().deleteAnchor(args.get(0))) {
                    sender.sendMessage("Anchor '" + args.get(0) + "' was successfully " + ChatColor.RED + "deleted!");
                } else {
                    sender.sendMessage("Anchor '" + args.get(0) + "' was " + ChatColor.RED + " NOT " + ChatColor.WHITE + "deleted!");
                }
            }
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to create Anchors.");
            return;
        }

        if (!this.plugin.getMVPerms().hasPermission(sender, "multiverse.core.anchor.create", true)) {
            sender.sendMessage(ChatColor.RED + "You don't have the permission to create anchors!");
        } else {
            Player player = (Player) sender;
            if (this.plugin.getAnchorManager().saveAnchorLocation(args.get(0), player.getLocation())) {
                sender.sendMessage("Anchor '" + args.get(0) + "' was successfully " + ChatColor.GREEN + "created!");
            } else {
                sender.sendMessage("Anchor '" + args.get(0) + "' was " + ChatColor.RED + " NOT " + ChatColor.WHITE + "created!");
            }
        }
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
}
