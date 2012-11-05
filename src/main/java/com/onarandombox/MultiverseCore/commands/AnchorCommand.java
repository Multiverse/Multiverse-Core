/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.localization.MultiverseMessage;

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
            this.plugin.getMessaging().sendMessage(sender, MultiverseMessage.CMD_ANCHOR_NOLISTPERM);
            return;
        }

        this.plugin.getMessaging().sendMessage(sender, MultiverseMessage.CMD_ANCHOR_LISTHEADER);
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }


        FilterObject filterObject = this.getPageAndFilter(args);

        List<String> availableAnchors = new ArrayList<String>(this.getFancyAnchorList(p));
        if (filterObject.getFilter().length() > 0) {
            availableAnchors = this.getFilteredItems(availableAnchors, filterObject.getFilter());
            if (availableAnchors.size() == 0) {
                this.plugin.getMessaging().sendMessage(sender, MultiverseMessage.CMD_ANCHOR_NOMATCH, filterObject.getFilter());
                return;
            }
        } else {
            if (availableAnchors.size() == 0) {
                this.plugin.getMessaging().sendMessage(sender, MultiverseMessage.CMD_ANCHOR_NODEF);
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

        this.plugin.getMessaging().sendMessage(sender, MultiverseMessage.CMD_ANCHOR_PAGEHEADER, filterObject.getPage(), totalPages);

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
            if (!this.plugin.getMVPerms().hasPermission(sender, "multiverse.core.anchor.delete", true))
                this.plugin.getMessaging().sendMessage(sender, MultiverseMessage.CMD_ANCHOR_NODELPERM);
            else
                this.plugin.getMessaging().sendMessage(sender, this.plugin.getAnchorManager().deleteAnchor(args.get(0))
                        ? MultiverseMessage.CMD_ANCHOR_DELSUCCESS : MultiverseMessage.CMD_ANCHOR_DELFAIL, args.get(0));
            return;
        }

        if (!(sender instanceof Player)) {
            this.plugin.getMessaging().sendMessage(sender, MultiverseMessage.CMD_ANCHOR_CONSOLECREATE);
            return;
        }

        if (!this.plugin.getMVPerms().hasPermission(sender, "multiverse.core.anchor.create", true))
            this.plugin.getMessaging().sendMessage(sender, MultiverseMessage.CMD_ANCHOR_NOCREATEPERM);
        else
            this.plugin.getMessaging().sendMessage(sender, this.plugin.getAnchorManager().saveAnchorLocation(args.get(0), ((Player) sender).getLocation())
                    ? MultiverseMessage.CMD_ANCHOR_CREATESUCCESS : MultiverseMessage.CMD_ANCHOR_CREATEFAIL, args.get(0));
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
