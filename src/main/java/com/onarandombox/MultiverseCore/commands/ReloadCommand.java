package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MultiverseCore;

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
        sender.sendMessage("Reloading Multiverse-Core config.yml and worlds.yml");
        this.plugin.loadConfigs();
        this.plugin.loadWorlds(true);
        sender.sendMessage("Reload Complete!");
    }

}
