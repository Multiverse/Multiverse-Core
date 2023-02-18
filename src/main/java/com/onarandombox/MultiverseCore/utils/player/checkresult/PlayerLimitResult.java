package com.onarandombox.MultiverseCore.utils.player.checkresult;

import com.onarandombox.MultiverseCore.api.action.SimpleActionResult;

public class PlayerLimitResult extends SimpleActionResult {
    /**
     * World does not have a player limit.
     */
    public static final PlayerLimitResult NO_PLAYERLIMIT = new PlayerLimitResult("NO_PLAYERLIMIT", true);

    /**
     * Player is within the player limit of the world.
     */
    public static final PlayerLimitResult WITHIN_PLAYERLIMIT = new PlayerLimitResult("WITHIN_PLAYERLIMIT", true);

    /**
     * Player has permission to bypass the player limit.
     */
    public static final PlayerLimitResult BYPASS_PLAYERLIMIT = new PlayerLimitResult("BYPASS_PLAYERLIMIT", true);

    /**
     * Player is above the player limit of the world, and cannot bypass it.
     */
    public static final PlayerLimitResult EXCEED_PLAYERLIMIT = new PlayerLimitResult("EXCEED_PLAYERLIMIT", false);

    /**
     * {@inheritDoc}
     */
    private PlayerLimitResult(String name, boolean isSuccessful) {
        super(name, isSuccessful);
    }
}
