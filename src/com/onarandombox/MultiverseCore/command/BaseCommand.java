package com.onarandombox.MultiverseCore.command;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Deprecated
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

    public boolean validate(ArrayList<String> args) {
        int argsLength = args.size();
        if ((argsLength == -1 || argsLength >= this.minArgs) && (this.maxArgs == -1 || argsLength <= this.maxArgs)) {
            return true;
        }
        return false;
    }

    @Deprecated
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

    public String getIdentifier(ArrayList<String> allArgs) {
        // Combines our args to a space seperated string
        String argsString = this.getArgsString(allArgs);

        for (String s : this.identifiers) {
            String identifier = s.toLowerCase();
            if (argsString.matches(identifier + "(\\s+.*|\\s*)")) {
                return identifier;
            }
        }
        return null;
    }

    public ArrayList<String> removeIdentifierArgs(ArrayList<String> allArgs, String identifier) {
        int identifierLength = identifier.split(" ").length;
        for (int i = 0; i < identifierLength; i++) {
            // Since we're pulling from the front, always remove the first element
            allArgs.remove(0);
        }
        return allArgs;
    }

    protected String[] removeRedundantArgs(String[] args, String command) {
        System.out.print("Attempting to remove redundant args:");
        System.out.print(Arrays.toString(args));
        System.out.print(command);
        String[] cmdSplit = command.split(" ");
        // Start at cmdSplit[1], because 0 is the command name
        int match = 0;
        int i = 0;
        while (i + 1 < cmdSplit.length && i < args.length && cmdSplit[i + 1].equalsIgnoreCase(args[i])) {
            System.out.print("Found a match!");
            match = i + 1;
            i++;
        }
        ArrayList<String> newArgs = new ArrayList<String>();
        for (int j = match; j < args.length; j++) {
            newArgs.add(args[j]);
        }
        String[] mynewArr = {};
        return newArgs.toArray(mynewArr);
    }

    @Deprecated
    private String getArgsString(String[] args) {
        String returnString = "";
        for (String s : args) {
            returnString += " " + s;
        }
        return returnString;
    }

    private String getArgsString(ArrayList<String> args) {
        String returnString = "";
        for (String s : args) {
            returnString += s + " ";
        }
        return returnString.substring(0, returnString.length() - 1);
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
