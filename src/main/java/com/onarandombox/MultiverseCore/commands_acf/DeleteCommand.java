package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("mv")
public class DeleteCommand extends MultiverseCommand {

    public DeleteCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("delete")
    @CommandPermission("multiverse.core.delete")
    @Syntax("<world>")
    @CommandCompletion("@mvworlds")
    @Description("")
    public void onDeleteCommand(CommandSender sender, @Single String worldName) {
        this.plugin.getCommandQueueManager().addToQueue(sender, deleteRunnable(sender, worldName));
    }

    private Runnable deleteRunnable(CommandSender sender, String worldName) {
        return () -> {
            //TODO: deleteWorld method should take world object directly
            if (!this.plugin.getMVWorldManager().deleteWorld(worldName)) {
                sender.sendMessage(ChatColor.RED + "World '" + worldName + "' could NOT be deleted!");
            }
            sender.sendMessage(ChatColor.GREEN + "World '" + worldName + "' Deleted!");
        };
    }
}
