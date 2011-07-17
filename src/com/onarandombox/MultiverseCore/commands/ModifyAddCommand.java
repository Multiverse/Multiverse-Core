package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;

// This will contain all the properties that support the ADD/REMOVE
// Anything not in here will only support the SET action

public class ModifyAddCommand extends MultiverseCommand {

    public ModifyAddCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "Modify a World (Add a value)";
        this.commandDesc = "Modify various aspects of worlds. See the help wiki for how to use this command properly. If you do not include a world, the current world will be used.";
        this.commandUsage = "/mvmodify " + ChatColor.GREEN + "ADD {VALUE} {PROPERTY}" + ChatColor.GOLD + " [WORLD] ";
        this.minimumArgLength = 2;
        this.maximumArgLength = 3;
        this.commandKeys.add("mvmodify add");
        this.commandKeys.add("mv modify add");
        this.commandKeys.add("mvm add");
        this.commandKeys.add("mvmadd");
        this.permission = "multiverse.world.modify";
        this.opRequired = true;
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
            sender.sendMessage(this.commandDesc);
            sender.sendMessage(this.commandUsage);
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
