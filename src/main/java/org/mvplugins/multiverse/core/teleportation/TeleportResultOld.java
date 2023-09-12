/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.teleportation;

/**
 * An enum containing possible teleport-results.
 */
@Deprecated
public enum TeleportResultOld {
    /**
     * Insufficient permissions.
     */
    FAIL_PERMISSION,
    /**
     * The teleport was unsafe.
     */
    FAIL_UNSAFE,
    /**
     * The player was to poor.
     */
    FAIL_TOO_POOR,
    /**
     * The teleport was invalid.
     */
    FAIL_INVALID,
    /**
     * Unknown reason.
     */
    FAIL_OTHER,
    /**
     * The player was successfully teleported.
     */
    SUCCESS
}
