/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.destination;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.commands.TeleportCommand;
import com.onarandombox.MultiverseCore.utils.PermissionTools;
import com.pneumaticraft.commandhandler.Command;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A factory class that will create destinations from specific strings. */
public class DestinationFactory {
    private MultiverseCore plugin;
    private Map<String, Class<? extends MVDestination>> destList;
    private Command teleportCommand;

    public DestinationFactory(MultiverseCore plugin) {
        this.plugin = plugin;
        this.destList = new HashMap<String, Class<? extends MVDestination>>();
        List<Command> cmds = this.plugin.getCommandHandler().getAllCommands();
        for (Command c : cmds) {
            if (c instanceof TeleportCommand) {
                this.teleportCommand = c;
            }
        }
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
        Permission self = this.plugin.getServer().getPluginManager().getPermission("multiverse.teleport.self." + identifier);
        Permission other = this.plugin.getServer().getPluginManager().getPermission("multiverse.teleport.other." + identifier);
        PermissionTools pt = new PermissionTools(this.plugin);
        if (self == null) {
            self = new Permission("multiverse.teleport.self." + identifier,
                    "Permission to teleport yourself for the " + identifier + " destination.", PermissionDefault.OP);
            this.plugin.getServer().getPluginManager().addPermission(self);
            pt.addToParentPerms("multiverse.teleport.self." + identifier);
        }
        if (other == null) {
            other = new Permission("multiverse.teleport.other." + identifier,
                    "Permission to teleport others for the " + identifier + " destination.", PermissionDefault.OP);
            this.plugin.getServer().getPluginManager().addPermission(other);
            pt.addToParentPerms("multiverse.teleport.other." + identifier);
        }
        this.teleportCommand.addAdditonalPermission(self);
        this.teleportCommand.addAdditonalPermission(other);
        return true;
    }
}
