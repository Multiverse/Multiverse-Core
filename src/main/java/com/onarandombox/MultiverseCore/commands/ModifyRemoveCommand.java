package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class ModifyRemoveCommand extends MultiverseCommand {

    public ModifyRemoveCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Modify a World (Remove a value)");
        this.setCommandUsage("/mvmodify" + ChatColor.GREEN + "REMOVE {PROPERTY} {VALUE}" + ChatColor.GOLD + " [WORLD]");
        this.setArgRange(2, 3);
        this.addKey("mvm remove");
        this.addKey("mvmremove");
        this.addKey("mv modify remove");
        this.addKey("mvmodify remove");
        this.setPermission("multiverse.core.modify.remove", "Modify various aspects of worlds. See the help wiki for how to use this command properly. If you do not include a world, the current world will be used.", PermissionDefault.OP);
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

        if (!ModifyCommand.validateAction(Action.Remove, property)) {
            sender.sendMessage("Sorry, you can't REMOVE anything from" + property);
            sender.sendMessage("Please visit our Github Wiki for more information: http://goo.gl/4W8cY");
            return;
        }
        if (world.removeFromList(property, value)) {
            sender.sendMessage(value + " was removed from " + property);
        } else {
            sender.sendMessage(value + " could not be removed from " + property);
        }
    }

}
