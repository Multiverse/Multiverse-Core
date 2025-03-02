package org.mvplugins.multiverse.core.commandtools;

/**
 * Sets whether the command executor needs to confirm before executing a command.
 */
public enum ConfirmMode {
    /**
     * Every command executor needs to confirm before executing a command.
     */
    ENABLE,

    /**
     * Only players need to confirm before executing a command. Command blocks and console will not need to confirm.
     */
    PLAYER_ONLY,

    /**
     * Everyone except command blocks will not need to confirm before executing a command.
     */
    DISABLE_COMMAND_BLOCKS,

    /**
     * Everyone except console will not need to confirm before executing a command.
     */
    DISABLE_CONSOLE,

    /**
     * Everyone will not need to confirm before executing a command.
     */
    DISABLE,
}
