package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class ModifyCommand extends BaseCommand {

    public ModifyCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Modify a World";
        this.description = "Modify various aspects of worlds";
        this.usage = "/mvmodify" + ChatColor.GREEN + " {WORLD} {TYPE}" + ChatColor.GOLD + " [SEED]";
        this.minArgs = 2;
        this.maxArgs = 3;
        this.identifiers.add("mvcreate");
        this.permission = "multiverse.world.create";
        this.requiresOp = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // TODO Currently only NON-List items will be supported
        
    }

}
