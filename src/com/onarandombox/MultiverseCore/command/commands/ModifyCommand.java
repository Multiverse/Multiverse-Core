package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

enum Action{Set, Add, Remove}
public class ModifyCommand extends BaseCommand {

    public ModifyCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Modify a World";
        this.description = "Modify various aspects of worlds. See the help wiki for how to use this command properly. If you do not include a world, the current world will be used";
        this.usage = "/mvmodify" + ChatColor.GOLD + " [WORLD] " + ChatColor.GREEN + "{SET|ADD|REMOVE} {VALUE} {PROPERTY}";
        this.minArgs = 3;
        this.maxArgs = 4;
        this.identifiers.add("mvmodify");
        this.permission = "multiverse.world.modify";
        this.requiresOp = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // We NEED a world from the command line
        if(args.length == 3 && !(sender instanceof Player)){
            sender.sendMessage("From the command line, WORLD is required.");
            sender.sendMessage("Nothing changed.");
            return;
        }
        
        MVWorld world;
        Action action;
        String value;
        String property;
        Player p;
        if(args.length == 3) {
            p = (Player) sender;
            world = plugin.getMVWorld(p.getWorld().getName());
        } else {
            
        }
        
        
    }
    
    private Action getActionEnum(String action) {
        if(action.equalsIgnoreCase("set")) {
            return Action.Set;
        }
        if(action.equalsIgnoreCase("add") || action.equalsIgnoreCase("+")) {
            return Action.Add;
        }
        if(action.equalsIgnoreCase("remove") || action.equalsIgnoreCase("-")) {
            return Action.Remove;
        }
        return null;
    }

}
