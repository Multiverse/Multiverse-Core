/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2012.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.api.world.config;

import org.bukkit.PortalType;

/**
 * Custom enum that adds all/none for allowing portal creation.
 * @since 5.0
 */
public enum AllowedPortalType {
    /**
     * No portals are allowed.
     *
     * @since 5.0
     */
    NONE(PortalType.CUSTOM),

    /**
     * All portal types are allowed.
     *
     * @since 5.0
     */
    ALL(PortalType.CUSTOM),

    /**
     * Only Nether style portals are allowed.
     *
     * @since 5.0
     */
    NETHER(PortalType.NETHER),

    /**
     * Only Ender style portals are allowed.
     *
     * @since 5.0
     */
    END(PortalType.ENDER);

    private final PortalType type;

    AllowedPortalType(PortalType type) {
        this.type = type;
    }

    /**
     * Gets the text.
     *
     * @return The text.
     * @since 5.0
     */
    public PortalType getActualPortalType() {
        return this.type;
    }

    /**
     * Checks if the given portal type is allowed.
     *
     * @param portalType    The portal type.
     * @return True if allowed, else false.
     * @since 5.0
     */
    public boolean isPortalAllowed(PortalType portalType) {
        return this != NONE && (getActualPortalType() == portalType || this == ALL);
    }
}
