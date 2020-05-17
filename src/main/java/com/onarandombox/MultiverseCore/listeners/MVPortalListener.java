/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2012.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.logging.Level;

/**
 * A custom listener for portal related events.
 */
public class MVPortalListener implements Listener {

    private MultiverseCore plugin;

    public MVPortalListener(MultiverseCore core) {
        this.plugin = core;
    }

    /**
     * This is called when a portal is created.
     *
     * @param event The event where a portal is created.
     */
    @EventHandler(ignoreCancelled = true)
    public void portalForm(PortalCreateEvent event) {
        // as of Bukkit 1.13, this event is only fired for nether portals
        MultiverseWorld world = this.plugin.getMVWorldManager().getMVWorld(event.getWorld());
        if (world != null && !world.getAllowedPortals().isPortalAllowed(PortalType.NETHER)) {
            plugin.log(Level.FINE, "Cancelling creation of nether portal because portalForm disallows.");
            event.setCancelled(true);
        }
    }

    /**
     * This method will prevent ender portals from being created in worlds where they are not allowed due to portalForm.
     *
     * @param event The player interact event.
     */
    @EventHandler(ignoreCancelled = true)
    public void portalForm(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.END_PORTAL_FRAME) {
            return;
        }
        if (event.getItem() == null || event.getItem().getType() != Material.ENDER_EYE) {
            return;
        }
        MultiverseWorld world = this.plugin.getMVWorldManager().getMVWorld(event.getPlayer().getWorld());
        if (world != null && !world.getAllowedPortals().isPortalAllowed(PortalType.ENDER)) {
            plugin.log(Level.FINE, "Cancelling creation of ender portal because portalForm disallows.");
            event.setCancelled(true);
        }
    }
}
