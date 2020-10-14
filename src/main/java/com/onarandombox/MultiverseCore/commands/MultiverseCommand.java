/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseMessaging;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.pneumaticraft.commandhandler.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * A generic Multiverse-command.
 */
public abstract class MultiverseCommand extends Command {

    /**
     * The reference to the core.
     */
    protected MultiverseCore plugin;
    /**
     * The reference to {@link MultiverseMessaging}.
     */
    protected MultiverseMessaging messaging;

    public MultiverseCommand(MultiverseCore plugin) {
        super(plugin);
        this.plugin = plugin;
        this.messaging = this.plugin.getMessaging();
    }

    /**
     * Get world that sender is in.
     *
     * @param sender   The object executing the command.
     * @return Returns {@link MultiverseWorld} sender is in if sender is a player, else returns null.
     */
    protected MultiverseWorld getSenderWorld(CommandSender sender) {
        if (sender instanceof Player) {
            return this.plugin.getMVWorldManager().getMVWorld(((Player) sender).getWorld().getName());
        }
        return null;
    }

    /**
     * Get world based on sender and args.
     *
     * @param sender         The object executing the command.
     * @param args           The arguments of the command.
     * @param argWorldIndex  Index of args where the worldName is.
     * @return Returns {@link MultiverseWorld} if target world is found, else return null.
     */
    protected MultiverseWorld getTargetWorld(CommandSender sender, List<String> args, int argWorldIndex) {
        MultiverseWorld world;

        if (args.size() <= argWorldIndex) {
            world = getSenderWorld(sender);
            if (world == null) {
                sender.sendMessage(ChatColor.RED + "From the command line, WORLD is required.");
                sender.sendMessage(this.getCommandDesc());
                sender.sendMessage(this.getCommandUsage());
                sender.sendMessage("Nothing changed.");
            }
            return world;
        }

        String targetWorld = args.get(argWorldIndex);
        world = this.plugin.getMVWorldManager().getMVWorld(targetWorld);
        if (world == null) {
            sender.sendMessage(ChatColor.RED + "Failure!" + ChatColor.WHITE + " World '" + ChatColor.AQUA + targetWorld
                    + ChatColor.WHITE + "' does not exist.");
        }
        return world;
    }

    @Override
    public abstract void runCommand(CommandSender sender, List<String> args);

}
