/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2012.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import jakarta.inject.Inject;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.jvnet.hk2.annotations.Service;

/**
 * A custom listener for portal related events.
 */
@Service
public class MVPortalListener implements Listener {

    private MVWorldManager worldManager;

    @Inject
    public MVPortalListener(MVWorldManager worldManager) {
        this.worldManager = worldManager;
    }

    /**
     * This is called when a portal is formed.
     * @param event The event where a portal was created or formed due to a world link
     */
    @EventHandler(ignoreCancelled = true)
    public void portalForm(PortalCreateEvent event) {
        Logging.fine("Attempting to create portal at '%s' with reason: %s", event.getWorld().getName(), event.getReason());

        MVWorld world = this.worldManager.getMVWorld(event.getWorld());
        if (world == null) {
            Logging.fine("World '%s' is not managed by Multiverse! Ignoring at PortalCreateEvent.", event.getWorld().getName());
            return;
        }

        PortalType targetType;
        switch (event.getReason()) {
            case FIRE:
                // Ensure portal by flint and steel actually creates nether
                boolean isNether = false;
                for (BlockState block : event.getBlocks()) {
                    if (block.getType() == Material.NETHER_PORTAL) {
                        isNether = true;
                        break;
                    }
                }
                if (!isNether) {
                    return;
                }
                targetType = PortalType.NETHER;
                break;
            case NETHER_PAIR:
                targetType = PortalType.NETHER;
                break;
            case END_PLATFORM:
                targetType = PortalType.ENDER;
                break;
            default:
                Logging.fine("Portal created is not NETHER or ENDER type. Ignoring...");
                return;
        }

        if (!world.getAllowedPortals().isPortalAllowed(targetType)) {
            Logging.fine("Cancelling creation of %s portal because portalForm disallows.", targetType);
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
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.END_PORTAL_FRAME) {
            return;
        }
        if (event.getItem() == null || event.getItem().getType() != Material.ENDER_EYE) {
            return;
        }

        MVWorld world = this.worldManager.getMVWorld(event.getPlayer().getWorld());
        if (world == null) {
            Logging.fine("World '%s' is not managed by Multiverse! Ignoring at PlayerInteractEvent.",
                    event.getPlayer().getWorld().getName());
            return;
        }

        if (!world.getAllowedPortals().isPortalAllowed(PortalType.ENDER)) {
            Logging.fine("Cancelling creation of ENDER portal because portalForm disallows.");
            event.setCancelled(true);
        }
    }
}
