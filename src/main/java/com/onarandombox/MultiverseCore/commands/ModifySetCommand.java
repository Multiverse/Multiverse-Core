/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.enums.Action;
import com.onarandombox.MultiverseCore.utils.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

public class ModifySetCommand extends MultiverseCommand {
    private WorldManager worldManager;

    public ModifySetCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Modify a World (Set a value)");
        this.setCommandUsage("/mv modify" + ChatColor.GREEN + " set {PROPERTY} {VALUE}" + ChatColor.GOLD + " [WORLD]");
        this.setArgRange(2, 3);
        this.addKey("mvm set");
        this.addKey("mvmset");
        this.addKey("mv modify set");
        this.addKey("mvmodify set");
        this.setPermission("multiverse.core.modify.set", "Modify various aspects of worlds. See the help wiki for how to use this command properly. If you do not include a world, the current world will be used.", PermissionDefault.OP);
        this.worldManager = this.plugin.getWorldManager();
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // We NEED a world from the command line
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }

        if (args.size() == 2 && p == null) {
            sender.sendMessage("From the command line, WORLD is required.");
            sender.sendMessage(this.getCommandDesc());
            sender.sendMessage(this.getCommandUsage());
            sender.sendMessage("Nothing changed.");
            return;
        }

        MVWorld world;
        String value = args.get(1);
        String property = args.get(0);

        if (args.size() == 2) {
            world = this.worldManager.getMVWorld(p.getWorld().getName());
        } else {
            world = this.worldManager.getMVWorld(args.get(2));
        }

        if (world == null) {
            sender.sendMessage("That world does not exist!");
            return;
        }

        if (!ModifyCommand.validateAction(Action.Set, property)) {
            sender.sendMessage("Sorry, you can't SET " + property);
            sender.sendMessage("Please visit our Github Wiki for more information: http://goo.gl/l54PH");
            return;
        }
        if ((property.equalsIgnoreCase("aliascolor") || property.equalsIgnoreCase("color")) && !world.isValidAliasColor(value)) {
            sender.sendMessage(value + " is not a valid color. Please see our Github Wiki for the complete color list.");
        } else if (world.setVariable(property, value)) {
            sender.sendMessage(ChatColor.GREEN + "Success!" + ChatColor.WHITE + " Property " + ChatColor.AQUA + property + ChatColor.WHITE + " was set to " + ChatColor.GREEN + value);
        } else {
            sender.sendMessage(ChatColor.RED + "There was an error setting " + ChatColor.GRAY + property);
        }
    }
}
