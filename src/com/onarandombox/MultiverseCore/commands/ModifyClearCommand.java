package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;

public class ModifyClearCommand extends Command {

    public ModifyClearCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "Modify a World (Clear a property)";
        this.commandDesc = "Removes all values from a property. This will work on properties that contain lists";
        this.commandUsage = "/mvmodify" + ChatColor.GREEN + " CLEAR {PROPERTY}" + ChatColor.GOLD + " [WORLD] ";
        this.minimumArgLength = 1;
        this.maximumArgLength = 2;
        this.commandKeys.add("mvmodify clear");
        this.commandKeys.add("mv modify clear");
        this.commandKeys.add("mvm clear");
        this.commandKeys.add("mvmclear");
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
        if (args.size() == 1 && p == null) {
            sender.sendMessage(ChatColor.RED + "From the console, WORLD is required.");
            sender.sendMessage(this.commandDesc);
            sender.sendMessage(this.commandUsage);
            sender.sendMessage("Nothing changed.");
            return;
        }

        MVWorld world;
        String property = args.get(0);

        if (args.size() == 1) {
            world = ((MultiverseCore) this.plugin).getMVWorld(p.getWorld().getName());
        } else {
            world = ((MultiverseCore) this.plugin).getMVWorld(args.get(1));
        }

        if (world == null) {
            sender.sendMessage("That world does not exist!");
            return;
        }

        if (!ModifyCommand.validateAction(Action.Clear, property)) {
            sender.sendMessage("Sorry, you can't use CLEAR with " + property);
            sender.sendMessage("Please visit our Github Wiki for more information: http://goo.gl/cgB2B");
            return;
        }
        if (world.clearList(property)) {
            sender.sendMessage(property + " was cleared. It contains 0 values now.");
        } else {
            sender.sendMessage(property + " was NOT cleared.");
        }
    }

}
