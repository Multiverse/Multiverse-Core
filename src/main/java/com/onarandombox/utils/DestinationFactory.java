package com.onarandombox.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class DestinationFactory {
    private MultiverseCore plugin;
    private Map<String, Class<? extends MVDestination>> destList;
    private List<Permission> teleportPermissions;

    public DestinationFactory(MultiverseCore plugin) {
        this.plugin = plugin;
        this.destList = new HashMap<String, Class<? extends MVDestination>>();
        this.teleportPermissions = new ArrayList<Permission>();
    }

    public MVDestination getDestination(String destination) {
        String idenChar = "";
        if (destination.split(":").length > 1) {
            idenChar = destination.split(":")[0];
        }

        if (this.destList.containsKey(idenChar)) {
            Class<? extends MVDestination> myClass = this.destList.get(idenChar);
            try {
                MVDestination mydest = myClass.newInstance();
                if (!mydest.isThisType((MultiverseCore) this.plugin, destination)) {
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

    public boolean registerDestinationType(Class<? extends MVDestination> c, String identifier) {
        if (this.destList.containsKey(identifier)) {
            return false;
        }
        this.destList.put(identifier, c);
        // Special case for world defaults:
        if(identifier.equals("")) {
            identifier = "w";
        }
        Permission self = this.plugin.getServer().getPluginManager().getPermission("multiverse.teleport.self."+identifier);
        Permission other = this.plugin.getServer().getPluginManager().getPermission("multiverse.teleport.other."+identifier);
        PermissionTools pt = new PermissionTools(this.plugin);
        if(self == null) {
            this.plugin.getServer().getPluginManager().addPermission(new Permission("multiverse.teleport.self."+identifier,"Permission to teleport yourself for the " + identifier + " destination.", PermissionDefault.OP));
            pt.addToParentPerms("multiverse.teleport.self."+identifier);
        }
        if(other == null) {
            this.plugin.getServer().getPluginManager().addPermission(new Permission("multiverse.teleport.other."+identifier,"Permission to teleport others for the " + identifier + " destination.", PermissionDefault.OP));
            pt.addToParentPerms("multiverse.teleport.other."+identifier);
        }
        this.teleportPermissions.add(self);
        this.teleportPermissions.add(other);
        return true;
    }

    public List<Permission> getTeleportPermissions() {
        return this.teleportPermissions;
    }
}
