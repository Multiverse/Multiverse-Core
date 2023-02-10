package com.onarandombox.MultiverseCore.utils.actioncheck;

import com.onarandombox.MultiverseCore.api.action.ActionResult;

public enum ActionCheckResult implements ActionResult {
    NULL_WORLD(false),
    NULL_LOCATION(false),
    NULL_DESTINATION(false),

    CAN_USE_DESTINATION(true),
    NO_DESTINATION_PERMISSION(false),

    NOT_MV_WORLD(true),
    SAME_WORLD(true),

    HAS_WORLD_ACCESS(true),
    NO_WORLD_ACCESS(false),
    NO_ENFORCE_WORLD_ACCESS(true),

    ENOUGH_MONEY(true),
    NOT_ENOUGH_MONEY(false),
    EXEMPTED_FROM_ENTRY_FEE(true),
    CANNOT_PAY_ENTRY_FEE(false),
    FREE_ENTRY(true),

    NO_PLAYERLIMIT(true),
    WITHIN_PLAYERLIMIT(true),
    EXCEED_PLAYERLIMIT(false),
    BYPASS_PLAYERLIMIT(true),

    NOT_BLACKLISTED(true),
    BLACKLISTED(false),

    KEEP_GAME_MODE(true),
    ENFORCE_GAME_MODE(false),
    ;

    private final boolean isSuccessful;

    ActionCheckResult(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    @Override
    public String getName() {
        return this.name();
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    @Override
    public boolean hasResult(ActionResult result) {
        return result == this;
    }
}
