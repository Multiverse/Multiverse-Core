package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.utils.WorldManager;

public class ModifyClearCommand extends MultiverseCommand {
    private WorldManager worldManager;

    public ModifyClearCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Modify a World (Clear a property)");
        this.setCommandUsage("/mv modify" + ChatColor.GREEN + " clear {PROPERTY}" + ChatColor.GOLD + " [WORLD]");
        this.setArgRange(1, 2);
        this.addKey("mvm clear");
        this.addKey("mvmclear");
        this.addKey("mv modify clear");
        this.addKey("mvmodify clear");
        this.setPermission("multiverse.core.modify.clear", "Removes all values from a property. This will work on properties that contain lists.", PermissionDefault.OP);
        this.worldManager = this.plugin.getWorldManager();
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
            sender.sendMessage(this.getCommandDesc());
            sender.sendMessage(this.getCommandUsage());
            sender.sendMessage("Nothing changed.");
            return;
        }

        MVWorld world;
        String property = args.get(0);

        if (args.size() == 1) {
            world = this.worldManager.getMVWorld(p.getWorld().getName());
        } else {
            world = this.worldManager.getMVWorld(args.get(1));
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
            sender.sendMessage(ChatColor.GREEN + "Success! " + ChatColor.AQUA + property + ChatColor.WHITE + " was " + ChatColor.GREEN + "CLEARED" + ChatColor.WHITE + ". It contains " + ChatColor.LIGHT_PURPLE + "0" + ChatColor.WHITE + " values now.");
        } else {
            sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GOLD + property + ChatColor.WHITE + " was " + ChatColor.GOLD + "NOT" + ChatColor.WHITE + " cleared.");
        }
    }

}
