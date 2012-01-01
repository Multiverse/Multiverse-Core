/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2012.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.utils.SafeTTeleporter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.logging.Level;

public class MVPortalAdjustListener extends PlayerListener {

    private MultiverseCore plugin;

    public MVPortalAdjustListener(MultiverseCore core) {
        this.plugin = core;
    }

    @Override
    public void onPlayerPortal(PlayerPortalEvent event) {
        this.plugin.log(Level.FINE, "CALLING CORE-ADJUST!!!");
        if (event.isCancelled() || event.getFrom() == null) {
            return;
        }

        // REMEMBER! getTo MAY be NULL HERE!!!
        // If the player was actually outside of the portal, adjust the from location
        if (event.getFrom().getWorld().getBlockAt(event.getFrom()).getType() != Material.PORTAL) {
            Location newloc = SafeTTeleporter.findPortalBlockNextTo(event.getFrom());
            // TODO: Fix this. Currently, we only check for PORTAL blocks. I'll have to figure out what
            // TODO: we want to do here.
            if (newloc != null) {
                event.setFrom(newloc);
            }
        }
        // Wait for the adjust, then return!
        if (event.getTo() == null) {
            return;
        }
    }
}
