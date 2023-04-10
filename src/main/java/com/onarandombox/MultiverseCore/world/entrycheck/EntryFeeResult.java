package com.onarandombox.MultiverseCore.world.entrycheck;

import com.onarandombox.MultiverseCore.utils.result.FailureReason;
import com.onarandombox.MultiverseCore.utils.result.SuccessReason;

public class EntryFeeResult {
    public enum Success implements SuccessReason {
        FREE_ENTRY,
        ENOUGH_MONEY,
        EXEMPT_FROM_ENTRY_FEE,
        CONSOLE_OR_BLOCK_COMMAND_SENDER
    }

    public enum Failure implements FailureReason {
        NOT_ENOUGH_MONEY,
        CANNOT_PAY_ENTRY_FEE
    }
}
