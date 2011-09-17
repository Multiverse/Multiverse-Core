package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MVTeleport;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.event.MVTeleportEvent;
import com.onarandombox.utils.DestinationFactory;
import com.onarandombox.utils.InvalidDestination;
import com.onarandombox.utils.LocationManipulation;
import com.onarandombox.utils.MVDestination;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class TeleportCommand extends MultiverseCommand {
    private MVTeleport playerTeleporter;

    public TeleportCommand(MultiverseCore plugin) {
        super(plugin);
        Permission menu = new Permission("multiverse.teleport", "Allows you to display the teleport menu.", PermissionDefault.OP);

        this.setName("Teleport");
        this.setCommandUsage("/mv tp " + ChatColor.GOLD + "[PLAYER]" + ChatColor.GREEN + " {WORLD}");
        this.setArgRange(1, 2);
        this.addKey("mvtp");
        this.addKey("mv tp");
        this.playerTeleporter = new MVTeleport(this.plugin);
        this.setPermission(menu);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // Check if the command was sent from a Player.
        Player teleporter = null;
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
            teleporter = (Player) sender;
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
            this.plugin.log(Level.FINE, "Someone else cancelled the MVTeleport Event!!!");
            return;
        }

        if (d != null && d instanceof InvalidDestination) {
            sender.sendMessage("Multiverse does not know how to take you to: " + ChatColor.RED + destinationName);
            return;
        }

        if(!this.checkSendPermissions(teleporter,teleportee,d)) {
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
                teleporter.sendMessage("DOH! Doesn't look like you can get to " + ChatColor.RED + "THERE from " + ChatColor.GREEN + teleporter.getWorld().getName());
            } else {
                teleporter.sendMessage("DOH! Doesn't look like " + ChatColor.GREEN + teleporter.getWorld().getName() + " can get to " + ChatColor.RED + "THERE from where they are...");
            }
            return;
        }
        if (d.getLocation(teleportee) == null) {
            teleporter.sendMessage("Sorry Boss, I tried everything, but just couldn't teleport ya there!");
            return;
        }
        if (!this.playerTeleporter.safelyTeleport(teleportee, d)) {
            this.plugin.log(Level.FINE, "Could not teleport " + teleportee.getName() + " to " + LocationManipulation.strCoordsRaw(d.getLocation(teleportee)));
            this.plugin.log(Level.FINE, "Queueing Command");
            Class<?> paramTypes[] = { Player.class, Location.class };
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
        if(teleporter.equals(teleportee)) {
            if(!this.plugin.getPermissions().hasPermission(teleporter, "multiverse.teleport.self."+destination.getIdentifier(),true)) {
                teleporter.sendMessage("You don't have permission to teleport yourself to a " + ChatColor.GREEN + destination.getType() + " Destination.");
                teleporter.sendMessage(ChatColor.RED + "   (multiverse.teleport.self."+destination.getIdentifier()+")");
                return false;
            }
        } else {
            if(!this.plugin.getPermissions().hasPermission(teleporter, "multiverse.teleport.other."+destination.getIdentifier(),true)) {
                teleporter.sendMessage("You don't have permission to teleport another player to a " + ChatColor.GREEN + destination.getType() + " Destination.");
                teleporter.sendMessage(ChatColor.RED + "   (multiverse.teleport.other."+destination.getIdentifier()+")");
                return false;
            }
        }
        return true;
    }
}
