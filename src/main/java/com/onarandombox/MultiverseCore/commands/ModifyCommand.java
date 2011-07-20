package com.onarandombox.MultiverseCore.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MultiverseCore;

enum AddProperties {
    blockblacklist, playerwhitelist, playerblacklist, worldblacklist, animals, monsters
}

enum Action {
    Set, Add, Remove, Clear
}

// Color == Aliascolor
enum SetProperties {
    alias, animals, monsters, pvp, scaling, aliascolor, color, respawn
}

public class ModifyCommand extends MultiverseCommand {

    public ModifyCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Modify a World");
        this.setCommandUsage("/mvmodify" + ChatColor.GREEN + " {set|add|remove|clear} ...");
        this.setArgRange(2, 3);
        this.addKey("mvm");
        this.addKey("mvmodify");
        Map<String, Boolean> children = new HashMap<String, Boolean>();
        children.put("multiverse.core.modify.add", true);
        children.put("multiverse.core.modify.modify", true);
        children.put("multiverse.core.modify.clear", true);
        children.put("multiverse.core.modify.remove", true);
        Permission modify = new Permission("multiverse.core.modify", "Modify various aspects of worlds. See the help wiki for how to use this command properly. If you do not include a world, the current world will be used.", PermissionDefault.OP, children);
        this.setPermission(modify);
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
