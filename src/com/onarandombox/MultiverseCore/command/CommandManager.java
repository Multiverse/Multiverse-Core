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
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class CommandManager {
    
    protected List<BaseCommand> commands;
    
    public CommandManager() {
        this.commands = new ArrayList<BaseCommand>();
    }
    
    public boolean dispatch(CommandSender sender, Command command, String label, String[] args) {
        
        BaseCommand match = null;
        String[] trimmedArgs = null;
        StringBuilder identifier = new StringBuilder();
        
        for (BaseCommand cmd : this.commands) {
            StringBuilder tmpIdentifier = new StringBuilder();
            String[] tmpArgs = parseAllQuotedStrings(args);
            if (match == null) {
                match = cmd.matchIdentifier(label) == null ? null : cmd;
            }
            
            if (match != null && cmd.validate(label, tmpArgs, tmpIdentifier) && tmpIdentifier.length() > identifier.length()) {
                identifier = tmpIdentifier;
                trimmedArgs = tmpArgs;
            }
        }
        
        if (match != null) {
            if (this.hasPermission(sender, match.getPermission(), match.isOpRequired())) {
                if (trimmedArgs != null) {
                    match.execute(sender, trimmedArgs);
                } else {
                    sender.sendMessage(ChatColor.AQUA + "Command: " + ChatColor.WHITE + match.getName());
                    sender.sendMessage(ChatColor.AQUA + "Description: " + ChatColor.WHITE + match.getDescription());
                    sender.sendMessage(ChatColor.AQUA + "Usage: " + ChatColor.WHITE + match.getUsage());
                }
            } else {
                sender.sendMessage("You do not have permission to use this command. (" + match.getPermission() + ")");
            }
        }
        return true;
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
        for(BaseCommand c : this.commands) {
            if(this.hasPermission(sender, c.permission, c.isOpRequired())) {
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
    private String[] parseAllQuotedStrings(String[] args) {
        // TODO: Allow '
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
        if (start != -1) {
            // ... then we want to close that quote and make that one arg.
            newArgs.add(parseQuotedString(args, start, args.length));
        }
        
        return newArgs.toArray(new String[newArgs.size()]);
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
    
    public boolean hasPermission(CommandSender sender, String node, boolean isOpRequired) {
        
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        
        if (player.isOp()) {
            // If Player is Op we always let them use it.
            return true;
        } else if (MultiverseCore.Permissions != null && MultiverseCore.Permissions.has(player, node)) {
            // If Permissions is enabled we check against them.
            return true;
        }
        // If the Player doesn't have Permissions and isn't an Op then
        // we return true if OP is not required, otherwise we return false
        return !isOpRequired;
    }
}
