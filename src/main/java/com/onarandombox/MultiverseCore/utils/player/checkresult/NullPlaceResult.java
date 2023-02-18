package com.onarandombox.MultiverseCore.utils.player.checkresult;

import com.onarandombox.MultiverseCore.api.action.ActionResult;
import com.onarandombox.MultiverseCore.api.action.SimpleActionResult;

public class NullPlaceResult extends SimpleActionResult {
    /**
     * The target destination is null.
     */
    public static final ActionResult NULL_DESTINATION = new NullPlaceResult("NULL_DESTINATION", false);

    /**
     * The target location is null.
     */
    public static final ActionResult NULL_LOCATION = new NullPlaceResult("NULL_LOCATION", false);

    /**
     * The target world is null.
     */
    public static final ActionResult NULL_WORLD = new NullPlaceResult("NULL_WORLD", false);

    /**
     * {@inheritDoc}
     */
    private NullPlaceResult(String name, boolean isSuccessful) {
        super(name, isSuccessful);
    }
}
