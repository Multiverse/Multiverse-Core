/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a nice help menu.
 */
public class HelpCommand extends PaginatedCoreCommand<Command> {

    public HelpCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Get Help with Multiverse");
        this.setCommandUsage("/mv " + ChatColor.GOLD + "[FILTER] [PAGE #]");
        this.setArgRange(0, 2);
        this.addKey("mv");
        this.addKey("mvh");
        this.addKey("mvhelp");
        this.addKey("mv help");
        this.addKey("mvsearch");
        this.addKey("mv search");
        this.addCommandExample("/mv help ?");
        this.setPermission("multiverse.help", "Displays a nice help menu.", PermissionDefault.TRUE);
        this.setItemsPerPage(7); // SUPPRESS CHECKSTYLE: MagicNumberCheck
    }

    @Override
    protected List<Command> getFilteredItems(List<Command> availableItems, String filter) {
        List<Command> filtered = new ArrayList<Command>();

        for (Command c : availableItems) {
            if (stitchThisString(c.getKeyStrings()).matches("(?i).*" + filter + ".*")) {
                filtered.add(c);
            } else if (c.getCommandName().matches("(?i).*" + filter + ".*")) {
                filtered.add(c);
            } else if (c.getCommandDesc().matches("(?i).*" + filter + ".*")) {
                filtered.add(c);
            } else if (c.getCommandUsage().matches("(?i).*" + filter + ".*")) {
                filtered.add(c);
            } else {
                for (String example : c.getCommandExamples()) {
                    if (example.matches("(?i).*" + filter + ".*")) {
                        filtered.add(c);
                        break;
                    }
                }
            }
        }
        return filtered;
    }

    @Override
    protected String getItemText(Command item) {
        return ChatColor.AQUA + item.getCommandUsage();
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        sender.sendMessage(ChatColor.AQUA + "====[ Multiverse Help ]====");

        FilterObject filterObject = this.getPageAndFilter(args);

        List<Command> availableCommands = new ArrayList<Command>(this.plugin.getCommandHandler().getCommands(sender));
        if (filterObject.getFilter().length() > 0) {
            availableCommands = this.getFilteredItems(availableCommands, filterObject.getFilter());
            if (availableCommands.size() == 0) {
                sender.sendMessage(ChatColor.RED + "Sorry... " + ChatColor.WHITE
                        + "No commands matched your filter: " + ChatColor.AQUA + filterObject.getFilter());
                return;
            }
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.AQUA + " Add a '" + ChatColor.DARK_PURPLE + "?" + ChatColor.AQUA + "' after a command to see more about it.");
            for (Command c : availableCommands) {
                sender.sendMessage(ChatColor.AQUA + c.getCommandUsage());
            }
            return;
        }

        int totalPages = (int) Math.ceil(availableCommands.size() / (this.itemsPerPage + 0.0));

        if (filterObject.getPage() > totalPages) {
            filterObject.setPage(totalPages);
        }

        sender.sendMessage(ChatColor.AQUA + " Page " + filterObject.getPage() + " of " + totalPages);
        sender.sendMessage(ChatColor.AQUA + " Add a '" + ChatColor.DARK_PURPLE + "?" + ChatColor.AQUA + "' after a command to see more about it.");

        this.showPage(filterObject.getPage(), sender, availableCommands);
    }
}
