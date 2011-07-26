package com.onarandombox.MultiverseCore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.event.MVConfigReloadEvent;

public class ReloadCommand extends MultiverseCommand {

    public ReloadCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Reload Configs");
        this.setCommandUsage("/mvreload");
        this.setArgRange(0, 0);
        this.addKey("mvreload");
        this.addKey("mv reload");
        this.setPermission("multiverse.core.reload", "Reloads worlds.yml and config.yml.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        sender.sendMessage(ChatColor.GOLD + "Reloading all Multiverse Plugin configs...");
        this.plugin.loadConfigs();
        this.plugin.loadWorlds(true);
        
        // Create the event
        List<String> configsLoaded = new ArrayList<String>();
        configsLoaded.add("Multiverse-Core - config.yml");
        configsLoaded.add("Multiverse-Core - worlds.yml");
        MVConfigReloadEvent configReload = new MVConfigReloadEvent(configsLoaded);
        this.plugin.getServer().getPluginManager().callEvent(configReload);
        for(String s : configReload.getAllConfigsLoaded()) {
            sender.sendMessage(s);
        }
        
        sender.sendMessage(ChatColor.GREEN + "Reload Complete!");
    }

}
