package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MVCommandHandler;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class ImportCommand extends BaseCommand {

    public ImportCommand(MultiverseCore plugin) {
        super(plugin);
        name = "Import World";
        description = "Imports a new world of the specified type";
        usage = "/mvimport" + ChatColor.GREEN + " {NAME} {TYPE}";
        minArgs = 2;
        maxArgs = 2;
        identifiers.add("mvimport");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // TODO Auto-generated method stub
        
    }

}
