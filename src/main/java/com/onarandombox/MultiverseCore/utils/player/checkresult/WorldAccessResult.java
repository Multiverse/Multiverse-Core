package com.onarandombox.MultiverseCore.utils.player.checkresult;

import com.onarandombox.MultiverseCore.api.operation.SimpleOperationResult;

public class WorldAccessResult extends SimpleOperationResult {
    /**
     * Enforce world access is disabled.
     */
    public static final WorldAccessResult NO_ENFORCE_WORLD_ACCESS = new WorldAccessResult("NO_ENFORCE_WORLD_ACCESS", true);

    /**
     * Player has permission to access world.
     */
    public static final WorldAccessResult HAS_WORLD_ACCESS = new WorldAccessResult("HAS_WORLD_ACCESS", true);

    /**
     * Player has permission to access world access.
     */
    public static final WorldAccessResult NO_WORLD_ACCESS = new WorldAccessResult("NO_WORLD_ACCESS", false);

    /**
     * {@inheritDoc}
     */
    private WorldAccessResult(String name, boolean isSuccessful) {
        super(name, isSuccessful);
    }
}
