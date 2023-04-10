package com.onarandombox.MultiverseCore.world.entrycheck;

import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

public class WorldAccessResult {
    public enum Success implements SuccessReason {
        NO_ENFORCE_WORLD_ACCESS,
        HAS_WORLD_ACCESS
    }

    public enum Failure implements FailureReason {
        NO_WORLD_ACCESS
    }
}
