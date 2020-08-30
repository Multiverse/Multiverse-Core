/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.enums;

import org.bukkit.event.world.PortalCreateEvent;

/**
 * Custom enum that adds all/none for allowing portal creation.
 */
public enum AllowedPortalType {
    /**
     * No portals are allowed.
     */
    NONE(PortalCreateEvent.CreateReason.FIRE),
    /**
     * All portal types are allowed.
     */
    ALL(PortalCreateEvent.CreateReason.FIRE),
    /**
     * Only Nether style portals are allowed.
     */
    NETHER(PortalCreateEvent.CreateReason.NETHER_PAIR),
    /**
     * Only Ender style portals are allowed.
     */
    END(PortalCreateEvent.CreateReason.END_PLATFORM);

    private PortalCreateEvent.CreateReason type;

    AllowedPortalType(PortalCreateEvent.CreateReason type) {
        this.type = type;
    }

    /**
     * Gets the text.
     * @return The text.
     */
    public PortalCreateEvent.CreateReason getActualPortalType() {
        return this.type;
    }

    public boolean isPortalAllowed(PortalCreateEvent.CreateReason portalType) {
        return this != NONE && (getActualPortalType() == portalType || this == ALL);
    }
}
