/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.destination;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.utils.PermissionTools;
import com.onarandombox.MultiverseCore.utils.PlayerFinder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/** A factory class that will create destinations from specific strings. */
public class DestinationFactory {

    private static final Pattern CANNON_PATTERN = Pattern.compile("(?i)cannon-[\\d]+(\\.[\\d]+)?");

    private final MultiverseCore plugin;
    private final Map<String, Class<? extends MVDestination>> destList;
    private final Set<String> destPermissions;
    private final PermissionTools permTools;

    public DestinationFactory(MultiverseCore plugin) {
        this.plugin = plugin;
        this.destList = new HashMap<String, Class<? extends MVDestination>>();
        this.destPermissions = new HashSet<>();
        this.permTools = new PermissionTools(plugin);
    }

    /**
     * Parse a destination that has relation to sender, such as a cannon or player destination.
     *
     * @param teleportee        The player that is going to be teleported.
     * @param destinationName   The destination to parse.
     * @return A non-null MVDestination
     */
    @NotNull
    public MVDestination getPlayerAwareDestination(@NotNull Player teleportee,
                                                   @NotNull String destinationName) {

        // Prioritise world, in the event that a world is named after a player online.
        if (Bukkit.getWorld(destinationName) != null) {
            return getDestination(destinationName);
        }

        Player targetPlayer = PlayerFinder.get(destinationName, teleportee);
        if (targetPlayer != null) {
            return getDestination("pl:" + targetPlayer.getName());
        }

        if (CANNON_PATTERN.matcher(destinationName).matches()) {
            return getDestination(parseCannonDest(teleportee, destinationName));
        }

        return getDestination(destinationName);
    }

    /**
     * Parses a cannon destination.
     *
     * @param teleportee        The player that is going to be teleported.
     * @param destinationName   The destination to parse.
     * @return A destination string.
     */
    @NotNull
    private String parseCannonDest(@NotNull Player teleportee,
                                   @NotNull String destinationName) {

        String[] cannonSpeed = destinationName.split("-");
        try {
            double speed = Double.parseDouble(cannonSpeed[1]);
            destinationName = "ca:" + teleportee.getWorld().getName() + ":" + teleportee.getLocation().getX()
                    + "," + teleportee.getLocation().getY() + "," + teleportee.getLocation().getZ() + ":"
                    + teleportee.getLocation().getPitch() + ":" + teleportee.getLocation().getYaw() + ":" + speed;
        }
        catch (Exception e) {
            destinationName = "i:invalid";
        }

        return destinationName;
    }

    /**
     * Gets a new destination from a string.
     * Returns a new InvalidDestination if the string could not be parsed.
     *
     * @param destination The destination in string format.
     *
     * @return A non-null MVDestination
     */
    public MVDestination getDestination(String destination) {
        String idenChar = "";
        if (destination.split(":").length > 1) {
            idenChar = destination.split(":")[0];
        }

        if (this.destList.containsKey(idenChar)) {
            Class<? extends MVDestination> myClass = this.destList.get(idenChar);
            try {
                MVDestination mydest = myClass.newInstance();
                if (!mydest.isThisType(this.plugin, destination)) {
                    return new InvalidDestination();
                }
                mydest.setDestination(this.plugin, destination);
                return mydest;
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return new InvalidDestination();
    }

    /**
     * Registers a {@link MVDestination}.
     *
     * @param c The {@link Class} of the {@link MVDestination} to register.
     * @param identifier The {@link String}-identifier.
     * @return True if the class was successfully registered.
     */
    public boolean registerDestinationType(Class<? extends MVDestination> c, String identifier) {
        if (this.destList.containsKey(identifier)) {
            return false;
        }
        this.destList.put(identifier, c);
        // Special case for world defaults:
        if (identifier.equals("")) {
            identifier = "w";
        }
        addDestPerm("multiverse.teleport.self." + identifier,
                "Permission to teleport yourself for the " + identifier + " destination.");
        addDestPerm("multiverse.teleport.other." + identifier,
                "Permission to teleport other for the " + identifier + " destination.");
        return true;
    }

    /**
     * Registers a single destination permission.
     *
     * @param permNode      Permission node to register.
     * @param description   Info on what the permission node is used for.
     */
    private void addDestPerm(String permNode, String description) {
        if (this.plugin.getServer().getPluginManager().getPermission(permNode) != null) {
            Logging.fine("Destination permission node " + permNode + " already added.");
            return;
        }
        Permission perm = new Permission(permNode, description, PermissionDefault.OP);
        this.plugin.getServer().getPluginManager().addPermission(perm);
        this.permTools.addToParentPerms(permNode);
        this.destPermissions.add(permNode);
    }
    
    public Collection<String> getRegisteredIdentifiers() {
        return this.destList.keySet();
    }

    /**
     * Gets all registered permissions relating to usages of {@link MVDestination} on teleport.
     *
     * @return A set of permission nodes.
     */
    public Set<String> getDestPermissions() {
        return destPermissions;
    }
}
