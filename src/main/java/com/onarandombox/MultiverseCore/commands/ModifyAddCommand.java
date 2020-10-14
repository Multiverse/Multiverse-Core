/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.enums.Action;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

// This will contain all the properties that support the ADD/REMOVE
// Anything not in here will only support the SET action

/**
 * Used to modify various aspects of worlds.
 */
public class ModifyAddCommand extends MultiverseCommand {

    public ModifyAddCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Modify a World (Add a value)");
        this.setCommandUsage("/mv modify " + ChatColor.GREEN + "add {VALUE} {PROPERTY}" + ChatColor.GOLD + " [WORLD] ");
        this.setArgRange(2, 3);
        this.addKey("mvm add");
        this.addKey("mvmadd");
        this.addKey("mv modify add");
        this.addKey("mvmodify add");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "add " + ChatColor.GREEN + "sheep " + ChatColor.RED + "animals");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "add " + ChatColor.GREEN + "creeper " + ChatColor.RED + "monsters");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "add " + ChatColor.GREEN + "MyWorld " + ChatColor.RED + "worldblacklist");
        this.setPermission("multiverse.core.modify.add", "Modify various aspects of worlds. See the help wiki for how to use this command properly. "
                + "If you do not include a world, the current world will be used.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        MultiverseWorld world = getTargetWorld(sender, args, 2);
        if (world == null) {
            return;
        }
        String property = args.get(0);
        String value = args.get(1);

        if (!ModifyCommand.validateAction(Action.Add, property)) {
            sender.sendMessage("Sorry, you can't ADD to " + property);
            sender.sendMessage("Please visit our Github Wiki for more information: https://goo.gl/OMGwzx");
            return;
        }

        // TODO fix this
        if (world.addToVariable(property, value)) {
            sender.sendMessage(ChatColor.GREEN + "Success! " + ChatColor.AQUA
                    + value + ChatColor.WHITE + " was " + ChatColor.GREEN + "added to " + ChatColor.GREEN + property);
            if (!plugin.saveWorldConfig()) {
                sender.sendMessage(ChatColor.RED + "There was an issue saving worlds.yml!  Your changes will only be temporary!");
            }
        } else {
            sender.sendMessage(value + " could not be added to " + property);
        }
    }
}
