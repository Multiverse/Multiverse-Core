package com.onarandombox.MultiverseCore.command.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;

enum AddProperties {
    animallist, monsterlist, blockblacklist, playerwhitelist, playerblacklist, editwhitelist, editblacklist, worldblacklist, animals, monsters
}

enum Action {
    Set, Add, Remove, Clear
}

enum SetProperties {
    alias, animals, monsters, pvp, scaling
}

public class ModifyCommand extends Command {

    public ModifyCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "Modify a World";
        this.commandDesc = "MVModify requires an extra parameter: SET,ADD,REMOVE or CLEAR. See below for usage.";
        this.commandUsage = "/mvmodify" + ChatColor.GREEN + " {SET|ADD|REMOVE|CLEAR} ...";
        // Make it so they can NEVER execute this one
        this.minimumArgLength = 1;
        this.maximumArgLength = 0;
        this.commandKeys.add("mvmodify");
        this.permission = "multiverse.world.modify";
        this.opRequired = true;
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

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // This is just a place holder. The real commands are in:
        // ModifyAddCommand
        // ModifyRemoveCommand
        // ModifySetCommand
        // ModifyClearCommand
    }
}
