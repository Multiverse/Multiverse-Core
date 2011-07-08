package com.onarandombox.MultiverseCore.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;

public abstract class BaseCommand {
    
    protected MultiverseCore plugin;
    protected String name;
    protected String description;
    protected String usage;
    protected String permission = "";
    protected boolean requiresOp;
    protected int minArgs;
    protected int maxArgs;
    protected List<String> identifiers;
    
    public final String IN_GAME_COMMAND_MSG = "This command needs to be used as a Player in game.";
    
    public BaseCommand(MultiverseCore plugin) {
        this.identifiers = new ArrayList<String>();
        this.plugin = plugin;
    }
    
    public abstract void execute(CommandSender sender, String[] args);
    
    public boolean validate(String name, String[] parsedArgs, StringBuilder identifier) {
        String match = this.matchIdentifier(name, parsedArgs);
        if (match != null) {
            identifier = identifier.append(match);
            if (parsedArgs == null) {
                parsedArgs = new String[0];
            }
            int l = parsedArgs.length;
            if (l >= this.minArgs && (this.maxArgs == -1 || l <= this.maxArgs)) {
                return true;
            }
        }
        return false;
    }
    
    public String matchIdentifier(String input, String[] args) {
        
        String argsString = this.getArgsString(args);
        String lower = input.toLowerCase() + argsString;
        int index = -1;
        int n = this.identifiers.size();
        for (int i = 0; i < n; i++) {
            String identifier = this.identifiers.get(i).toLowerCase();
            if (index == -1 && lower.matches(identifier + "(\\s+.*|\\s*)")) {
                index = i;
            }
        }
        
        if (index != -1) {
            return this.identifiers.get(index);
        } else {
            return null;
        }
    }
    
    private String getArgsString(String[] args) {
        String returnString = "";
        for (String s : args) {
            returnString += " " + s;
        }
        return returnString;
    }
    
    public List<String> getIdentifiers() {
        return this.identifiers;
    }
    
    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getUsage() {
        return this.usage;
    }
    
    public boolean isOpRequired() {
        return this.requiresOp;
    }
    
    public String getPermission() {
        return this.permission;
    }
    
}
