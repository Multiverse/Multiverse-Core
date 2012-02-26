/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2012.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.enums.AllowedPortalType;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.world.PortalCreateEvent;

/**
 * A custom listener for portal related events.
 */
public class MVPortalListener implements Listener {

    private MultiverseCore plugin;

    public MVPortalListener(MultiverseCore core) {
        this.plugin = core;
    }

    /**
     * This is called when an entity creates a portal.
     *
     * @param event The event where an entity created a portal.
     */
    @EventHandler
    public void entityPortalCreate(EntityCreatePortalEvent event) {
        if (event.isCancelled() || event.getBlocks().size() == 0) {
            return;
        }
        MultiverseWorld world = this.plugin.getMVWorldManager().getMVWorld(event.getBlocks().get(0).getWorld());
        // We have to do it like this due to a bug in 1.1-R3
        if (this.cancelPortalEvent(world, event.getPortalType())) {
            event.setCancelled(true);
        }
    }

    /**
     * This is called when a portal is created as the result of another world being linked.
     * @param event The event where a portal was formed due to a world link
     */
    @EventHandler
    public void portalForm(PortalCreateEvent event) {
        if (event.isCancelled() || event.getBlocks().size() == 0) {
            return;
        }
        // There's no type attribute (as of 1.1-R1), so we have to iterate.
        for (Block b : event.getBlocks()) {
            if (b.getType() == Material.PORTAL) {
                MultiverseWorld world = this.plugin.getMVWorldManager().getMVWorld(b.getWorld());
                if (this.cancelPortalEvent(world, PortalType.NETHER)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        // If We're here, then the Portal was an Ender type:
        MultiverseWorld world = this.plugin.getMVWorldManager().getMVWorld(event.getBlocks().get(0).getWorld());
        if (this.cancelPortalEvent(world, PortalType.ENDER)) {
            event.setCancelled(true);
        }
    }

    private boolean cancelPortalEvent(MultiverseWorld world, PortalType type) {
        if (world.getAllowedPortals() == AllowedPortalType.NONE) {
            return true;
        } else if (world.getAllowedPortals() != AllowedPortalType.ALL) {
            if (type != world.getAllowedPortals().getActualPortalType()) {
                return true;
            }
        }
        return false;
    }
}
