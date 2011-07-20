package com.onarandombox.MultiverseCore.commands;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class VersionCommand extends MultiverseCommand {

    public VersionCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Multiverse Version");
        this.setCommandUsage("/mv version");
        this.setArgRange(0, 0);
        this.addKey("mv version");
        this.addKey("mvv");
        this.addKey("mvversion");
        this.setPermission("multiverse.core.version", "What version of Multiverse-Core are you on!.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // Check if the command was sent from a Player.
        if (sender instanceof Player) {
            sender.sendMessage("Version info dumped to console. Please check your server logs.");
        }
        this.plugin.log(Level.INFO, "Multiverse-Core Version: " + this.plugin.getDescription().getVersion());
        this.plugin.log(Level.INFO, "Bukkit Version: " + this.plugin.getServer().getVersion());
        this.plugin.log(Level.INFO, "Loaded Worlds: " + this.plugin.getMVWorlds().size());
        this.plugin.log(Level.INFO, "Multiverse Plugins Loaded: " + this.plugin.getPluginCount());
        this.plugin.log(Level.INFO, "Economy being used: " + this.plugin.getBank().getEconUsed());
        this.plugin.log(Level.INFO, "Permissions Plugin: " + this.plugin.getPermissions().getType());
        this.plugin.log(Level.INFO, "Dumping Config Values: (version " + this.plugin.getConfig().getString("version", "NOT SET") + ")");
        this.plugin.log(Level.INFO, "messagecooldown: " + this.plugin.getConfig().getString("messagecooldown", "NOT SET"));
        this.plugin.log(Level.INFO, "teleportcooldown: " + this.plugin.getConfig().getString("teleportcooldown", "NOT SET"));
        this.plugin.log(Level.INFO, "worldnameprefix: " + this.plugin.getConfig().getString("messagecooldown", "NOT SET"));
        this.plugin.log(Level.INFO, "opfallback: " + this.plugin.getConfig().getString("worldnameprefix", "NOT SET"));
        this.plugin.log(Level.INFO, "disableautoheal: " + this.plugin.getConfig().getString("disableautoheal", "NOT SET"));
        this.plugin.log(Level.INFO, "fakepvp: " + this.plugin.getConfig().getString("fakepvp", "NOT SET"));
    }
}
