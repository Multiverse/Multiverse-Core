package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

enum AddProperties {
    animallist, monsterlist, blockblacklist, playerwhitelist, playerblacklist, editwhitelist, editblacklist, worldblacklist, animals, monsters
}

enum Action {
    Set, Add, Remove, Clear
}

enum SetProperties {
    alias, animals, monsters, pvp, scaling
}

public class ModifyCommand extends BaseCommand {
    
    public ModifyCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Modify a World";
        this.description = "MVModify requires an extra parameter: SET,ADD,REMOVE or CLEAR. See below for usage.";
        this.usage = "/mvmodify" + ChatColor.GREEN + " {SET|ADD|REMOVE|CLEAR} ...";
        this.minArgs = 0;
        this.maxArgs = 0;
        this.identifiers.add("mvmodify");
        this.permission = "multiverse.world.modify";
        this.requiresOp = true;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(this.name);
        sender.sendMessage(this.description);
        sender.sendMessage(this.usage);
        // This is just a place holder. The real commands are in:
        // ModifyAddCommand
        // ModifyRemoveCommand
        // ModifySetCommand
        // ModifyClearCommand
    }
    protected static boolean validateAction(Action action, String property) {
        if (action == Action.Set) {
            try {
                SetProperties.valueOf(property);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else {
            try {
                AddProperties.valueOf(property);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        
    }
}
