package org.mvplugins.multiverse.core.api.commandtools;

/**
 * Sets whether the command executor needs to confirm before executing a command.
 *
 * @since 5.0
 */
public enum ConfirmMode {
    /**
     * Every command executor needs to confirm before executing a command.
     *
     * @since 5.0
     */
    ENABLE,

    /**
     * Only players need to confirm before executing a command. Command blocks and console will not need to confirm.
     *
     * @since 5.0
     */
    PLAYER_ONLY,

    /**
     * Everyone except command blocks will not need to confirm before executing a command.
     *
     * @since 5.0
     */
    DISABLE_COMMAND_BLOCKS,

    /**
     * Everyone except console will not need to confirm before executing a command.
     *
     * @since 5.0
     */
    DISABLE_CONSOLE,

    /**
     * Everyone will not need to confirm before executing a command.
     *
     * @since 5.0
     */
    DISABLE,
}
