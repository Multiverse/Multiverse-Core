package com.onarandombox.MultiverseCore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class DeleteCommand extends MultiverseCommand {

    public DeleteCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Delete World");
        this.setCommandUsage("/mv delete" + ChatColor.GREEN + " {WORLD}");
        this.setArgRange(1, 1);
        this.addKey("mvdelete");
        this.addKey("mv delete");
        this.setPermission("multiverse.core.delete", "Deletes a world on your server. " + ChatColor.RED + "PERMANENTLY.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Class<?> paramTypes[] = { String.class };
        List<Object> objectArgs = new ArrayList<Object>(args);
        //this.plugin.getCommandHandler().queueCommand(sender, "mvdelete", "deleteWorld", objectArgs, paramTypes, "World Deleted!", "World was not deleted!");
        this.plugin.getCommandHandler().queueCommand(sender, "mvteleport", "deleteWorld", objectArgs, paramTypes, "Blah", "Blah", "Success", "Fail", 15);
    }
}
