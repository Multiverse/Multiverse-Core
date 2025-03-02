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
import org.mvplugins.multiverse.core.world.WorldManager;

import static org.bukkit.PortalType.CUSTOM;

/**
 * A custom listener for portal related events.
 */
@Service
final class MVPortalListener implements CoreListener {

    private final MVCoreConfig config;
    private final WorldManager worldManager;

    @Inject
    MVPortalListener(@NotNull MVCoreConfig config, @NotNull WorldManager worldManager) {
        this.config = config;
        this.worldManager = worldManager;
    }

    /**
     * This is called when a portal is formed.
     *
     * @param event The event where a portal was created or formed due to a world link
     */
    @EventHandler(ignoreCancelled = true)
    public void portalCreate(PortalCreateEvent event) {
        Logging.fine("Attempting to create portal at '%s' with reason: %s",
                event.getWorld().getName(), event.getReason());

        this.worldManager.getLoadedWorld(event.getWorld()).peek(world -> {
            PortalType targetType = getPortalType(event);
            if (targetType == PortalType.CUSTOM) {
                return;
            }
            if (!world.getPortalForm().isPortalAllowed(targetType)) {
                Logging.fine("Cancelling creation of %s portal because portalForm disallows.", targetType);
                event.setCancelled(true);
            }
        }).onEmpty(() -> {
            Logging.fine("World '%s' is not managed by Multiverse! Ignoring at PortalCreateEvent.",
                    event.getWorld().getName());
        });
    }

    private PortalType getPortalType(PortalCreateEvent event) {
        return switch (event.getReason()) {
            case FIRE -> {
                // Ensure portal by flint and steel actually creates nether
                for (BlockState block : event.getBlocks()) {
                    if (block.getType() == Material.NETHER_PORTAL) {
                        yield PortalType.NETHER;
                    }
                }
                yield CUSTOM;
            }
            case NETHER_PAIR -> {
                yield PortalType.NETHER;
            }
            case END_PLATFORM -> {
                yield PortalType.ENDER;
            }
            default -> {
                Logging.fine("Portal created is not NETHER or ENDER type. Ignoring...");
                yield CUSTOM;
            }
        };
    }

    /**
     * This method will prevent ender portals from being created in worlds where they are not allowed due to portalForm.
     *
     * @param event The player interact event.
     */
    @EventHandler(ignoreCancelled = true)
    public void playerInteract(PlayerInteractEvent event) {
        if (isCreateEndPortalInteraction(event)) {
            return;
        }

        this.worldManager.getLoadedWorld(event.getPlayer().getWorld()).peek(world -> {
            if (!world.getPortalForm().isPortalAllowed(PortalType.ENDER)) {
                Logging.fine("Cancelling creation of ENDER portal because portalForm disallows.");
                event.setCancelled(true);
            }
        }).onEmpty(() -> {
            Logging.fine("World '%s' is not managed by Multiverse! Ignoring at PlayerInteractEvent.",
                    event.getPlayer().getWorld().getName());
        });
    }

    private boolean isCreateEndPortalInteraction(PlayerInteractEvent event) {
        return event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.getClickedBlock() == null
                || event.getClickedBlock().getType() != Material.END_PORTAL_FRAME
                || event.getItem() == null
                || event.getItem().getType() != Material.ENDER_EYE;
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
        if (config.isUsingCustomPortalSearch()) {
            event.setSearchRadius(config.getCustomPortalSearchRadius());
        }
    }
}
