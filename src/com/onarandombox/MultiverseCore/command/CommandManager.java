/**
 * Copyright (C) 2011 DThielke <dave.thielke@gmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

package com.onarandombox.MultiverseCore.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandManager {
    
    protected List<BaseCommand> commands;
    private CommandSender sender;
    
    public CommandManager() {
        commands = new ArrayList<BaseCommand>();
    }
    
    public boolean dispatch(CommandSender sender, Command command, String label, String[] args) {
        this.sender = sender;
        
        BaseCommand match = null;
        String[] trimmedArgs = null;
        StringBuilder identifier = new StringBuilder();
        
        for (BaseCommand cmd : commands) {
            StringBuilder tmpIdentifier = new StringBuilder();
            String[] tmpArgs = parseAllQuotedStrings(args);
            if(cmd.validate(label, tmpArgs, tmpIdentifier) && tmpIdentifier.length() > identifier.length()) {
                identifier = tmpIdentifier;
                match = cmd;
                trimmedArgs = tmpArgs;
            }
        }
        
        if (match != null) {
            if (trimmedArgs != null) {
                match.execute(sender, trimmedArgs);
                return true;
            } else {
                sender.sendMessage(ChatColor.AQUA + "Command: " + ChatColor.WHITE + match.getName());
                sender.sendMessage(ChatColor.AQUA + "Description: " + ChatColor.WHITE + match.getDescription());
                sender.sendMessage(ChatColor.AQUA + "Usage: " + ChatColor.WHITE + match.getUsage());
            }
        }
        return true;
    }
    
    public void addCommand(BaseCommand command) {
        commands.add(command);
    }
    
    public void removeCommand(BaseCommand command) {
        commands.remove(command);
    }
    
    public List<BaseCommand> getCommands() {
        return commands;
    }
    /**
     * Combines all quoted strings
     * @param args
     * @return
     */
    private String[] parseAllQuotedStrings(String[] args) {
        ArrayList<String> newArgs = new ArrayList<String>();
        // Iterate through all command params:
        // we could have: "Fish dog" the man bear pig "lives today" and maybe "even tomorrow" or "the" next day
        int start = -1;
        for (int i = 0; i < args.length; i++) {
            
            // If we aren't looking for an end quote, and the first part of a string is a quote
            if (start == -1 && args[i].substring(0, 1).equals("\"")) {
                start = i;
            }
            // Have to keep this seperate for one word quoted strings like: "fish"
            if (start != -1 && args[i].substring(args[i].length() - 1, args[i].length()).equals("\"")) {
                // Now we've found the second part of a string, let's parse the quoted one out
                // Make sure it's i+1, we still want I included
                newArgs.add(parseQuotedString(args, start, i + 1));
                // Reset the start to look for more!
                start = -1;
            } else if (start == -1) {
                // This is a word that is NOT enclosed in any quotes, so just add it
                newArgs.add(args[i]);
            }
        }
        // If the string was ended but had an open quote...
        if(start != -1) {
            newArgs.add(parseQuotedString(args, start, args.length));
        }
        String[] results = newArgs.toArray(new String[newArgs.size()]);
        String msg = "Here's what I have boss: [";
        if (results != null) {
            for (String s : results) {
                msg += s + ", ";
            }
            msg += "]";
            this.sender.sendMessage(msg);
        }
        
        return results;
    }
    
    /**
     * Takes a string array and returns a combined string, excluding the stop position, including the start
     * 
     * @param args
     * @param start
     * @param stop
     * @return
     */
    private String parseQuotedString(String[] args, int start, int stop) {
        String returnVal = args[start];
        for (int i = start + 1; i < stop; i++) {
            returnVal += " " + args[i];
        }
        return returnVal.replace("\"", "");
    }
}
