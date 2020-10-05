/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.event.MVConfigReloadEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;

/**
 * Reloads worlds.yml and config.yml.
 */
public class ReloadCommand extends MultiverseCommand {

    public ReloadCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Reload Configs");
        this.setCommandUsage("/mv reload");
        this.setArgRange(0, 0);
        this.addKey("mvreload");
        this.addKey("mvr");
        this.addKey("mv reload");
        this.addCommandExample("/mv reload");
        this.setPermission("multiverse.core.reload", "Reloads worlds.yml and config.yml.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        sender.sendMessage(ChatColor.GOLD + "Reloading all Multiverse Plugin configs...");
        this.plugin.loadConfigs();
        this.plugin.getAnchorManager().loadAnchors();
        this.plugin.getMVWorldManager().loadWorlds(true);

        List<String> configsLoaded = new ArrayList<String>();
        configsLoaded.add("Multiverse-Core - config.yml");
        configsLoaded.add("Multiverse-Core - worlds.yml");
        configsLoaded.add("Multiverse-Core - anchors.yml");
        // Create the event
        MVConfigReloadEvent configReload = new MVConfigReloadEvent(configsLoaded);
        // Fire it off
        this.plugin.getServer().getPluginManager().callEvent(configReload);
        for (String s : configReload.getAllConfigsLoaded()) {
            sender.sendMessage(s);
        }

        sender.sendMessage(ChatColor.GREEN + "Reload Complete!");
    }

}
