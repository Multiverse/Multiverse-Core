package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class DeleteCommand extends MultiverseCommand {

    public DeleteCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("delete")
    @CommandPermission("multiverse.core.delete")
    @Syntax("<world>")
    @CommandCompletion("@MVWorlds")
    @Description("")
    public void onDeleteCommand(@NotNull CommandSender sender,
                                @NotNull @Flags("other") MultiverseWorld world) {

        this.plugin.getCommandQueueManager().addToQueue(sender, deleteRunnable(sender, world));
    }

    private Runnable deleteRunnable(@NotNull CommandSender sender,
                                    @NotNull MultiverseWorld world) {

        return () -> {
            //TODO: deleteWorld method should take world object directly
            String resultMessage = (this.plugin.getMVWorldManager().deleteWorld(world.getName()))
                    ? ChatColor.GREEN + "World '" + world.getName() + "' Deleted!"
                    : ChatColor.RED + "World '" + world.getName() + "' could NOT be deleted!";

            sender.sendMessage(resultMessage);
        };
    }
}
