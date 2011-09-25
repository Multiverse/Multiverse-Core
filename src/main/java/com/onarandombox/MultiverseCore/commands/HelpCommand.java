/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

// This file is no longer licensed under that silly CC license. I have blanked it out and will start implementaiton of my own in a few days. For now there is no help.
package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends MultiverseCommand {
    private static final int CMDS_PER_PAGE = 7;

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
        this.setPermission("multiverse.help", "Displays a nice help menu.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        sender.sendMessage(ChatColor.AQUA + "====[ Multiverse Help ]====");

        int page = 1;

        String filter = "";

        if (args.size() == 0) {
            filter = "";
            page = 1;
        } else if (args.size() == 1) {
            try {
                page = Integer.parseInt(args.get(0));
            } catch (NumberFormatException ex) {
                filter = args.get(0);
                page = 1;
            }
        } else if (args.size() == 2) {
            filter = args.get(0);
            try {
                page = Integer.parseInt(args.get(1));
            } catch (NumberFormatException ex) {
                page = 1;
            }
        }

        List<Command> availableCommands = new ArrayList<Command>(this.plugin.getCommandHandler().getCommands(sender));
        if (filter.length() > 0) {
            availableCommands = this.getFilteredCommands(availableCommands, filter);
            if (availableCommands.size() == 0) {
                sender.sendMessage(ChatColor.RED + "Sorry... " + ChatColor.WHITE + "No commands matched your filter: " + ChatColor.AQUA + filter);
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

        int totalPages = (int) Math.ceil(availableCommands.size() / (CMDS_PER_PAGE + 0.0));

        if (page > totalPages) {
            page = totalPages;
        }

        sender.sendMessage(ChatColor.AQUA + " Page " + page + " of " + totalPages);
        sender.sendMessage(ChatColor.AQUA + " Add a '" + ChatColor.DARK_PURPLE + "?" + ChatColor.AQUA + "' after a command to see more about it.");

        this.showPage(page, sender, availableCommands);

    }

    private List<Command> getFilteredCommands(List<Command> availableCommands, String filter) {
        List<Command> filtered = new ArrayList<Command>();

        for (Command c : availableCommands) {
            if (stitchThisString(c.getKeyStrings()).matches("(?i).*" + filter + ".*")) {
                filtered.add(c);
            } else if (c.getCommandName().matches("(?i).*" + filter + ".*")) {
                filtered.add(c);
            } else if (c.getCommandDesc().matches("(?i).*" + filter + ".*")) {
                filtered.add(c);
            } else if (c.getCommandUsage().matches("(?i).*" + filter + ".*")) {
                filtered.add(c);
            }
        }
        return filtered;
    }

    private String stitchThisString(List<String> list) {
        String returnstr = "";
        for (String s : list) {
            returnstr += s + " ";
        }
        return returnstr;
    }

    private void showPage(int page, CommandSender sender, List<Command> cmds) {
        int start = (page - 1) * CMDS_PER_PAGE;
        int end = start + CMDS_PER_PAGE;
        for (int i = start; i < end; i++) {
            // For consistancy, print some extra lines if it's a player:
            if (i < cmds.size()) {
                sender.sendMessage(ChatColor.AQUA + cmds.get(i).getCommandUsage());
            } else if (sender instanceof Player) {
                sender.sendMessage(" ");
            }
        }
    }

}
