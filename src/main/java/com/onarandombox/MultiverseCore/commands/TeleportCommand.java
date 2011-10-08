/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.utils.SafeTTeleporter;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.destination.DestinationFactory;
import com.onarandombox.MultiverseCore.destination.InvalidDestination;
import com.onarandombox.MultiverseCore.destination.WorldDestination;
import com.onarandombox.MultiverseCore.event.MVTeleportEvent;
import com.onarandombox.MultiverseCore.utils.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class TeleportCommand extends MultiverseCommand {
    private SafeTTeleporter playerTeleporter;

    public TeleportCommand(MultiverseCore plugin) {
        super(plugin);
        Permission menu = new Permission("multiverse.teleport.*", "Allows you to display the teleport menu.", PermissionDefault.OP);

        this.setName("Teleport");
        this.setCommandUsage("/mv tp " + ChatColor.GOLD + "[PLAYER]" + ChatColor.GREEN + " {WORLD}");
        this.setArgRange(1, 2);
        this.addKey("mvtp");
        this.addKey("mv tp");
        this.playerTeleporter = new SafeTTeleporter(this.plugin);
        this.setPermission(menu);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // Check if the command was sent from a Player.
        CommandSender teleporter = sender;
        Player teleportee = null;
        if (sender instanceof Player) {
            teleporter = (Player) sender;
        }

        String destinationName;

        if (args.size() == 2) {
            teleportee = this.plugin.getServer().getPlayer(args.get(0));
            if (teleportee == null) {
                sender.sendMessage("Sorry, I couldn't find player: " + args.get(0));
                return;
            }
            destinationName = args.get(1);

        } else {
            destinationName = args.get(0);
            if (!(sender instanceof Player)) {
                sender.sendMessage("From the console, you must specify a player to teleport");
                return;
            }
            teleportee = (Player) sender;
        }
        // Special case for cannons:
        if (destinationName.matches("(?i)cannon-[\\d]+(\\.[\\d]+)?")) {
            String[] cannonSpeed = destinationName.split("-");
            try {
                double speed = Double.parseDouble(cannonSpeed[1]);
                destinationName = "ca:" + teleportee.getWorld().getName() + ":" + teleportee.getLocation().getX() + "," + teleportee.getLocation().getY() + "," + teleportee.getLocation().getZ() + ":" + teleportee.getLocation().getPitch() + ":" + teleportee.getLocation().getYaw() + ":" + speed;
            } catch (Exception e) {
                destinationName = "i:invalid";
            }

        }
        DestinationFactory df = this.plugin.getDestinationFactory();
        MVDestination d = df.getDestination(destinationName);


        MVTeleportEvent teleportEvent = new MVTeleportEvent(d, teleportee, teleporter);
        this.plugin.getServer().getPluginManager().callEvent(teleportEvent);
        if (teleportEvent.isCancelled()) {
            this.plugin.log(Level.FINE, "Someone else cancelled the SafeTTeleporter Event!!!");
            return;
        }

        if (d != null && d instanceof InvalidDestination) {
            sender.sendMessage("Multiverse does not know how to take you to: " + ChatColor.RED + destinationName);
            return;
        }

        if (!this.checkSendPermissions(teleporter, teleportee, d)) {
            return;
        }

        if (teleporter != null && !this.plugin.getPermissions().canEnterDestination(teleporter, d)) {
            if (teleportee.equals(teleporter)) {
                teleporter.sendMessage("Doesn't look like you're allowed to go " + ChatColor.RED + "there...");
            } else {
                teleporter.sendMessage("Doesn't look like you're allowed to send " + ChatColor.GOLD + teleportee.getName() + ChatColor.WHITE + " to " + ChatColor.RED + "there...");
            }
            return;
        } else if (teleporter != null && !this.plugin.getPermissions().canTravelFromLocation(teleporter, d.getLocation(teleportee))) {
            if (teleportee.equals(teleporter)) {
                teleporter.sendMessage("DOH! Doesn't look like you can get to " + ChatColor.RED + "THERE from " + ChatColor.GREEN + ((Player) teleporter).getWorld().getName());
            } else {
                teleporter.sendMessage("DOH! Doesn't look like " + ChatColor.GREEN + ((Player) teleporter).getWorld().getName() + " can get to " + ChatColor.RED + "THERE from where they are...");
            }
            return;
        }

        // Special check to verify if players are tryint to teleport to the same
        // WORLDDestination as the world they're in, that they ALSO have multiverse.core.spawn.self

        if (d instanceof WorldDestination) {
            World w = d.getLocation(teleportee).getWorld();
            if (teleportee.getWorld().equals(w)) {
                if (teleporter.equals(teleportee)) {
                    if (!this.plugin.getPermissions().hasPermission(teleporter, "multiverse.core.spawn.self", true)) {
                        teleporter.sendMessage("Sorry you don't have permission to go to the world spawn!");
                        teleporter.sendMessage(ChatColor.RED + "  (multiverse.core.spawn.self)");
                        return;
                    }
                } else {
                    if (!this.plugin.getPermissions().hasPermission(teleporter, "multiverse.core.spawn.other", true)) {
                        teleporter.sendMessage("Sorry you don't have permission to send " + teleportee.getDisplayName() + "to the world spawn!");
                        teleporter.sendMessage(ChatColor.RED + "  (multiverse.core.spawn.other)");
                        return;
                    }
                }
            }
        }

        if (d.getLocation(teleportee) == null) {
            teleporter.sendMessage("Sorry Boss, I tried everything, but just couldn't teleport ya there!");
            return;
        }
        if (!this.playerTeleporter.safelyTeleport(teleportee, d)) {
            this.plugin.log(Level.FINE, "Could not teleport " + teleportee.getName() + " to " + LocationManipulation.strCoordsRaw(d.getLocation(teleportee)));
            this.plugin.log(Level.FINE, "Queueing Command");
            Class<?> paramTypes[] = {Player.class, Location.class};
            List<Object> items = new ArrayList<Object>();
            items.add(teleportee);
            items.add(d.getLocation(teleportee));
            String player = "you";
            if (!teleportee.equals(teleporter)) {
                player = teleportee.getName();
            }
            String message = ChatColor.GREEN + "Multiverse" + ChatColor.WHITE + " did not teleport " + ChatColor.AQUA + player + ChatColor.WHITE + " to " + ChatColor.DARK_AQUA + d.getName() + ChatColor.WHITE + " because it was unsafe.";
            this.plugin.getCommandHandler().queueCommand(sender, "mvteleport", "teleportPlayer", items, paramTypes, message, "Would you like to try anyway?", "", "", 15);
        }
    }

    private boolean checkSendPermissions(CommandSender teleporter, Player teleportee, MVDestination destination) {
        MVMessaging message = this.plugin.getMessaging();
        if (teleporter.equals(teleportee)) {
            if (!this.plugin.getPermissions().hasPermission(teleporter, "multiverse.teleport.self." + destination.getIdentifier(), true)) {
                message.sendMessages(teleporter, new String[]{"You don't have permission to teleport yourself to a " + ChatColor.GREEN + destination.getType() + " Destination.", ChatColor.RED + "   (multiverse.teleport.self." + destination.getIdentifier() + ")"});
                return false;
            }
        } else {
            if (!this.plugin.getPermissions().hasPermission(teleporter, "multiverse.teleport.other." + destination.getIdentifier(), true)) {
                message.sendMessages(teleporter, new String[]{"You don't have permission to teleport another player to a " + ChatColor.GREEN + destination.getType() + " Destination.", ChatColor.RED + "   (multiverse.teleport.other." + destination.getIdentifier() + ")"});
                return false;
            }
        }
        return true;
    }
}
