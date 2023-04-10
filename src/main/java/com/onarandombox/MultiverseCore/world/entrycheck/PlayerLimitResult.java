package com.onarandombox.MultiverseCore.world.entrycheck;

import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

public class PlayerLimitResult {
    public enum Success implements SuccessReason {
        NO_PLAYERLIMIT,
        WITHIN_PLAYERLIMIT,
        BYPASS_PLAYERLIMIT
    }

    public enum Failure implements FailureReason {
        EXCEED_PLAYERLIMIT
    }
}
