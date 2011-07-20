package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;

// This will contain all the properties that support the ADD/REMOVE
// Anything not in here will only support the SET action

public class ModifyAddCommand extends MultiverseCommand {

    public ModifyAddCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Modify a World (Add a value)");
        this.setCommandUsage("/mvmodify " + ChatColor.GREEN + "ADD {VALUE} {PROPERTY}" + ChatColor.GOLD + " [WORLD] ");
        this.setArgRange(2, 3);
        this.addKey("mvm add");
        this.addKey("mvmadd");
        this.addKey("mv modify add");
        this.addKey("mvmodify add");
        this.setPermission("multiverse.core.modify.add", "Modify various aspects of worlds. See the help wiki for how to use this command properly. If you do not include a world, the current world will be used.", PermissionDefault.OP);
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

        MVWorld world;
        String value = args.get(0);
        String property = args.get(1);

        if (args.size() == 2) {
            world = this.plugin.getMVWorld(p.getWorld().getName());
        } else {
            world = this.plugin.getMVWorld(args.get(2));
        }

        if (world == null) {
            sender.sendMessage("That world does not exist!");
            return;
        }

        if (!ModifyCommand.validateAction(Action.Add, property)) {
            sender.sendMessage("Sorry, you can't ADD to " + property);
            sender.sendMessage("Please visit our Github Wiki for more information: http://goo.gl/4W8cY");
            return;
        }

        if (world.addToList(property, value)) {
            sender.sendMessage(value + " was added to " + property);
        } else {
            sender.sendMessage(value + " could not be added to " + property);
        }
    }
}
