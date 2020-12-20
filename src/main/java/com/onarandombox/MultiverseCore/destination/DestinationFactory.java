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
import com.pneumaticraft.commandhandler.Command;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** A factory class that will create destinations from specific strings. */
public class DestinationFactory {
    private final MultiverseCore plugin;
    private final Map<String, Class<? extends MVDestination>> destList;
    private final Set<String> destPermissions;
    private final PermissionTools permTools;

    public DestinationFactory(MultiverseCore plugin) {
        this.plugin = plugin;
        this.destList = new HashMap<>();
        this.destPermissions = new HashSet<>();
        this.permTools = new PermissionTools(plugin);
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

    private void addDestPerm(String permNode, String description) {
        if (this.plugin.getServer().getPluginManager().getPermission(permNode) != null) {
            Logging.fine("Destination permission node " + permNode + " already added.");
            return;
        }

        Permission perm = new Permission(permNode, description, PermissionDefault.OP);
        this.plugin.getServer().getPluginManager().addPermission(perm);
        permTools.addToParentPerms(permNode);
        destPermissions.add(permNode);
    }

    public Collection<String> getIdentifiers() {
        return destList.keySet();
    }

    public Set<String> getPermissions() {
        return destPermissions;
    }
}
