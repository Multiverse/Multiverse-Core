package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class SpawnCommand extends MultiverseCommand {

    public SpawnCommand(MultiverseCore plugin) {
        super(plugin);
        Permission otherPerm = new Permission("multiverse.core.spawn.other", "Teleports another player to the spawn of the world they are in.", PermissionDefault.OP);
        this.setName("Spawn");
        this.setCommandUsage("/mv spawn" + ChatColor.GOLD + " [PLAYER]");
        this.setArgRange(0, 1);
        this.addKey("mvspawn");
        this.addKey("mv spawn");
        this.addKey("mvs");
        this.setPermission("multiverse.core.spawn.self", "Teleports you to the Spawn Point of the world you are in.", PermissionDefault.OP);
        this.addAdditonalPermission(otherPerm);

    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        // If a persons name was passed in, you must be A. the console, or B have permissions
        if (args.size() == 1) {
            if (player != null && !this.plugin.getPermissions().hasPermission(player, "multiverse.core.spawn.other", true)) {
                sender.sendMessage("You don't have permission to teleport another player to spawn. (multiverse.core.spawn.other)");
                return;
            }
            Player target = this.plugin.getServer().getPlayer(args.get(0));
            if (target != null) {
                target.sendMessage("Teleporting to this world's spawn...");
                spawnAccurately(target);

                if (player != null) {
                    target.sendMessage("You were teleported by: " + ChatColor.YELLOW + player.getName());
                } else {
                    target.sendMessage("You were teleported by: " + ChatColor.LIGHT_PURPLE + "the console");
                }
            } else {
                sender.sendMessage(args.get(0) + " is not logged on right now!");
            }
        } else {
            if (player != null && !this.plugin.getPermissions().hasPermission(player, "multiverse.core.spawn.self", true)) {
                sender.sendMessage("You don't have permission to teleport yourself to spawn. (multiverse.core.spawn.self)");
                return;
            }
            if (player != null) {
                player.sendMessage("Teleporting to this world's spawn...");
                spawnAccurately(player);
            } else {
                sender.sendMessage("From the console, you must provide a PLAYER.");
            }
        }
    }

    private void spawnAccurately(Player player) {
        MVWorld world = this.plugin.getWorldManager().getMVWorld(player.getWorld().getName());
        if (world != null) {
            player.teleport(world.getSpawnLocation());
        } else {
            player.teleport(player.getWorld().getSpawnLocation());
        }
    }
}
