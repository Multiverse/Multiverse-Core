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

    public boolean validate(ArrayList<String> args) {
        int argsLength = args.size();
        if ((argsLength == -1 || argsLength >= this.minArgs) && (this.maxArgs == -1 || argsLength <= this.maxArgs)) {
            return true;
        }
        return false;
    }

    public String getIdentifier(ArrayList<String> allArgs) {
        // Combines our args to a space separated string
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
