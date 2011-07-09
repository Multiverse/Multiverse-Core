package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class ModifyClearCommand extends BaseCommand {

    public ModifyClearCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Modify a World (Clear a property)";
        this.description = "Removes all values from a property. This will work on properties that contain lists";
        this.usage = "/mvmodify" + ChatColor.GREEN + " CLEAR {PROPERTY}" + ChatColor.GOLD + " [WORLD] ";
        this.minArgs = 1;
        this.maxArgs = 2;
        this.identifiers.add("mvmodify clear");
        this.identifiers.add("mvmclear");
        this.identifiers.add("mvmc");
        this.permission = "multiverse.world.modify";
        this.requiresOp = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // We NEED a world from the command line
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }
        if (args.length == 1 && p == null) {
            sender.sendMessage(ChatColor.RED + "From the console, WORLD is required.");
            sender.sendMessage(this.description);
            sender.sendMessage(this.usage);
            sender.sendMessage("Nothing changed.");
            return;
        }

        MVWorld world;
        String property = args[0];

        if (args.length == 1) {
            world = this.plugin.getMVWorld(p.getWorld().getName());
        } else {
            world = this.plugin.getMVWorld(args[1]);
        }

        if (world == null) {
            sender.sendMessage("That world does not exist!");
            return;
        }

        if (!ModifyCommand.validateAction(Action.Clear, property)) {
            sender.sendMessage("Sorry, you can't use CLEAR with " + property);
            sender.sendMessage("Please visit our wiki for more information: URLGOESHERE FERNFERRET DON'T FORGET IT!");
            return;
        }
        if (world.clearList(property)) {
            sender.sendMessage(property + " was cleared. It contains 0 values now.");
        } else {
            sender.sendMessage(property + " was NOT cleared.");
        }
    }

}
