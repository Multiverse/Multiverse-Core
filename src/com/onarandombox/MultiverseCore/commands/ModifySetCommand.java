package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;

public class ModifySetCommand extends Command {

    public ModifySetCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "Modify a World (Set a value)";
        this.commandDesc = "Modify various aspects of worlds. See the help wiki for how to use this command properly. If you do not include a world, the current world will be used";
        this.commandUsage = "/mvmodify" + ChatColor.GREEN + " set {PROPERTY} {VALUE}" + ChatColor.GOLD + " [WORLD]";
        this.minimumArgLength = 2;
        this.maximumArgLength = 3;
        this.commandKeys.add("mvmodify set");
        this.commandKeys.add("mv modify set");
        this.commandKeys.add("mvm set");
        this.commandKeys.add("mvmset");
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
            sender.sendMessage("From the command line, WORLD is required.");
            sender.sendMessage(this.commandDesc);
            sender.sendMessage(this.commandUsage);
            sender.sendMessage("Nothing changed.");
            return;
        }

        MVWorld world;
        String value = args.get(1);
        String property = args.get(0);

        if (args.size() == 2) {
            world = ((MultiverseCore) this.plugin).getMVWorld(p.getWorld().getName());
        } else {
            world = ((MultiverseCore) this.plugin).getMVWorld(args.get(2));
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
            sender.sendMessage("Property " + property + " was set to " + value);
        } else {
            sender.sendMessage("There was an error setting " + property);
        }
    }
}
