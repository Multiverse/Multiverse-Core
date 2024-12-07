package org.mvplugins.multiverse.core.permissions;

final class CorePermissions {
    /**
     * Permission to access a world.
     */
    static final String WORLD_ACCESS = "multiverse.access";

    /**
     * Permission to bypass the entry fee of a world.
     */
    static final String WORLD_EXEMPT = "multiverse.exempt";

    /**
     * Permission to bypass the gamemode of a world.
     */
    static final String GAMEMODE_BYPASS = "mv.bypass.gamemode";

    /**
     * Permission to bypass the player limit of a world.
     */
    static final String PLAYERLIMIT_BYPASS = "mv.bypass.playerlimit";

    /**
     * Permission to teleport to a destination.
     */
    static final String TELEPORT = "multiverse.teleport";

    private CorePermissions() {
        // Prevent instantiation as this is a static utility class
    }
}
