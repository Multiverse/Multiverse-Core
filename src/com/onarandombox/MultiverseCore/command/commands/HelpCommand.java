// This file is no longer licensed under that silly CC license. I have blanked it out and will start implementaiton of my own in a few days. For now there is no help.
package com.onarandombox.MultiverseCore.command.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;

public class HelpCommand extends Command {
    private static final int CMDS_PER_PAGE = 7;

    public HelpCommand(JavaPlugin plugin) {
        super(plugin);
        this.commandName = "Get Help with Multiverse";
        this.commandDesc = "Displays a nice help menu";
        this.commandUsage = "/mv " + ChatColor.GOLD + "[PAGE #]";
        this.minimumArgLength = 0;
        this.maximumArgLength = 1;
        this.commandKeys.add("mv");
        this.commandKeys.add("mvhelp");
        this.commandKeys.add("mv help");
        this.permission = "multiverse.help";
        this.opRequired = false;
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        int page = 1;
        if (args.size() == 1) {
            try {
                page = Integer.parseInt(args.get(0));
            } catch (NumberFormatException e) {
            }
        }

        List<Command> availableCommands = new ArrayList<Command> (((MultiverseCore) this.plugin).getCommandHandler().getAllCommands());
        for(Command c : availableCommands) {
            if(!((MultiverseCore) this.plugin).getPermissions().hasPermission(sender, c.getPermission(), c.isOpRequired())) {
                availableCommands.remove(c);
            }
        }
        int totalPages = (int) Math.ceil(availableCommands.size() / ( CMDS_PER_PAGE + 0.0));

        if (page > totalPages) {
            page = totalPages;
        }

        sender.sendMessage(ChatColor.AQUA + "====[ Multiverse Help ]====");
        sender.sendMessage(ChatColor.AQUA + " Page " + page + " of " + totalPages);
        sender.sendMessage(ChatColor.AQUA + " Add a '" + ChatColor.DARK_PURPLE + "?" + ChatColor.AQUA + "' after a command to see more about it.");
        this.showPage(page, sender, availableCommands);

    }

    private void showPage(int page, CommandSender sender, List<Command> cmds) {
        int start = (page - 1) * CMDS_PER_PAGE;
        int end = start + CMDS_PER_PAGE;
        for (int i = start; i < end; i++) {
            // For consistancy, print some extra lines if it's a player:
            if (i < cmds.size()) {
            sender.sendMessage(ChatColor.AQUA + cmds.get(i).getCommandUsage());
            } else if(sender instanceof Player) {
                sender.sendMessage(" ");
            }
        }
    }

}
