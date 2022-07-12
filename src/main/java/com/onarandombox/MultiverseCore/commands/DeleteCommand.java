/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandtools.queue.QueuedCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Deletes worlds.
 */
public class DeleteCommand extends MultiverseCommand {

    public DeleteCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Delete World");
        this.setCommandUsage("/mv delete" + ChatColor.GREEN + " {WORLD}");
        this.setArgRange(1, 1);
        this.addKey("mvdelete");
        this.addKey("mv delete");
        this.addCommandExample("/mv delete " + ChatColor.GOLD + "MyWorld");
        this.setPermission("multiverse.core.delete", "Deletes a world on your server. " + ChatColor.RED + "PERMANENTLY.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String worldName = args.get(0);

        this.plugin.getCommandQueueManager().addToQueue(new QueuedCommand(
                sender,
                deleteRunnable(sender, worldName),
                String.format("Are you sure you want to delete world '%s'? You cannot undo this action.", worldName)
        ));
    }

    private Runnable deleteRunnable(@NotNull CommandSender sender,
                                    @NotNull String worldName) {

        return () -> this.plugin.getMVWorldManager().addOrRemoveWorldSafely(worldName, "delete", () -> {
            sender.sendMessage(String.format("Deleting world '%s'...", worldName));
            if (this.plugin.getMVWorldManager().deleteWorld(worldName)) {
                sender.sendMessage(String.format("%sWorld %s was deleted!", ChatColor.GREEN, worldName));
                return;
            }
            sender.sendMessage(String.format("%sThere was an issue deleting '%s'! Please check console for errors.",
                    ChatColor.RED, worldName));
        });
    }
}
