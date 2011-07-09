/**
 * Copyright (C) 2011 DThielke <dave.thielke@gmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

package com.onarandombox.MultiverseCore.command;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class CommandManager {

    protected List<BaseCommand> commands;
    private MultiverseCore plugin;

    // List to hold commands that require approval
    public List<QueuedCommand> queuedCommands = new ArrayList<QueuedCommand>();

    public CommandManager(MultiverseCore plugin) {
        this.commands = new ArrayList<BaseCommand>();
        this.plugin = plugin;
    }

    public boolean dispatch(CommandSender sender, ArrayList<String> allArgs) {
        ArrayList<String> parsedArgs = parseAllQuotedStrings(allArgs);
        String identifier = null;

        Iterator<BaseCommand> iterator = this.commands.iterator();
        BaseCommand foundCommand = null;
        // This loop is pretty neat. It goes until either the iterator has no more
        // Or until we find an identifier. When we do, we no longer iterate, and the
        // value left in "foundCommand" is the command we found!
        while (iterator.hasNext() && identifier == null) {
            foundCommand = iterator.next();
            identifier = foundCommand.getIdentifier(parsedArgs);
            if (identifier != null) {
                // This method, removeIdentifierArgs mutates parsedArgs
                foundCommand.removeIdentifierArgs(parsedArgs, identifier);
                validateAndRunCommand(sender, parsedArgs, foundCommand);
            }
        }
        return true;
    }

    private void validateAndRunCommand(CommandSender sender, ArrayList<String> parsedArgs, BaseCommand foundCommand) {
        if (foundCommand.validate(parsedArgs)) {
            if (this.plugin.ph.hasPermission(sender, foundCommand.getPermission(), foundCommand.isOpRequired())) {
                foundCommand.execute(sender, parsedArgs.toArray(new String[parsedArgs.size()]));
            } else {
                sender.sendMessage("You do not have permission to use this command. (" + foundCommand.getPermission() + ")");
            }
        } else {
            sender.sendMessage(ChatColor.AQUA + "Command: " + ChatColor.WHITE + foundCommand.getName());
            sender.sendMessage(ChatColor.AQUA + "Description: " + ChatColor.WHITE + foundCommand.getDescription());
            sender.sendMessage(ChatColor.AQUA + "Usage: " + ChatColor.WHITE + foundCommand.getUsage());
        }
    }

    public void addCommand(BaseCommand command) {
        this.commands.add(command);
    }

    public void removeCommand(BaseCommand command) {
        this.commands.remove(command);
    }

    @Deprecated
    public List<BaseCommand> getCommands() {
        return this.commands;
    }

    public List<BaseCommand> getCommands(CommandSender sender) {
        ArrayList<BaseCommand> playerCommands = new ArrayList<BaseCommand>();
        for (BaseCommand c : this.commands) {
            if (this.plugin.ph.hasPermission(sender, c.permission, c.isOpRequired())) {
                playerCommands.add(c);
            }
        }
        return playerCommands;
    }

    /**
     * Combines all quoted strings
     * 
     * @param args
     * @return
     */
    private ArrayList<String> parseAllQuotedStrings(ArrayList<String> args) {
        // TODO: Allow '
        ArrayList<String> newArgs = new ArrayList<String>();
        // Iterate through all command params:
        // we could have: "Fish dog" the man bear pig "lives today" and maybe "even tomorrow" or "the" next day
        int start = -1;
        for (int i = 0; i < args.size(); i++) {

            // If we aren't looking for an end quote, and the first part of a string is a quote
            if (start == -1 && args.get(i).substring(0, 1).equals("\"")) {
                start = i;
            }
            // Have to keep this seperate for one word quoted strings like: "fish"
            if (start != -1 && args.get(i).substring(args.get(i).length() - 1, args.get(i).length()).equals("\"")) {
                // Now we've found the second part of a string, let's parse the quoted one out
                // Make sure it's i+1, we still want I included
                newArgs.add(parseQuotedString(args, start, i + 1));
                // Reset the start to look for more!
                start = -1;
            } else if (start == -1) {
                // This is a word that is NOT enclosed in any quotes, so just add it
                newArgs.add(args.get(i));
            }
        }
        // If the string was ended but had an open quote...
        if (start != -1) {
            // ... then we want to close that quote and make that one arg.
            newArgs.add(parseQuotedString(args, start, args.size()));
        }

        return newArgs;
    }

    /**
     * Takes a string array and returns a combined string, excluding the stop position, including the start
     * 
     * @param args
     * @param start
     * @param stop
     * @return
     */
    private String parseQuotedString(ArrayList<String> args, int start, int stop) {
        String returnVal = args.get(start);
        for (int i = start + 1; i < stop; i++) {
            returnVal += " " + args.get(i);
        }
        return returnVal.replace("\"", "");
    }

    /**
     * Returns the given flag value
     * 
     * @param flag A param flag, like -s or -g
     * @param args All arguments to search through
     * @return A string or null
     */
    public static String getFlag(String flag, String[] args) {
        int i = 0;
        try {
            for (String s : args) {
                if (s.equalsIgnoreCase(flag)) {
                    return args[i + 1];
                }
                i++;
            }
        } catch (IndexOutOfBoundsException e) {
        }
        return null;
    }

    /**
     * 
     */
    public void queueCommand(CommandSender sender, String commandName, String methodName, String[] args, Class<?>[] paramTypes, String success, String fail) {
        cancelQueuedCommand(sender);
        this.queuedCommands.add(new QueuedCommand(methodName, args, paramTypes, sender, Calendar.getInstance(), this.plugin, success, fail));
        sender.sendMessage("The command " + ChatColor.RED + commandName + ChatColor.WHITE + " has been halted due to the fact that it could break something!");
        sender.sendMessage("If you still wish to execute " + ChatColor.RED + commandName + ChatColor.WHITE);
        sender.sendMessage("please type: " + ChatColor.GREEN + "/mvconfirm");
        sender.sendMessage(ChatColor.GREEN + "/mvconfirm" + ChatColor.WHITE + " will only be available for 10 seconds.");
    }

    /**
     * Tries to fire off the command
     * 
     * @param sender
     * @return
     */
    public boolean confirmQueuedCommand(CommandSender sender) {
        for (QueuedCommand com : this.queuedCommands) {
            if (com.getSender().equals(sender)) {
                if (com.execute()) {
                    sender.sendMessage(com.getSuccess());
                    return true;
                } else {
                    sender.sendMessage(com.getFail());
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Cancels(invalidates) a command that has been requested. This is called when a user types something other than 'yes' or when they try to queue a second command Queuing a second command will delete the first command entirely.
     * 
     * @param sender
     */
    public void cancelQueuedCommand(CommandSender sender) {
        QueuedCommand c = null;
        for (QueuedCommand com : this.queuedCommands) {
            if (com.getSender().equals(sender)) {
                c = com;
            }
        }
        if (c != null) {
            // Each person is allowed at most one queued command.
            this.queuedCommands.remove(c);
        }
    }
}
