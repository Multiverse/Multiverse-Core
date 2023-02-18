package com.onarandombox.MultiverseCore.utils.player.checkresult;

import com.onarandombox.MultiverseCore.api.action.SimpleActionResult;

public class GameModeResult extends SimpleActionResult {
    /**
     * The player's game mode should be kept.
     */
    public static final GameModeResult KEEP_GAME_MODE = new GameModeResult("KEEP_GAME_MODE", true);

    /**
     * The player has permission to bypass the game mode enforcement.
     */
    public static final GameModeResult BYPASS_ENFORCE_GAME_MODE = new GameModeResult("BYPASS_ENFORCE_GAME_MODE", true);

    /**
     * The player's game mode should be enforced.
     */
    public static final GameModeResult ENFORCE_GAME_MODE = new GameModeResult("ENFORCE_GAME_MODE", false);

    /**
     * {@inheritDoc}
     */
    private GameModeResult(String name, boolean isSuccessful) {
        super(name, isSuccessful);
    }
}
