package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class GeneratorCommand extends MultiverseCommand {

    public GeneratorCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("World Information");
        this.setCommandUsage("/mv generators");
        this.setArgRange(0, 0);
        this.addKey("mv generators");
        this.addKey("mvgenerators");
        this.addKey("mv gens");
        this.addKey("mvgens");
        this.setPermission("multiverse.core.generator", "Returns a list of Loaded Generator Plugins.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        this.plugin.log(Level.INFO, "PLEASE IGNORE the 'Plugin X does not contain any generators' message below!");
        Plugin[] plugins = this.plugin.getServer().getPluginManager().getPlugins();
        List<String> generators = new ArrayList<String>();
        for (Plugin p : plugins) {
            if (p.isEnabled() && p.getDefaultWorldGenerator("world", "") != null) {
                generators.add(p.getDescription().getName());
            }
        }
        sender.sendMessage(ChatColor.AQUA + "--- Loaded Generator Plugins ---");
        String loadedGens = "";
        boolean altColor = false;
        for (String s : generators) {
            if (altColor) {
                altColor = false;
                loadedGens += ChatColor.YELLOW + s + "";
            } else {
                altColor = true;
                loadedGens += ChatColor.WHITE + s + "";
            }
        }
        if (loadedGens.length() == 0) {
            loadedGens = ChatColor.RED + "No Generator Plugins found.";
        }
        sender.sendMessage(loadedGens);
    }
}
