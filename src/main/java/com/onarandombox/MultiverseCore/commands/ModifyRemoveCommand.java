/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.enums.Action;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * Removes values from a world-property.
 */
public class ModifyRemoveCommand extends MultiverseCommand {
    private MVWorldManager worldManager;

    public ModifyRemoveCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Modify a World (Remove a value)");
        this.setCommandUsage("/mv modify" + ChatColor.GREEN + " remove {PROPERTY} {VALUE}" + ChatColor.GOLD + " [WORLD]");
        this.setArgRange(2, 3);
        this.addKey("mvm remove");
        this.addKey("mvmremove");
        this.addKey("mv modify remove");
        this.addKey("mvmodify remove");
        this.addKey("mvm delete");
        this.addKey("mvmdelete");
        this.addKey("mv modify delete");
        this.addKey("mvmodify delete");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "remove " + ChatColor.GREEN + "sheep " + ChatColor.RED + "animals");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "remove " + ChatColor.GREEN + "creeper " + ChatColor.RED + "monsters");
        this.addCommandExample("/mvm " + ChatColor.GOLD + "remove " + ChatColor.GREEN + "MyWorld " + ChatColor.RED + "worldblacklist");
        this.setPermission("multiverse.core.modify.remove", "Modify various aspects of worlds. See the help wiki for how to use this command properly. "
                + "If you do not include a world, the current world will be used.", PermissionDefault.OP);
        this.worldManager = this.plugin.getMVWorldManager();
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // We NEED a world from the command line
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }

        if (args.size() == 2 && p == null) {
            sender.sendMessage(ChatColor.RED + "From the console, WORLD is required.");
            sender.sendMessage(this.getCommandDesc());
            sender.sendMessage(this.getCommandUsage());
            sender.sendMessage("Nothing changed.");
            return;
        }

        MultiverseWorld world;
        String value = args.get(0);
        String property = args.get(1);

        if (args.size() == 2) {
            world = this.worldManager.getMVWorld(p.getWorld().getName());
        } else {
            world = this.worldManager.getMVWorld(args.get(2));
        }

        if (world == null) {
            sender.sendMessage("That world does not exist!");
            return;
        }

        if (!ModifyCommand.validateAction(Action.Remove, property)) {
            sender.sendMessage("Sorry, you can't REMOVE anything from" + property);
            sender.sendMessage("Please visit our Github Wiki for more information: https://goo.gl/OMGwzx");
            return;
        }
        // TODO fix this
        if (world.removeFromVariable(property, value)) {
            sender.sendMessage(ChatColor.GREEN + "Success! " + ChatColor.AQUA + value + ChatColor.WHITE
                    + " was " + ChatColor.RED + "removed from " + ChatColor.GREEN + property);
            if (!plugin.saveWorldConfig()) {
                sender.sendMessage(ChatColor.RED + "There was an issue saving worlds.yml!  Your changes will only be temporary!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "There was an error removing " + ChatColor.GRAY
                    + value + ChatColor.WHITE + " from " + ChatColor.GOLD + property);
        }
    }

}
