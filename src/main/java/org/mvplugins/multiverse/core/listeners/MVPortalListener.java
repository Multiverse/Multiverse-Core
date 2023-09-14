/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2012.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.listeners;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.inject.InjectableListener;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

/**
 * A custom listener for portal related events.
 */
@Service
public class MVPortalListener implements InjectableListener {

    private final MVCoreConfig config;
    private final WorldManager worldManager;

    @Inject
    MVPortalListener(@NotNull MVCoreConfig config, @NotNull WorldManager worldManager) {
        this.config = config;
        this.worldManager = worldManager;
    }

    /**
     * This is called when a portal is formed.
     * @param event The event where a portal was created or formed due to a world link
     */
    @EventHandler(ignoreCancelled = true)
    public void portalForm(PortalCreateEvent event) {
        Logging.fine("Attempting to create portal at '%s' with reason: %s", event.getWorld().getName(), event.getReason());

        LoadedMultiverseWorld world = this.worldManager.getLoadedWorld(event.getWorld()).getOrNull();
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

        if (!world.getPortalForm().isPortalAllowed(targetType)) {
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

        LoadedMultiverseWorld world = this.worldManager.getLoadedWorld(event.getPlayer().getWorld()).getOrNull();
        if (world == null) {
            Logging.fine("World '%s' is not managed by Multiverse! Ignoring at PlayerInteractEvent.",
                    event.getPlayer().getWorld().getName());
            return;
        }

        if (!world.getPortalForm().isPortalAllowed(PortalType.ENDER)) {
            Logging.fine("Cancelling creation of ENDER portal because portalForm disallows.");
            event.setCancelled(true);
        }
    }

    /**
     * Handles portal search radius adjustment.
     *
     * @param event The Event that was fired.
     */
    @EventHandler
    public void entityPortal(EntityPortalEvent event) {
        if (event.isCancelled() || event.getTo() == null) {
            return;
        }
        if (!config.isUsingCustomPortalSearch()) {
            event.setSearchRadius(config.getCustomPortalSearchRadius());
        }
    }
}
