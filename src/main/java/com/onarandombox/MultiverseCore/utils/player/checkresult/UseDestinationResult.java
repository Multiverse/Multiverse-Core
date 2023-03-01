package com.onarandombox.MultiverseCore.utils.player.checkresult;

import com.onarandombox.MultiverseCore.api.operation.SimpleOperationResult;

public class UseDestinationResult extends SimpleOperationResult {
    /**
     * The result of a check if a player can use a destination.
     */
    public static final UseDestinationResult CAN_USE_DESTINATION = new UseDestinationResult("CAN_USE_DESTINATION", true);

    /**
     * Player does not have permission to use the destination.
     */
    public static final UseDestinationResult NO_DESTINATION_PERMISSION = new UseDestinationResult("NO_DESTINATION_PERMISSION", false);

    /**
     * Player does not have finer permission to use the destination.
     */
    public static final UseDestinationResult NO_DESTINATION_PERMISSION_FINER = new UseDestinationResult("NO_DESTINATION_FINER", false);

    /**
     * {@inheritDoc}
     */
    private UseDestinationResult(String name, boolean isSuccessful) {
        super(name, isSuccessful);
    }
}
