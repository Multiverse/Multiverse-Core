/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.logging.Level;

/**
 * Utility-class for permissions.
 */
public class PermissionTools {
    private MultiverseCore plugin;

    public PermissionTools(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a permission to the parent-permissions.
     * @param permString The new permission as {@link String}.
     */
    public void addToParentPerms(String permString) {
        String permStringChopped = permString.replace(".*", "");

        String[] seperated = permStringChopped.split("\\.");
        String parentPermString = getParentPerm(seperated);
        if (parentPermString == null) {
            addToRootPermission("*", permStringChopped);
            addToRootPermission("*.*", permStringChopped);
            return;
        }
        Permission parentPermission = this.plugin.getServer().getPluginManager().getPermission(parentPermString);
        // Creat parent and grandparents
        if (parentPermission == null) {
            parentPermission = new Permission(parentPermString);
            this.plugin.getServer().getPluginManager().addPermission(parentPermission);

            this.addToParentPerms(parentPermString);
        }
        // Create actual perm.
        Permission actualPermission = this.plugin.getServer().getPluginManager().getPermission(permString);
        // Extra check just to make sure the actual one is added
        if (actualPermission == null) {

            actualPermission = new Permission(permString);
            this.plugin.getServer().getPluginManager().addPermission(actualPermission);
        }
        if (!parentPermission.getChildren().containsKey(permString)) {
            parentPermission.getChildren().put(actualPermission.getName(), true);
            this.plugin.getServer().getPluginManager().recalculatePermissionDefaults(parentPermission);
        }
    }

    private void addToRootPermission(String rootPerm, String permStringChopped) {
        Permission rootPermission = this.plugin.getServer().getPluginManager().getPermission(rootPerm);
        if (rootPermission == null) {
            rootPermission = new Permission(rootPerm);
            this.plugin.getServer().getPluginManager().addPermission(rootPermission);
        }
        rootPermission.getChildren().put(permStringChopped + ".*", true);
        this.plugin.getServer().getPluginManager().recalculatePermissionDefaults(rootPermission);
    }

    /**
     * If the given permission was 'multiverse.core.tp.self', this would return 'multiverse.core.tp.*'.
     *
     * @param separatedPermissionString The array of a dot separated perm string.
     * @return The dot separated parent permission string.
     */
    private static String getParentPerm(String[] separatedPermissionString) {
        if (separatedPermissionString.length == 1) {
            return null;
        }
        String returnString = "";
        for (int i = 0; i < separatedPermissionString.length - 1; i++) {
            returnString += separatedPermissionString[i] + ".";
        }
        return returnString + "*";
    }

    /**
     * Checks if the given {@link Player} has enough money to enter the specified {@link MultiverseWorld}.
     * @param fromWorld The {@link MultiverseWorld} the player is coming from.
     * @param toWorld The {@link MultiverseWorld} the player is going to.
     * @param teleporter The teleporter.
     * @param teleportee The teleportee.
     * @param pay If the player has to pay the money.
     * @return True if the player can enter the world.
     */
    public boolean playerHasMoneyToEnter(MultiverseWorld fromWorld, MultiverseWorld toWorld, CommandSender teleporter, Player teleportee, boolean pay) {
        Player teleporterPlayer;
        if (plugin.getMVConfig().getTeleportIntercept()) {
            if (teleporter instanceof ConsoleCommandSender) {
                return true;
            }

            if (teleporter == null) {
                teleporter = teleportee;
            }

            if (!(teleporter instanceof Player)) {
                return false;
            }
            teleporterPlayer = (Player) teleporter;
        } else {
            if (teleporter instanceof Player) {
                teleporterPlayer = (Player) teleporter;
            } else {
                teleporterPlayer = null;
            }

            // Old-style!
            if (teleporterPlayer == null) {
                return true;
            }
        }

        // If the toWorld isn't controlled by MV,
        // We don't care.
        if (toWorld == null) {
            return true;
        }

        // Only check payments if it's a different world:
        if (!toWorld.equals(fromWorld)) {
            final double price = toWorld.getPrice();
            // Don't bother checking economy stuff if it doesn't even cost to enter.
            if (price == 0D) {
                return true;
            }
            // If the player does not have to pay, return now.
            if (this.plugin.getMVPerms().hasPermission(teleporter, toWorld.getExemptPermission().getName(), true)) {
                return true;
            }

            final MVEconomist economist = plugin.getEconomist();
            final Material currency = toWorld.getCurrency();
            final String formattedAmount = economist.formatPrice(price, currency);

            if (economist.isPlayerWealthyEnough(teleporterPlayer, price, currency)) {
                if (pay) {
                    if (price < 0D) {
                        economist.deposit(teleporterPlayer, -price, currency);
                    } else {
                        economist.withdraw(teleporterPlayer, price, currency);
                    }
                    sendTeleportPaymentMessage(economist, teleporterPlayer, teleportee, toWorld.getColoredWorldString(), price, currency);
                }
            } else {
                if (teleportee.equals(teleporter)) {
                    teleporterPlayer.sendMessage(economist.getNSFMessage(currency,
                            "You need " + formattedAmount + " to enter " + toWorld.getColoredWorldString()));
                } else {
                    teleporterPlayer.sendMessage(economist.getNSFMessage(currency,
                            "You need " + formattedAmount + " to send " + teleportee.getName() + " to " + toWorld.getColoredWorldString()));
                }
                return false;
            }
        }
        return true;
    }

    private void sendTeleportPaymentMessage (MVEconomist economist, Player teleporterPlayer, Player teleportee, String toWorld, double price, Material currency) {
        price = Math.abs(price);
        if (teleporterPlayer.equals(teleportee)) {
            teleporterPlayer.sendMessage("You were " + (price > 0D ? "charged " : "given ") + economist.formatPrice(price, currency) + " for teleporting to " + toWorld);
        } else {
            teleporterPlayer.sendMessage("You were " + (price > 0D ? "charged " : "given ") + economist.formatPrice(price, currency) + " for teleporting " + teleportee.getName() + " to " + toWorld);
        }
    }


    /**
     * Checks to see if player can go to a world given their current status.
     * <p>
     * The return is a little backwards, and will return a value safe for event.setCancelled.
     *
     * @param fromWorld  The MultiverseWorld they are in.
     * @param toWorld    The MultiverseWorld they want to go to.
     * @param teleporter The CommandSender that wants to send someone somewhere. If null,
     *                   will be given the same value as teleportee.
     * @param teleportee The player going somewhere.
     * @return True if they can't go to the world, False if they can.
     */
    public boolean playerCanGoFromTo(MultiverseWorld fromWorld, MultiverseWorld toWorld, CommandSender teleporter, Player teleportee) {
        Logging.finest("Checking '" + teleporter + "' can send '" + teleportee + "' somewhere");

        Player teleporterPlayer;
        if (plugin.getMVConfig().getTeleportIntercept()) {
            // The console can send anyone anywhere
            if (teleporter instanceof ConsoleCommandSender) {
                return true;
            }

            // Make sure we have a teleporter of some kind, even if it's inferred to be the teleportee
            if (teleporter == null) {
                teleporter = teleportee;
            }

            // Now make sure we can cast the teleporter to a player, 'cause I'm tired of console things now
            if (!(teleporter instanceof Player)) {
                return false;
            }
            teleporterPlayer = (Player) teleporter;
        } else {
            if (teleporter instanceof Player) {
                teleporterPlayer = (Player) teleporter;
            } else {
                teleporterPlayer = null;
            }

            // Old-style!
            if (teleporterPlayer == null) {
                return true;
            }
        }

        // Actual checks
        if (toWorld != null) {
            if (!this.plugin.getMVPerms().canEnterWorld(teleporterPlayer, toWorld)) {
                if (teleportee.equals(teleporter)) {
                    teleporter.sendMessage("You don't have access to go here...");
                } else {
                    teleporter.sendMessage("You can't send " + teleportee.getName() + " here...");
                }

                return false;
            }
        } else {
            // TODO: Determine if this value is false because a world didn't exist
            // or if it was because a world wasn't imported.
            return true;
        }
        if (fromWorld != null) {
            if (fromWorld.getWorldBlacklist().contains(toWorld.getName())) {
                if (teleportee.equals(teleporter)) {
                    teleporter.sendMessage("You don't have access to go to " + toWorld.getColoredWorldString() + " from " + fromWorld.getColoredWorldString());
                } else {
                    teleporter.sendMessage("You don't have access to send " + teleportee.getName() + " from "
                         + fromWorld.getColoredWorldString() + " to " + toWorld.getColoredWorldString());
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Checks to see if a player can bypass the player limit.
     *
     * @param toWorld The world travelling to.
     * @param teleporter The player that initiated the teleport.
     * @param teleportee The player travelling.
     * @return True if they can bypass the player limit.
     */
    public boolean playerCanBypassPlayerLimit(MultiverseWorld toWorld, CommandSender teleporter, Player teleportee) {
        if (teleporter == null) {
            teleporter = teleportee;
        }

        if (!(teleporter instanceof Player)) {
            return true;
        }

        MVPermissions perms = plugin.getMVPerms();
        if (perms.hasPermission(teleportee, "mv.bypass.playerlimit." + toWorld.getName(), false)) {
            return true;
        } else {
            teleporter.sendMessage("The world " + toWorld.getColoredWorldString() + " is full");
            return false;
        }
    }

    /**
     * Checks to see if a player should bypass game mode restrictions.
     *
     * @param toWorld world travelling to.
     * @param teleportee player travelling.
     * @return True if they should bypass restrictions
     */
    public boolean playerCanIgnoreGameModeRestriction(MultiverseWorld toWorld, Player teleportee) {
        if (toWorld != null) {
            return this.plugin.getMVPerms().canIgnoreGameModeRestriction(teleportee, toWorld);
        } else {
            // TODO: Determine if this value is false because a world didn't exist
            // or if it was because a world wasn't imported.
            return true;
        }
    }

    /**
     * Checks to see if a player should bypass fly restrictions.
     *
     * @param toWorld world travelling to.
     * @param teleportee player travelling.
     * @return True if they should bypass restrictions
     */
    public boolean playerCanIgnoreFlyRestriction(MultiverseWorld toWorld, Player teleportee) {
        if (toWorld != null) {
            return this.plugin.getMVPerms().canIgnoreFlyRestriction(teleportee, toWorld);
        } else {
            // TODO: Determine if this value is false because a world didn't exist
            // or if it was because a world wasn't imported.
            return true;
        }
    }
}
