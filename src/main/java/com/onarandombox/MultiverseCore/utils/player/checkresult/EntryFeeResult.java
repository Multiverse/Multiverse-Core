package com.onarandombox.MultiverseCore.utils.player.checkresult;

import com.onarandombox.MultiverseCore.api.action.SimpleActionResult;

public class EntryFeeResult extends SimpleActionResult {
    /**
     * Sender is in the same world. Don't charge them.
     */
    public static final EntryFeeResult SAME_WORLD = new EntryFeeResult("SAME_WORLD", true);

    /**
     * Free to enter the world.
     */
    public static final EntryFeeResult FREE_ENTRY = new EntryFeeResult("FREE_ENTRY", true);

    /**
     * Sender has enough money to pay the entry fee.
     */
    public static final EntryFeeResult ENOUGH_MONEY = new EntryFeeResult("ENOUGH_MONEY", true);

    /**
     * Sender is exempt from paying the entry fee.
     */
    public static final EntryFeeResult EXEMPT_FROM_ENTRY_FEE = new EntryFeeResult("EXEMPT_FROM_ENTRY_FEE", true);

    /**
     * Sender does not have enough money to pay the entry fee.
     */
    public static final EntryFeeResult NOT_ENOUGH_MONEY = new EntryFeeResult("NOT_ENOUGH_MONEY", false);

    /**
     * Sender cannot pay the entry fee.
     */
    public static final EntryFeeResult CANNOT_PAY_ENTRY_FEE = new EntryFeeResult("CANNOT_PAY_ENTRY_FEE", false);

    /**
     * {@inheritDoc}
     */
    private EntryFeeResult(String name, boolean isSuccessful) {
        super(name, isSuccessful);
    }
}
