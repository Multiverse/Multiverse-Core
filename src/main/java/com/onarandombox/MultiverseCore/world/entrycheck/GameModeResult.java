package com.onarandombox.MultiverseCore.world.entrycheck;

import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

public class GameModeResult {
    public enum Success implements SuccessReason {
        ENFORCE_GAME_MODE
    }

    public enum Failure implements FailureReason {
        NO_ENFORCE_GAME_MODE,
        BYPASS_ENFORCE_GAME_MODE
    }
}
