package com.onarandombox.MultiverseCore.command.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;

public class DeleteCommand extends Command {

    public DeleteCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Delete World";
        this.description = "Deletes a world on your server. " + ChatColor.RED + "PERMANENTLY.";
        this.usage = "/mvdelete" + ChatColor.GREEN + " {WORLD} ";
        this.minArgs = 1;
        this.maxArgs = 1;
        this.identifiers.add("mvdelete");
        this.permission = "multiverse.world.delete";
        this.requiresOp = true;
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Class<?> paramTypes[] = { String.class };
        ((MultiverseCore) this.plugin).getCommandHandler().queueCommand(sender, "mvdelete", "deleteWorld", args, paramTypes, "World Deleted!", "World was not deleted!");
    }
}
