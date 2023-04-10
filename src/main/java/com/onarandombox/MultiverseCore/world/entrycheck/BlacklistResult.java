package com.onarandombox.MultiverseCore.world.entrycheck;

import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

public class BlacklistResult {
    public enum Success implements SuccessReason {
        UNKNOWN_FROM_WORLD,
        BYPASSED_BLACKLISTED,
        NOT_BLACKLISTED
    }

    public enum Failure implements FailureReason {
        BLACKLISTED
    }
}
