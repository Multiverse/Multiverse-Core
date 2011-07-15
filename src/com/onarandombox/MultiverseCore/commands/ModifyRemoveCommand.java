package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;

public class ModifyRemoveCommand extends Command {

    public ModifyRemoveCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "Modify a World";
        this.commandDesc = "Modify various aspects of worlds. See the help wiki for how to use this command properly. If you do not include a world, the current world will be used";
        this.commandUsage = "/mvmodify" + ChatColor.GREEN + "REMOVE {PROPERTY} {VALUE}" + ChatColor.GOLD + " [WORLD]";
        this.minimumArgLength = 2;
        this.maximumArgLength = 3;
        this.commandKeys.add("mvmodify remove");
        this.commandKeys.add("mv modify remove");
        this.commandKeys.add("mvm remove");
        this.commandKeys.add("mvmremove");
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
            world = ((MultiverseCore) this.plugin).getMVWorld(p.getWorld().getName());
        } else {
            world = ((MultiverseCore) this.plugin).getMVWorld(args.get(2));
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
