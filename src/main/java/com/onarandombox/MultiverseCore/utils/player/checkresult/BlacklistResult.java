package com.onarandombox.MultiverseCore.utils.player.checkresult;

import com.onarandombox.MultiverseCore.api.action.SimpleActionResult;

public class BlacklistResult extends SimpleActionResult {
    /**
     * Player coming from a world that is not managed by Multiverse.
     */
    public static final BlacklistResult UNKNOWN_SOURCE_WORLD = new BlacklistResult("UNKNOWN_SOURCE_WORLD", true);

    /**
     * The player is not blacklisted.
     */
    public static final BlacklistResult NOT_BLACKLISTED = new BlacklistResult("NOT_BLACKLISTED", true);

    /**
     * The player has permission to bypass the blacklist.
     */
    public static final BlacklistResult BYPASSED_BLACKLISTED = new BlacklistResult("NOT_BLACKLISTED", true);

    /**
     * The player is blacklisted.
     */
    public static final BlacklistResult BLACKLISTED = new BlacklistResult("DENIED", false);

    /**
     * {@inheritDoc}
     */
    private BlacklistResult(String name, boolean isSuccessful) {
        super(name, isSuccessful);
    }
}
